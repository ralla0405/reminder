package logan.ai.reminder.repository;

import logan.ai.reminder.entity.Reminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReminderRepositoryTest {

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
    }

    @Test
    void save_shouldPersistReminder() {
        Reminder reminder = Reminder.builder()
                .title("장보기")
                .description("우유, 빵")
                .build();

        Reminder saved = reminderRepository.save(reminder);

        assertNotNull(saved.getId());
        assertEquals("장보기", saved.getTitle());
        assertEquals("우유, 빵", saved.getDescription());
        assertFalse(saved.getCompleted());
    }

    @Test
    void findByCompleted_shouldReturnCompletedReminders() {
        Reminder r1 = Reminder.builder().title("할 일 1").build();
        Reminder r2 = Reminder.builder().title("할 일 2").build();
        r2.complete();
        reminderRepository.save(r1);
        reminderRepository.save(r2);

        List<Reminder> completed = reminderRepository.findByCompleted(true);
        List<Reminder> incomplete = reminderRepository.findByCompleted(false);

        assertEquals(1, completed.size());
        assertEquals("할 일 2", completed.getFirst().getTitle());
        assertEquals(1, incomplete.size());
        assertEquals("할 일 1", incomplete.getFirst().getTitle());
    }

    @Test
    void findByRemindAtBetween_shouldReturnTodayReminders() {
        LocalDateTime today = LocalDateTime.of(2026, 3, 16, 10, 0);
        LocalDateTime tomorrow = LocalDateTime.of(2026, 3, 17, 15, 0);

        Reminder r1 = Reminder.builder().title("오늘 할 일").remindAt(today).build();
        Reminder r2 = Reminder.builder().title("내일 할 일").remindAt(tomorrow).build();
        reminderRepository.save(r1);
        reminderRepository.save(r2);

        LocalDateTime startOfDay = LocalDateTime.of(2026, 3, 16, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2026, 3, 16, 23, 59, 59);
        List<Reminder> todayReminders = reminderRepository.findByRemindAtBetween(startOfDay, endOfDay);

        assertEquals(1, todayReminders.size());
        assertEquals("오늘 할 일", todayReminders.getFirst().getTitle());
    }

    @Test
    void findByRemindAtIsNotNull_shouldReturnScheduledReminders() {
        Reminder r1 = Reminder.builder().title("예정됨").remindAt(LocalDateTime.of(2026, 4, 1, 9, 0)).build();
        Reminder r2 = Reminder.builder().title("날짜 없음").build();
        reminderRepository.save(r1);
        reminderRepository.save(r2);

        List<Reminder> scheduled = reminderRepository.findByRemindAtIsNotNullOrderByRemindAtAsc();

        assertEquals(1, scheduled.size());
        assertEquals("예정됨", scheduled.getFirst().getTitle());
    }

    @Test
    void searchByKeyword_shouldMatchTitleAndDescription() {
        Reminder r1 = Reminder.builder().title("회의 준비").description("발표 자료").build();
        Reminder r2 = Reminder.builder().title("장보기").description("회의 후 간식 구매").build();
        Reminder r3 = Reminder.builder().title("운동").description("헬스장").build();
        reminderRepository.save(r1);
        reminderRepository.save(r2);
        reminderRepository.save(r3);

        List<Reminder> results = reminderRepository.searchByKeyword("회의");

        assertEquals(2, results.size());
    }

    @Test
    void searchByKeyword_shouldBeCaseInsensitive() {
        Reminder r1 = Reminder.builder().title("Meeting notes").build();
        reminderRepository.save(r1);

        List<Reminder> results = reminderRepository.searchByKeyword("meeting");

        assertEquals(1, results.size());
    }

    @Test
    void countMethods_shouldReturnCorrectCounts() {
        LocalDateTime today = LocalDateTime.of(2026, 3, 16, 10, 0);
        LocalDateTime future = LocalDateTime.of(2026, 4, 1, 9, 0);

        Reminder r1 = Reminder.builder().title("오늘").remindAt(today).build();
        Reminder r2 = Reminder.builder().title("미래").remindAt(future).build();
        Reminder r3 = Reminder.builder().title("완료됨").build();
        r3.complete();
        Reminder r4 = Reminder.builder().title("날짜 없음").build();
        reminderRepository.save(r1);
        reminderRepository.save(r2);
        reminderRepository.save(r3);
        reminderRepository.save(r4);

        LocalDateTime startOfDay = LocalDateTime.of(2026, 3, 16, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2026, 3, 16, 23, 59, 59);

        assertEquals(1, reminderRepository.countByCompletedFalseAndRemindAtBetween(startOfDay, endOfDay));
        assertEquals(2, reminderRepository.countByRemindAtIsNotNull());
        assertEquals(3, reminderRepository.countByCompletedFalse());
        assertEquals(1, reminderRepository.countByCompletedTrue());
    }
}

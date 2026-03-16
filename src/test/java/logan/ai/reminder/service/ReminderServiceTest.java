package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.service.ports.in.ReminderService;
import logan.ai.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReminderServiceTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
    }

    @Test
    void findAll_shouldReturnAllReminders() {
        reminderService.create("할 일 1", null, null);
        reminderService.create("할 일 2", null, null);

        List<Reminder> result = reminderService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnReminder() {
        Reminder created = reminderService.create("할 일", "메모", null);

        Reminder result = reminderService.findById(created.getId());

        assertEquals("할 일", result.getTitle());
        assertEquals("메모", result.getDescription());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderService.findById(999L));
    }

    @Test
    void findByFilter_today_shouldReturnTodayReminders() {
        LocalDateTime todayTime = LocalDate.now().atTime(10, 0);
        LocalDateTime tomorrowTime = LocalDate.now().plusDays(1).atTime(10, 0);
        reminderService.create("오늘 할 일", null, todayTime);
        reminderService.create("내일 할 일", null, tomorrowTime);

        List<Reminder> result = reminderService.findByFilter("today");

        assertEquals(1, result.size());
        assertEquals("오늘 할 일", result.getFirst().getTitle());
    }

    @Test
    void findByFilter_scheduled_shouldReturnScheduledReminders() {
        reminderService.create("예정됨", null, LocalDateTime.now().plusDays(3));
        reminderService.create("날짜 없음", null, null);

        List<Reminder> result = reminderService.findByFilter("scheduled");

        assertEquals(1, result.size());
        assertEquals("예정됨", result.getFirst().getTitle());
    }

    @Test
    void findByFilter_completed_shouldReturnCompletedReminders() {
        Reminder r1 = reminderService.create("완료될 일", null, null);
        reminderService.create("미완료", null, null);
        reminderService.toggleComplete(r1.getId());

        List<Reminder> result = reminderService.findByFilter("completed");

        assertEquals(1, result.size());
        assertEquals("완료될 일", result.getFirst().getTitle());
    }

    @Test
    void findByFilter_shouldThrowForUnknownFilter() {
        assertThrows(IllegalArgumentException.class, () -> reminderService.findByFilter("invalid"));
    }

    @Test
    void search_shouldMatchTitleAndDescription() {
        reminderService.create("회의 준비", "발표 자료", null);
        reminderService.create("장보기", "회의 후 간식", null);
        reminderService.create("운동", "헬스장", null);

        List<Reminder> result = reminderService.search("회의");

        assertEquals(2, result.size());
    }

    @Test
    void getCounts_shouldReturnCorrectCounts() {
        LocalDateTime todayTime = LocalDate.now().atTime(10, 0);
        LocalDateTime futureTime = LocalDate.now().plusDays(5).atTime(9, 0);

        reminderService.create("오늘", null, todayTime);
        reminderService.create("미래", null, futureTime);
        Reminder completed = reminderService.create("완료됨", null, null);
        reminderService.toggleComplete(completed.getId());
        reminderService.create("날짜 없음", null, null);

        Map<String, Long> counts = reminderService.getCounts();

        assertEquals(1L, counts.get("today"));
        assertEquals(2L, counts.get("scheduled"));
        assertEquals(3L, counts.get("all"));
        assertEquals(1L, counts.get("completed"));
    }

    @Test
    void create_shouldPersistReminder() {
        LocalDateTime remindAt = LocalDateTime.of(2026, 3, 20, 14, 0);

        Reminder result = reminderService.create("새 할 일", "메모", remindAt);

        assertNotNull(result.getId());
        assertEquals("새 할 일", result.getTitle());
        assertEquals("메모", result.getDescription());
        assertEquals(remindAt, result.getRemindAt());
        assertFalse(result.getCompleted());
    }

    @Test
    void update_shouldModifyReminder() {
        Reminder created = reminderService.create("원래 제목", null, null);
        LocalDateTime newRemindAt = LocalDateTime.of(2026, 4, 1, 10, 0);

        Reminder result = reminderService.update(created.getId(), "수정된 제목", "메모 추가", newRemindAt);

        assertEquals("수정된 제목", result.getTitle());
        assertEquals("메모 추가", result.getDescription());
        assertEquals(newRemindAt, result.getRemindAt());
    }

    @Test
    void toggleComplete_shouldCompleteWhenIncomplete() {
        Reminder created = reminderService.create("할 일", null, null);

        Reminder result = reminderService.toggleComplete(created.getId());

        assertTrue(result.getCompleted());
    }

    @Test
    void toggleComplete_shouldUncompleteWhenCompleted() {
        Reminder created = reminderService.create("할 일", null, null);
        reminderService.toggleComplete(created.getId());

        Reminder result = reminderService.toggleComplete(created.getId());

        assertFalse(result.getCompleted());
    }

    @Test
    void delete_shouldRemoveReminder() {
        Reminder created = reminderService.create("삭제할 일", null, null);
        Long id = created.getId();

        reminderService.delete(id);

        assertThrows(NoSuchElementException.class, () -> reminderService.findById(id));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderService.delete(999L));
    }
}

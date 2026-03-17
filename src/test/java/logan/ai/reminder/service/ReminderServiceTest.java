package logan.ai.reminder.service;

import logan.ai.reminder.entity.Priority;
import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;
import logan.ai.reminder.service.ports.in.ReminderService;
import logan.ai.reminder.repository.ReminderListRepository;
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

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
    }

    @Test
    void findAll_shouldReturnAllReminders() {
        reminderService.create("할 일 1", null, null, null, null);
        reminderService.create("할 일 2", null, null, null, null);

        List<Reminder> result = reminderService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnReminder() {
        Reminder created = reminderService.create("할 일", "메모", null, null, null);

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
        reminderService.create("오늘 할 일", null, todayTime, null, null);
        reminderService.create("내일 할 일", null, tomorrowTime, null, null);

        List<Reminder> result = reminderService.findByFilter("today");

        assertEquals(1, result.size());
        assertEquals("오늘 할 일", result.getFirst().getTitle());
    }

    @Test
    void findByFilter_scheduled_shouldReturnScheduledReminders() {
        reminderService.create("예정됨", null, LocalDateTime.now().plusDays(3), null, null);
        reminderService.create("날짜 없음", null, null, null, null);

        List<Reminder> result = reminderService.findByFilter("scheduled");

        assertEquals(1, result.size());
        assertEquals("예정됨", result.getFirst().getTitle());
    }

    @Test
    void findByFilter_completed_shouldReturnCompletedReminders() {
        Reminder r1 = reminderService.create("완료될 일", null, null, null, null);
        reminderService.create("미완료", null, null, null, null);
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
        reminderService.create("회의 준비", "발표 자료", null, null, null);
        reminderService.create("장보기", "회의 후 간식", null, null, null);
        reminderService.create("운동", "헬스장", null, null, null);

        List<Reminder> result = reminderService.search("회의");

        assertEquals(2, result.size());
    }

    @Test
    void getCounts_shouldReturnCorrectCounts() {
        LocalDateTime todayTime = LocalDate.now().atTime(10, 0);
        LocalDateTime futureTime = LocalDate.now().plusDays(5).atTime(9, 0);

        reminderService.create("오늘", null, todayTime, null, null);
        reminderService.create("미래", null, futureTime, null, null);
        Reminder completed = reminderService.create("완료됨", null, null, null, null);
        reminderService.toggleComplete(completed.getId());
        reminderService.create("날짜 없음", null, null, null, null);

        Map<String, Long> counts = reminderService.getCounts();

        assertEquals(1L, counts.get("today"));
        assertEquals(2L, counts.get("scheduled"));
        assertEquals(3L, counts.get("all"));
        assertEquals(1L, counts.get("completed"));
    }

    @Test
    void create_shouldPersistReminder() {
        LocalDateTime remindAt = LocalDateTime.of(2026, 3, 20, 14, 0);

        Reminder result = reminderService.create("새 할 일", "메모", remindAt, null, null);

        assertNotNull(result.getId());
        assertEquals("새 할 일", result.getTitle());
        assertEquals("메모", result.getDescription());
        assertEquals(remindAt, result.getRemindAt());
        assertFalse(result.getCompleted());
    }

    @Test
    void create_withPriorityAndList_shouldPersist() {
        ReminderList list = reminderListRepository.save(
                ReminderList.builder().name("업무").color("#FF3B30").build());

        Reminder result = reminderService.create("중요 할 일", "설명", null, Priority.HIGH, list.getId());

        assertEquals(Priority.HIGH, result.getPriority());
        assertNotNull(result.getReminderList());
        assertEquals("업무", result.getReminderList().getName());
    }

    @Test
    void update_shouldModifyReminder() {
        Reminder created = reminderService.create("원래 제목", null, null, null, null);
        LocalDateTime newRemindAt = LocalDateTime.of(2026, 4, 1, 10, 0);

        Reminder result = reminderService.update(created.getId(), "수정된 제목", "메모 추가", newRemindAt, null, null);

        assertEquals("수정된 제목", result.getTitle());
        assertEquals("메모 추가", result.getDescription());
        assertEquals(newRemindAt, result.getRemindAt());
    }

    @Test
    void update_withPriorityAndList_shouldModify() {
        Reminder created = reminderService.create("할 일", null, null, null, null);
        ReminderList list = reminderListRepository.save(
                ReminderList.builder().name("개인").color("#007AFF").build());

        Reminder result = reminderService.update(created.getId(), "할 일", null, null, Priority.MEDIUM, list.getId());

        assertEquals(Priority.MEDIUM, result.getPriority());
        assertNotNull(result.getReminderList());
        assertEquals("개인", result.getReminderList().getName());
    }

    @Test
    void toggleComplete_shouldCompleteWhenIncomplete() {
        Reminder created = reminderService.create("할 일", null, null, null, null);

        Reminder result = reminderService.toggleComplete(created.getId());

        assertTrue(result.getCompleted());
    }

    @Test
    void toggleComplete_shouldUncompleteWhenCompleted() {
        Reminder created = reminderService.create("할 일", null, null, null, null);
        reminderService.toggleComplete(created.getId());

        Reminder result = reminderService.toggleComplete(created.getId());

        assertFalse(result.getCompleted());
    }

    @Test
    void delete_shouldRemoveReminder() {
        Reminder created = reminderService.create("삭제할 일", null, null, null, null);
        Long id = created.getId();

        reminderService.delete(id);

        assertThrows(NoSuchElementException.class, () -> reminderService.findById(id));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderService.delete(999L));
    }
}

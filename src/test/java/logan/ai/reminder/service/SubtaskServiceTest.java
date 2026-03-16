package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.Subtask;
import logan.ai.reminder.repository.ReminderRepository;
import logan.ai.reminder.repository.SubtaskRepository;
import logan.ai.reminder.service.ports.in.SubtaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SubtaskServiceTest {

    @Autowired
    private SubtaskService subtaskService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    private Reminder reminder;

    @BeforeEach
    void setUp() {
        subtaskRepository.deleteAll();
        reminderRepository.deleteAll();
        reminder = reminderRepository.save(Reminder.builder().title("부모 리마인더").build());
    }

    @Test
    void create_shouldPersistSubtask() {
        Subtask result = subtaskService.create(reminder.getId(), "하위 작업", 1);

        assertNotNull(result.getId());
        assertEquals("하위 작업", result.getTitle());
        assertFalse(result.getCompleted());
        assertEquals(1, result.getSortOrder());
    }

    @Test
    void create_shouldThrowWhenReminderNotFound() {
        assertThrows(NoSuchElementException.class, () -> subtaskService.create(999L, "작업", null));
    }

    @Test
    void findByReminderId_shouldReturnOrderedSubtasks() {
        subtaskService.create(reminder.getId(), "작업 B", 2);
        subtaskService.create(reminder.getId(), "작업 A", 1);

        List<Subtask> result = subtaskService.findByReminderId(reminder.getId());

        assertEquals(2, result.size());
        assertEquals("작업 A", result.get(0).getTitle());
        assertEquals("작업 B", result.get(1).getTitle());
    }

    @Test
    void update_shouldModifySubtask() {
        Subtask created = subtaskService.create(reminder.getId(), "원래", 0);

        Subtask result = subtaskService.update(reminder.getId(), created.getId(), "수정됨", 5);

        assertEquals("수정됨", result.getTitle());
        assertEquals(5, result.getSortOrder());
    }

    @Test
    void toggleComplete_shouldToggle() {
        Subtask created = subtaskService.create(reminder.getId(), "작업", null);

        Subtask completed = subtaskService.toggleComplete(reminder.getId(), created.getId());
        assertTrue(completed.getCompleted());

        Subtask uncompleted = subtaskService.toggleComplete(reminder.getId(), created.getId());
        assertFalse(uncompleted.getCompleted());
    }

    @Test
    void delete_shouldRemoveSubtask() {
        Subtask created = subtaskService.create(reminder.getId(), "삭제할 작업", null);
        Long subtaskId = created.getId();

        subtaskService.delete(reminder.getId(), subtaskId);

        assertThrows(NoSuchElementException.class,
                () -> subtaskService.toggleComplete(reminder.getId(), subtaskId));
    }

    @Test
    void shouldThrowWhenSubtaskBelongsToDifferentReminder() {
        Reminder other = reminderRepository.save(Reminder.builder().title("다른 리마인더").build());
        Subtask created = subtaskService.create(other.getId(), "다른 작업", null);

        assertThrows(NoSuchElementException.class,
                () -> subtaskService.update(reminder.getId(), created.getId(), "변경", 0));
    }
}

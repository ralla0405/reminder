package logan.ai.reminder.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void builder_shouldSetDefaultValues() {
        Reminder reminder = Reminder.builder().title("부모").build();
        Subtask subtask = Subtask.builder()
                .title("하위 작업")
                .reminder(reminder)
                .build();

        assertEquals("하위 작업", subtask.getTitle());
        assertFalse(subtask.getCompleted());
        assertEquals(0, subtask.getSortOrder());
        assertEquals(reminder, subtask.getReminder());
    }

    @Test
    void toggleComplete_shouldToggle() {
        Reminder reminder = Reminder.builder().title("부모").build();
        Subtask subtask = Subtask.builder().title("작업").reminder(reminder).build();

        subtask.toggleComplete();
        assertTrue(subtask.getCompleted());

        subtask.toggleComplete();
        assertFalse(subtask.getCompleted());
    }

    @Test
    void update_shouldChangeFields() {
        Reminder reminder = Reminder.builder().title("부모").build();
        Subtask subtask = Subtask.builder().title("원래").reminder(reminder).sortOrder(0).build();

        subtask.update("수정됨", 3);

        assertEquals("수정됨", subtask.getTitle());
        assertEquals(3, subtask.getSortOrder());
    }
}

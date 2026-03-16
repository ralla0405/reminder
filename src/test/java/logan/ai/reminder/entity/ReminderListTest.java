package logan.ai.reminder.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReminderListTest {

    @Test
    void builder_shouldSetDefaultColor() {
        ReminderList list = ReminderList.builder()
                .name("업무")
                .build();

        assertEquals("업무", list.getName());
        assertEquals("#007AFF", list.getColor());
        assertNotNull(list.getCreatedAt());
        assertTrue(list.getReminders().isEmpty());
    }

    @Test
    void builder_shouldSetCustomColor() {
        ReminderList list = ReminderList.builder()
                .name("개인")
                .color("#FF3B30")
                .build();

        assertEquals("#FF3B30", list.getColor());
    }

    @Test
    void update_shouldChangNameAndColor() {
        ReminderList list = ReminderList.builder()
                .name("원래 이름")
                .build();

        list.update("수정 이름", "#34C759");

        assertEquals("수정 이름", list.getName());
        assertEquals("#34C759", list.getColor());
    }
}

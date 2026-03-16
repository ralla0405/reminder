package logan.ai.reminder.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReminderTest {

    @Test
    void builder_shouldSetDefaultValues() {
        Reminder reminder = Reminder.builder()
                .title("테스트")
                .build();

        assertEquals("테스트", reminder.getTitle());
        assertFalse(reminder.getCompleted());
        assertNotNull(reminder.getCreatedAt());
        assertNull(reminder.getDescription());
        assertNull(reminder.getRemindAt());
        assertEquals(Priority.NONE, reminder.getPriority());
    }

    @Test
    void builder_shouldSetAllFields() {
        LocalDateTime remindAt = LocalDateTime.of(2026, 3, 20, 14, 0);

        Reminder reminder = Reminder.builder()
                .title("회의 준비")
                .description("발표 자료 정리")
                .remindAt(remindAt)
                .build();

        assertEquals("회의 준비", reminder.getTitle());
        assertEquals("발표 자료 정리", reminder.getDescription());
        assertEquals(remindAt, reminder.getRemindAt());
        assertFalse(reminder.getCompleted());
        assertNotNull(reminder.getCreatedAt());
    }

    @Test
    void complete_shouldSetCompletedTrue() {
        Reminder reminder = Reminder.builder()
                .title("할 일")
                .build();

        reminder.complete();

        assertTrue(reminder.getCompleted());
    }

    @Test
    void uncomplete_shouldSetCompletedFalse() {
        Reminder reminder = Reminder.builder()
                .title("할 일")
                .build();

        reminder.complete();
        reminder.uncomplete();

        assertFalse(reminder.getCompleted());
    }

    @Test
    void update_shouldChangeFields() {
        Reminder reminder = Reminder.builder()
                .title("원래 제목")
                .build();

        LocalDateTime newRemindAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        reminder.update("수정된 제목", "메모 추가", newRemindAt);

        assertEquals("수정된 제목", reminder.getTitle());
        assertEquals("메모 추가", reminder.getDescription());
        assertEquals(newRemindAt, reminder.getRemindAt());
    }

    @Test
    void update_shouldNotChangeCompletedOrCreatedAt() {
        Reminder reminder = Reminder.builder()
                .title("원래 제목")
                .build();

        LocalDateTime originalCreatedAt = reminder.getCreatedAt();
        reminder.complete();
        reminder.update("수정", null, null);

        assertTrue(reminder.getCompleted());
        assertEquals(originalCreatedAt, reminder.getCreatedAt());
    }

    @Test
    void priority_shouldBeSetByBuilder() {
        Reminder reminder = Reminder.builder()
                .title("긴급")
                .priority(Priority.HIGH)
                .build();

        assertEquals(Priority.HIGH, reminder.getPriority());
    }

    @Test
    void updatePriority_shouldChangePriority() {
        Reminder reminder = Reminder.builder()
                .title("할 일")
                .build();

        reminder.updatePriority(Priority.MEDIUM);

        assertEquals(Priority.MEDIUM, reminder.getPriority());
    }
}
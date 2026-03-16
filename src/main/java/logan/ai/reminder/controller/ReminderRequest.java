package logan.ai.reminder.controller;

import java.time.LocalDateTime;

public record ReminderRequest(
        String title,
        String description,
        LocalDateTime remindAt
) {
}

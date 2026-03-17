package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Priority;

import java.time.LocalDateTime;

public record ReminderRequest(
        String title,
        String description,
        LocalDateTime remindAt,
        Priority priority,
        Long reminderListId
) {
}

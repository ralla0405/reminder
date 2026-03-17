package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Priority;
import logan.ai.reminder.entity.Reminder;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        String title,
        String description,
        LocalDateTime remindAt,
        Boolean completed,
        Priority priority,
        Long reminderListId,
        String reminderListName,
        LocalDateTime createdAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemindAt(),
                reminder.getCompleted(),
                reminder.getPriority(),
                reminder.getReminderList() != null ? reminder.getReminderList().getId() : null,
                reminder.getReminderList() != null ? reminder.getReminderList().getName() : null,
                reminder.getCreatedAt()
        );
    }
}

package logan.ai.reminder.controller;

import logan.ai.reminder.entity.ReminderList;

import java.time.LocalDateTime;

public record ReminderListResponse(
        Long id,
        String name,
        String color,
        LocalDateTime createdAt
) {
    public static ReminderListResponse from(ReminderList list) {
        return new ReminderListResponse(
                list.getId(),
                list.getName(),
                list.getColor(),
                list.getCreatedAt()
        );
    }
}

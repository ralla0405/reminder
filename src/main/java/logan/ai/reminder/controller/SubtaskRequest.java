package logan.ai.reminder.controller;

public record SubtaskRequest(
        String title,
        Integer sortOrder
) {
}

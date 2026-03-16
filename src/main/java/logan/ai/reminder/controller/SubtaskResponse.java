package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Subtask;

public record SubtaskResponse(
        Long id,
        String title,
        Boolean completed,
        Integer sortOrder
) {
    public static SubtaskResponse from(Subtask subtask) {
        return new SubtaskResponse(
                subtask.getId(),
                subtask.getTitle(),
                subtask.getCompleted(),
                subtask.getSortOrder()
        );
    }
}

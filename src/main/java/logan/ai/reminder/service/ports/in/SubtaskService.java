package logan.ai.reminder.service.ports.in;

import logan.ai.reminder.entity.Subtask;

import java.util.List;

public interface SubtaskService {

    List<Subtask> findByReminderId(Long reminderId);

    Subtask create(Long reminderId, String title, Integer sortOrder);

    Subtask update(Long reminderId, Long subtaskId, String title, Integer sortOrder);

    Subtask toggleComplete(Long reminderId, Long subtaskId);

    void delete(Long reminderId, Long subtaskId);
}

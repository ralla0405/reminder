package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.Subtask;
import logan.ai.reminder.repository.ReminderRepository;
import logan.ai.reminder.repository.SubtaskRepository;
import logan.ai.reminder.service.ports.in.SubtaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultSubtaskService implements SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<Subtask> findByReminderId(Long reminderId) {
        findReminder(reminderId);
        return subtaskRepository.findByReminderIdOrderBySortOrderAsc(reminderId);
    }

    @Override
    @Transactional
    public Subtask create(Long reminderId, String title, Integer sortOrder) {
        Reminder reminder = findReminder(reminderId);
        Subtask subtask = Subtask.builder()
                .title(title)
                .reminder(reminder)
                .sortOrder(sortOrder)
                .build();
        return subtaskRepository.save(subtask);
    }

    @Override
    @Transactional
    public Subtask update(Long reminderId, Long subtaskId, String title, Integer sortOrder) {
        Subtask subtask = findSubtask(reminderId, subtaskId);
        subtask.update(title, sortOrder);
        return subtask;
    }

    @Override
    @Transactional
    public Subtask toggleComplete(Long reminderId, Long subtaskId) {
        Subtask subtask = findSubtask(reminderId, subtaskId);
        subtask.toggleComplete();
        return subtask;
    }

    @Override
    @Transactional
    public void delete(Long reminderId, Long subtaskId) {
        Subtask subtask = findSubtask(reminderId, subtaskId);
        subtaskRepository.delete(subtask);
    }

    private Reminder findReminder(Long reminderId) {
        return reminderRepository.findById(reminderId)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found: " + reminderId));
    }

    private Subtask findSubtask(Long reminderId, Long subtaskId) {
        findReminder(reminderId);
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new NoSuchElementException("Subtask not found: " + subtaskId));
        if (!subtask.getReminder().getId().equals(reminderId)) {
            throw new NoSuchElementException("Subtask " + subtaskId + " does not belong to Reminder " + reminderId);
        }
        return subtask;
    }
}

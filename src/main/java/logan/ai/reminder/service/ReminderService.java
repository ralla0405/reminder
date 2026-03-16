package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public List<Reminder> findAll() {
        return reminderRepository.findAll();
    }

    public Reminder findById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found: " + id));
    }

    @Transactional
    public Reminder create(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder update(Long id, Reminder updated) {
        Reminder reminder = findById(id);
        reminder.setTitle(updated.getTitle());
        reminder.setDescription(updated.getDescription());
        reminder.setRemindAt(updated.getRemindAt());
        reminder.setCompleted(updated.getCompleted());
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder complete(Long id) {
        Reminder reminder = findById(id);
        reminder.setCompleted(true);
        return reminderRepository.save(reminder);
    }

    @Transactional
    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }
}

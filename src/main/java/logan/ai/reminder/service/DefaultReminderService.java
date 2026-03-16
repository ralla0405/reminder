package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.service.ports.in.ReminderService;
import logan.ai.reminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public List<Reminder> findAll() {
        return reminderRepository.findAll();
    }

    @Override
    public Reminder findById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found: " + id));
    }

    @Override
    public List<Reminder> findByFilter(String filter) {
        return switch (filter) {
            case "today" -> {
                LocalDateTime start = LocalDate.now().atStartOfDay();
                LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
                yield reminderRepository.findByRemindAtBetween(start, end);
            }
            case "scheduled" -> reminderRepository.findByRemindAtIsNotNullOrderByRemindAtAsc();
            case "completed" -> reminderRepository.findByCompleted(true);
            default -> throw new IllegalArgumentException("Unknown filter: " + filter);
        };
    }

    @Override
    public List<Reminder> search(String keyword) {
        return reminderRepository.searchByKeyword(keyword);
    }

    @Override
    public Map<String, Long> getCounts() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return Map.of(
                "today", reminderRepository.countByCompletedFalseAndRemindAtBetween(start, end),
                "scheduled", reminderRepository.countByRemindAtIsNotNull(),
                "all", reminderRepository.countByCompletedFalse(),
                "completed", reminderRepository.countByCompletedTrue()
        );
    }

    @Override
    @Transactional
    public Reminder create(String title, String description, LocalDateTime remindAt) {
        Reminder reminder = Reminder.builder()
                .title(title)
                .description(description)
                .remindAt(remindAt)
                .build();
        return reminderRepository.save(reminder);
    }

    @Override
    @Transactional
    public Reminder update(Long id, String title, String description, LocalDateTime remindAt) {
        Reminder reminder = findById(id);
        reminder.update(title, description, remindAt);
        return reminder;
    }

    @Override
    @Transactional
    public Reminder toggleComplete(Long id) {
        Reminder reminder = findById(id);
        if (reminder.getCompleted()) {
            reminder.uncomplete();
        } else {
            reminder.complete();
        }
        return reminder;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reminder reminder = findById(id);
        reminderRepository.delete(reminder);
    }
}

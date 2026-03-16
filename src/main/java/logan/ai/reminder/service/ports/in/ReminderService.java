package logan.ai.reminder.service.ports.in;

import logan.ai.reminder.entity.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReminderService {

    List<Reminder> findAll();

    Reminder findById(Long id);

    List<Reminder> findByFilter(String filter);

    List<Reminder> search(String keyword);

    Map<String, Long> getCounts();

    Reminder create(String title, String description, LocalDateTime remindAt);

    Reminder update(Long id, String title, String description, LocalDateTime remindAt);

    Reminder toggleComplete(Long id);

    void delete(Long id);
}
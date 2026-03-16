package logan.ai.reminder.service.ports.in;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;

import java.util.List;

public interface ReminderListService {

    List<ReminderList> findAll();

    ReminderList findById(Long id);

    ReminderList create(String name, String color);

    ReminderList update(Long id, String name, String color);

    void delete(Long id);

    List<Reminder> findRemindersByListId(Long listId);
}

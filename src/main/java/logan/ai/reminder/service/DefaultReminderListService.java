package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;
import logan.ai.reminder.repository.ReminderListRepository;
import logan.ai.reminder.repository.ReminderRepository;
import logan.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<ReminderList> findAll() {
        return reminderListRepository.findAll();
    }

    @Override
    public ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReminderList not found: " + id));
    }

    @Override
    @Transactional
    public ReminderList create(String name, String color) {
        ReminderList list = ReminderList.builder()
                .name(name)
                .color(color)
                .build();
        return reminderListRepository.save(list);
    }

    @Override
    @Transactional
    public ReminderList update(Long id, String name, String color) {
        ReminderList list = findById(id);
        list.update(name, color);
        return list;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReminderList list = findById(id);
        reminderListRepository.delete(list);
    }

    @Override
    public List<Reminder> findRemindersByListId(Long listId) {
        findById(listId);
        return reminderRepository.findByReminderListId(listId);
    }
}

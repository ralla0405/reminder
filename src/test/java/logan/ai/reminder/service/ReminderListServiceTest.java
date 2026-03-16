package logan.ai.reminder.service;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;
import logan.ai.reminder.repository.ReminderListRepository;
import logan.ai.reminder.repository.ReminderRepository;
import logan.ai.reminder.service.ports.in.ReminderListService;
import logan.ai.reminder.service.ports.in.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReminderListServiceTest {

    @Autowired
    private ReminderListService reminderListService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
    }

    @Test
    void create_shouldPersistList() {
        ReminderList list = reminderListService.create("업무", "#FF3B30");

        assertNotNull(list.getId());
        assertEquals("업무", list.getName());
        assertEquals("#FF3B30", list.getColor());
    }

    @Test
    void create_withNullColor_shouldUseDefault() {
        ReminderList list = reminderListService.create("개인", null);

        assertEquals("#007AFF", list.getColor());
    }

    @Test
    void findAll_shouldReturnAllLists() {
        reminderListService.create("업무", null);
        reminderListService.create("개인", null);

        List<ReminderList> result = reminderListService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnList() {
        ReminderList created = reminderListService.create("업무", null);

        ReminderList result = reminderListService.findById(created.getId());

        assertEquals("업무", result.getName());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderListService.findById(999L));
    }

    @Test
    void update_shouldModifyList() {
        ReminderList created = reminderListService.create("원래 이름", "#007AFF");

        ReminderList result = reminderListService.update(created.getId(), "수정 이름", "#34C759");

        assertEquals("수정 이름", result.getName());
        assertEquals("#34C759", result.getColor());
    }

    @Test
    void delete_shouldRemoveList() {
        ReminderList created = reminderListService.create("삭제할 리스트", null);
        Long id = created.getId();

        reminderListService.delete(id);

        assertThrows(NoSuchElementException.class, () -> reminderListService.findById(id));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderListService.delete(999L));
    }

    @Test
    void findRemindersByListId_shouldReturnRemindersInList() {
        ReminderList list = reminderListService.create("업무", null);
        reminderRepository.save(Reminder.builder().title("할 일 1").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("할 일 2").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("리스트 없음").build());

        List<Reminder> result = reminderListService.findRemindersByListId(list.getId());

        assertEquals(2, result.size());
    }

    @Test
    void findRemindersByListId_shouldThrowWhenListNotFound() {
        assertThrows(NoSuchElementException.class, () -> reminderListService.findRemindersByListId(999L));
    }
}

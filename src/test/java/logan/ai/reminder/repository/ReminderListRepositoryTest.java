package logan.ai.reminder.repository;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReminderListRepositoryTest {

    @Autowired
    private ReminderListRepository reminderListRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
    }

    @Test
    void save_shouldPersistReminderList() {
        ReminderList list = ReminderList.builder()
                .name("업무")
                .color("#FF3B30")
                .build();

        ReminderList saved = reminderListRepository.save(list);

        assertNotNull(saved.getId());
        assertEquals("업무", saved.getName());
        assertEquals("#FF3B30", saved.getColor());
    }

    @Test
    void findByReminderListId_shouldReturnRemindersInList() {
        ReminderList list = reminderListRepository.save(
                ReminderList.builder().name("업무").build());

        reminderRepository.save(Reminder.builder().title("할 일 1").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("할 일 2").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("리스트 없음").build());

        List<Reminder> result = reminderRepository.findByReminderListId(list.getId());

        assertEquals(2, result.size());
    }
}

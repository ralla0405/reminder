package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.ReminderList;
import logan.ai.reminder.repository.ReminderListRepository;
import logan.ai.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderListControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void getLists_shouldReturnAll() throws Exception {
        reminderListRepository.save(ReminderList.builder().name("업무").build());
        reminderListRepository.save(ReminderList.builder().name("개인").build());

        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getList_shouldReturnOne() throws Exception {
        ReminderList saved = reminderListRepository.save(
                ReminderList.builder().name("업무").color("#FF3B30").build());

        mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("업무"))
                .andExpect(jsonPath("$.color").value("#FF3B30"));
    }

    @Test
    void getList_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/lists/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRemindersByList_shouldReturnRemindersInList() throws Exception {
        ReminderList list = reminderListRepository.save(
                ReminderList.builder().name("업무").build());
        reminderRepository.save(Reminder.builder().title("할 일 1").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("할 일 2").reminderList(list).build());
        reminderRepository.save(Reminder.builder().title("리스트 없음").build());

        mockMvc.perform(get("/api/lists/{id}/reminders", list.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getRemindersByList_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/lists/{id}/reminders", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createList_shouldReturn201() throws Exception {
        String body = """
                {
                    "name": "쇼핑",
                    "color": "#5856D6"
                }
                """;

        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("쇼핑"))
                .andExpect(jsonPath("$.color").value("#5856D6"));
    }

    @Test
    void updateList_shouldReturnUpdated() throws Exception {
        ReminderList saved = reminderListRepository.save(
                ReminderList.builder().name("원래 이름").build());
        String body = """
                {
                    "name": "수정 이름",
                    "color": "#34C759"
                }
                """;

        mockMvc.perform(put("/api/lists/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정 이름"))
                .andExpect(jsonPath("$.color").value("#34C759"));
    }

    @Test
    void deleteList_shouldReturn204() throws Exception {
        ReminderList saved = reminderListRepository.save(
                ReminderList.builder().name("삭제할 리스트").build());

        mockMvc.perform(delete("/api/lists/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}

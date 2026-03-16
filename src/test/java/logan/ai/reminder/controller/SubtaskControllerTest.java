package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.entity.Subtask;
import logan.ai.reminder.repository.ReminderRepository;
import logan.ai.reminder.repository.SubtaskRepository;
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
class SubtaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    private Reminder reminder;

    @BeforeEach
    void setUp() {
        subtaskRepository.deleteAll();
        reminderRepository.deleteAll();
        reminder = reminderRepository.save(Reminder.builder().title("부모 리마인더").build());
    }

    @Test
    void getSubtasks_shouldReturnAll() throws Exception {
        subtaskRepository.save(Subtask.builder().title("작업 1").reminder(reminder).sortOrder(1).build());
        subtaskRepository.save(Subtask.builder().title("작업 2").reminder(reminder).sortOrder(2).build());

        mockMvc.perform(get("/api/reminders/{reminderId}/subtasks", reminder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getSubtasks_reminderNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/reminders/{reminderId}/subtasks", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubtask_shouldReturn201() throws Exception {
        String body = """
                {
                    "title": "새 하위 작업",
                    "sortOrder": 1
                }
                """;

        mockMvc.perform(post("/api/reminders/{reminderId}/subtasks", reminder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("새 하위 작업"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.sortOrder").value(1));
    }

    @Test
    void updateSubtask_shouldReturnUpdated() throws Exception {
        Subtask saved = subtaskRepository.save(
                Subtask.builder().title("원래").reminder(reminder).sortOrder(0).build());
        String body = """
                {
                    "title": "수정됨",
                    "sortOrder": 3
                }
                """;

        mockMvc.perform(put("/api/reminders/{reminderId}/subtasks/{subtaskId}",
                        reminder.getId(), saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정됨"))
                .andExpect(jsonPath("$.sortOrder").value(3));
    }

    @Test
    void toggleComplete_shouldToggle() throws Exception {
        Subtask saved = subtaskRepository.save(
                Subtask.builder().title("작업").reminder(reminder).build());

        mockMvc.perform(patch("/api/reminders/{reminderId}/subtasks/{subtaskId}/complete",
                        reminder.getId(), saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteSubtask_shouldReturn204() throws Exception {
        Subtask saved = subtaskRepository.save(
                Subtask.builder().title("삭제할 작업").reminder(reminder).build());

        mockMvc.perform(delete("/api/reminders/{reminderId}/subtasks/{subtaskId}",
                        reminder.getId(), saved.getId()))
                .andExpect(status().isNoContent());
    }
}

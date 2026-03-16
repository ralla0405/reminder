package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
    }

    @Test
    void getReminders_shouldReturnAll() throws Exception {
        reminderRepository.save(Reminder.builder().title("할 일 1").build());
        reminderRepository.save(Reminder.builder().title("할 일 2").build());

        mockMvc.perform(get("/api/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getReminders_withFilter_shouldReturnFiltered() throws Exception {
        Reminder r = Reminder.builder().title("완료됨").build();
        r.complete();
        reminderRepository.save(r);
        reminderRepository.save(Reminder.builder().title("미완료").build());

        mockMvc.perform(get("/api/reminders").param("filter", "completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("완료됨"));
    }

    @Test
    void getReminders_withSearch_shouldReturnMatched() throws Exception {
        reminderRepository.save(Reminder.builder().title("회의 준비").build());
        reminderRepository.save(Reminder.builder().title("운동").build());

        mockMvc.perform(get("/api/reminders").param("search", "회의"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("회의 준비"));
    }

    @Test
    void getCounts_shouldReturnAllCounts() throws Exception {
        LocalDateTime todayTime = LocalDate.now().atTime(10, 0);
        reminderRepository.save(Reminder.builder().title("오늘").remindAt(todayTime).build());
        Reminder completed = Reminder.builder().title("완료").build();
        completed.complete();
        reminderRepository.save(completed);

        mockMvc.perform(get("/api/reminders/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.today").value(1))
                .andExpect(jsonPath("$.completed").value(1));
    }

    @Test
    void getReminder_shouldReturnOne() throws Exception {
        Reminder saved = reminderRepository.save(Reminder.builder().title("할 일").description("메모").build());

        mockMvc.perform(get("/api/reminders/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("할 일"))
                .andExpect(jsonPath("$.description").value("메모"));
    }

    @Test
    void getReminder_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/reminders/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReminder_shouldReturn201() throws Exception {
        String body = """
                {
                    "title": "새 할 일",
                    "description": "메모",
                    "remindAt": "2026-03-20T14:00:00"
                }
                """;

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("새 할 일"))
                .andExpect(jsonPath("$.description").value("메모"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void updateReminder_shouldReturnUpdated() throws Exception {
        Reminder saved = reminderRepository.save(Reminder.builder().title("원래 제목").build());
        String body = """
                {
                    "title": "수정된 제목",
                    "description": "메모 추가"
                }
                """;

        mockMvc.perform(put("/api/reminders/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.description").value("메모 추가"));
    }

    @Test
    void toggleComplete_shouldToggle() throws Exception {
        Reminder saved = reminderRepository.save(Reminder.builder().title("할 일").build());

        mockMvc.perform(patch("/api/reminders/{id}/complete", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(patch("/api/reminders/{id}/complete", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void deleteReminder_shouldReturn204() throws Exception {
        Reminder saved = reminderRepository.save(Reminder.builder().title("삭제할 일").build());

        mockMvc.perform(delete("/api/reminders/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reminders/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
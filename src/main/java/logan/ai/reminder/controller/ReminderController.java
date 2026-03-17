package logan.ai.reminder.controller;

import logan.ai.reminder.entity.Reminder;
import logan.ai.reminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getReminders(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search) {
        List<Reminder> reminders;
        if (search != null) {
            reminders = reminderService.search(search);
        } else if (filter != null) {
            reminders = reminderService.findByFilter(filter);
        } else {
            reminders = reminderService.findAll();
        }
        return ResponseEntity.ok(reminders.stream().map(ReminderResponse::from).toList());
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Long>> getCounts() {
        return ResponseEntity.ok(reminderService.getCounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getReminder(@PathVariable Long id) {
        return ResponseEntity.ok(ReminderResponse.from(reminderService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@RequestBody ReminderRequest request) {
        Reminder created = reminderService.create(request.title(), request.description(), request.remindAt(), request.priority(), request.reminderListId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReminderResponse.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(@PathVariable Long id, @RequestBody ReminderRequest request) {
        Reminder updated = reminderService.update(id, request.title(), request.description(), request.remindAt(), request.priority(), request.reminderListId());
        return ResponseEntity.ok(ReminderResponse.from(updated));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ReminderResponse> toggleComplete(@PathVariable Long id) {
        return ResponseEntity.ok(ReminderResponse.from(reminderService.toggleComplete(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

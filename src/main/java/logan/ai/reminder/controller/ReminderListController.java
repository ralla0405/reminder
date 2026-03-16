package logan.ai.reminder.controller;

import logan.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public ResponseEntity<List<ReminderListResponse>> getLists() {
        return ResponseEntity.ok(
                reminderListService.findAll().stream().map(ReminderListResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderListResponse> getList(@PathVariable Long id) {
        return ResponseEntity.ok(ReminderListResponse.from(reminderListService.findById(id)));
    }

    @GetMapping("/{id}/reminders")
    public ResponseEntity<List<ReminderResponse>> getRemindersByList(@PathVariable Long id) {
        return ResponseEntity.ok(
                reminderListService.findRemindersByListId(id).stream().map(ReminderResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ReminderListResponse> createList(@RequestBody ReminderListRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReminderListResponse.from(reminderListService.create(request.name(), request.color())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderListResponse> updateList(@PathVariable Long id, @RequestBody ReminderListRequest request) {
        return ResponseEntity.ok(
                ReminderListResponse.from(reminderListService.update(id, request.name(), request.color())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        reminderListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

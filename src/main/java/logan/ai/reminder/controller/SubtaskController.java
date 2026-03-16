package logan.ai.reminder.controller;

import logan.ai.reminder.service.ports.in.SubtaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders/{reminderId}/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskService subtaskService;

    @GetMapping
    public ResponseEntity<List<SubtaskResponse>> getSubtasks(@PathVariable Long reminderId) {
        return ResponseEntity.ok(
                subtaskService.findByReminderId(reminderId).stream().map(SubtaskResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<SubtaskResponse> createSubtask(
            @PathVariable Long reminderId, @RequestBody SubtaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubtaskResponse.from(subtaskService.create(reminderId, request.title(), request.sortOrder())));
    }

    @PutMapping("/{subtaskId}")
    public ResponseEntity<SubtaskResponse> updateSubtask(
            @PathVariable Long reminderId, @PathVariable Long subtaskId, @RequestBody SubtaskRequest request) {
        return ResponseEntity.ok(
                SubtaskResponse.from(subtaskService.update(reminderId, subtaskId, request.title(), request.sortOrder())));
    }

    @PatchMapping("/{subtaskId}/complete")
    public ResponseEntity<SubtaskResponse> toggleComplete(
            @PathVariable Long reminderId, @PathVariable Long subtaskId) {
        return ResponseEntity.ok(SubtaskResponse.from(subtaskService.toggleComplete(reminderId, subtaskId)));
    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable Long reminderId, @PathVariable Long subtaskId) {
        subtaskService.delete(reminderId, subtaskId);
        return ResponseEntity.noContent().build();
    }
}

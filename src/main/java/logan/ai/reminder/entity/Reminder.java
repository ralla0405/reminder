package logan.ai.reminder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDateTime remindAt;

    private Boolean completed;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_list_id")
    private ReminderList reminderList;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Reminder(String title, String description, LocalDateTime remindAt, ReminderList reminderList, Priority priority) {
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
        this.reminderList = reminderList;
        this.priority = (priority != null) ? priority : Priority.NONE;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description, LocalDateTime remindAt) {
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
    }

    public void updatePriority(Priority priority) {
        this.priority = priority;
    }

    public void assignToList(ReminderList reminderList) {
        this.reminderList = reminderList;
    }

    public void complete() {
        this.completed = true;
    }

    public void uncomplete() {
        this.completed = false;
    }
}
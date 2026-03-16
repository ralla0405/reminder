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

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Reminder(String title, String description, LocalDateTime remindAt) {
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description, LocalDateTime remindAt) {
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
    }

    public void complete() {
        this.completed = true;
    }

    public void uncomplete() {
        this.completed = false;
    }
}
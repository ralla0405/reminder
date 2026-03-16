package logan.ai.reminder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;

    private Integer sortOrder;

    @Builder
    public Subtask(String title, Reminder reminder, Integer sortOrder) {
        this.title = title;
        this.reminder = reminder;
        this.sortOrder = (sortOrder != null) ? sortOrder : 0;
        this.completed = false;
    }

    public void update(String title, Integer sortOrder) {
        this.title = title;
        this.sortOrder = sortOrder;
    }

    public void toggleComplete() {
        this.completed = !this.completed;
    }
}

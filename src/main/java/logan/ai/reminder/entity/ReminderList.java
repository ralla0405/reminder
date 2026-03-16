package logan.ai.reminder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String color;

    @OneToMany(mappedBy = "reminderList")
    private List<Reminder> reminders = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ReminderList(String name, String color) {
        this.name = name;
        this.color = (color != null) ? color : "#007AFF";
        this.createdAt = LocalDateTime.now();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}

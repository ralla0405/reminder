package logan.ai.reminder.repository;

import logan.ai.reminder.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByReminderIdOrderBySortOrderAsc(Long reminderId);
}

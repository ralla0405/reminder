package logan.ai.reminder.repository;

import logan.ai.reminder.entity.ReminderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {
}

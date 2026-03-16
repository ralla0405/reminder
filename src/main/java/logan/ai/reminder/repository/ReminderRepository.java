package logan.ai.reminder.repository;

import logan.ai.reminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByCompleted(boolean completed);

    List<Reminder> findByRemindAtBetween(LocalDateTime start, LocalDateTime end);

    List<Reminder> findByRemindAtIsNotNullOrderByRemindAtAsc();

    @Query("SELECT r FROM Reminder r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Reminder> searchByKeyword(@Param("keyword") String keyword);

    long countByCompletedFalseAndRemindAtBetween(LocalDateTime start, LocalDateTime end);

    long countByRemindAtIsNotNull();

    long countByCompletedFalse();

    long countByCompletedTrue();
}
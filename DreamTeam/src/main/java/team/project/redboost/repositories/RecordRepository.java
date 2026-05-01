package team.project.redboost.repositories;

import team.project.redboost.entities.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByMeetingId(Long meetingId);

    boolean existsByMeetingId(Long meetingId);

}
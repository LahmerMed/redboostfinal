package team.project.redboost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.redboost.entities.Reclamation;
import team.project.redboost.entities.User;

import java.util.List;
import java.util.Optional;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByUser(User user);
    Optional<Reclamation> findByIdReclamationAndUser(Long idReclamation, User user); // Updated method name
}
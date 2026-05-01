package team.project.redboost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.redboost.entities.Coach;
import team.project.redboost.entities.Entrepreneur;
import team.project.redboost.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByProviderId(String providerId); // Add this method
}


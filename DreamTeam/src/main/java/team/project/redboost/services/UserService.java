package team.project.redboost.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.project.redboost.entities.User;
import team.project.redboost.repositories.UserRepository;

import java.io.IOException;

@Service
public class UserService { // No need to implement an interface

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Use the PasswordEncoder interface

    public User addUser(User user) {

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // If the user is authenticated via Firebase, do not encode the password
            user.setPassword(null); // or generate a random secure password if needed
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }


    @Autowired
    private CloudinaryService cloudinaryService;

    public void updateProfilePicture(String email, String imageUrl) {
        // Find the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Update the profile picture URL
        user.setProfilePictureUrl(imageUrl);
        userRepository.save(user);
    }

}
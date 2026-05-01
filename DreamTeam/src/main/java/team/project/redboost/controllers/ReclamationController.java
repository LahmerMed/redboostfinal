package team.project.redboost.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import team.project.redboost.authentif.JwtUtil;
import team.project.redboost.entities.Reclamation;
import team.project.redboost.entities.User;
import team.project.redboost.repositories.UserRepository;
import team.project.redboost.services.ReclamationService;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {

    @Autowired
    private ReclamationService reclamationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;
    // Create a new reclamation
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reclamation> createReclamation(@RequestBody Reclamation reclamation, HttpServletRequest request) {
        // Extract the token from the request header
        String token = request.getHeader("Authorization").replace("Bearer ", "");

        // Extract the userId from the token
        String userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Invalid token
        }

        // Fetch the User entity from the database
        User user = userRepository.findById(Long.valueOf(userId)).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found
        }

        // Set the user in the reclamation
        reclamation.setUser(user);

        // Save the reclamation
        Reclamation createdReclamation = reclamationService.createReclamation(reclamation);
        return new ResponseEntity<>(createdReclamation, HttpStatus.CREATED);
    }

    // Get all reclamations for the authenticated user
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reclamation>> getAllReclamations(@AuthenticationPrincipal UserDetails userDetails) {
        // Fetch the User entity from the database using the email from UserDetails
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found
        }

        // Fetch reclamations for the authenticated user
        List<Reclamation> reclamations = reclamationService.getAllReclamationsByUser(user);
        return new ResponseEntity<>(reclamations, HttpStatus.OK);
    }

    // Get a reclamation by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reclamation> getReclamationById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch the User entity from the database using the email from UserDetails
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found
        }

        // Fetch the reclamation by ID and user
        Reclamation reclamation = reclamationService.getReclamationByIdAndUser(id, user);
        if (reclamation != null) {
            return new ResponseEntity<>(reclamation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update a reclamation
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reclamation> updateReclamation(@PathVariable Long id, @RequestBody Reclamation reclamationDetails, @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch the User entity from the database using the email from UserDetails
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found
        }

        // Update the reclamation
        Reclamation updatedReclamation = reclamationService.updateReclamation(id, reclamationDetails, user);
        if (updatedReclamation != null) {
            return new ResponseEntity<>(updatedReclamation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a reclamation
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch the User entity from the database using the email from UserDetails
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found
        }

        // Delete the reclamation
        boolean isDeleted = reclamationService.deleteReclamation(id, user);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
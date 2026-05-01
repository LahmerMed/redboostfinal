/*package team.project.redboost.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import team.project.redboost.entities.Coach;
import team.project.redboost.entities.Entrepreneur;
import team.project.redboost.entities.RendezVous;
import team.project.redboost.repositories.CoachRepository;
import team.project.redboost.repositories.EntrepreneurRepository;
import team.project.redboost.services.CalendarService;
import team.project.redboost.services.RendezVousService;
import team.project.redboost.services.EmailService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rendezvous")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
public class RendezVousController {

    @Autowired
    private CalendarService googleCalendarService;
    @Autowired
    private RendezVousService rendezVousService;

    private static final Logger logger = LoggerFactory.getLogger(RendezVousController.class);
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private EntrepreneurRepository entrepreneurRepository;





    @Autowired
    private EmailService emailService;

    @Operation(summary = "Mettre à jour le statut d’un rendez-vous et l’intégrer à Google Calendar si accepté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour, et ajouté à Google Calendar si accepté"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour ou de l’ajout à Google Calendar")
    })


    @PatchMapping("/update-status/{id}")
    public ResponseEntity<ResponseMessage> updateRendezVousStatus(
            @PathVariable Long id,
            @RequestBody RendezVous.Status status,
            Authentication authentication) {  // Added Authentication parameter
        try {
            logger.info("Updating status for rendezvous ID: {} to {}", id, status);

            Optional<RendezVous> optionalRendezVous = rendezVousService.getRendezVousById(id);
            if (optionalRendezVous.isEmpty()) {
                logger.warn("Rendezvous with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }

            RendezVous rendezVous = optionalRendezVous.get();
            rendezVous.setStatus(status);
            rendezVousService.updateRendezVous(id, rendezVous);

            boolean success = true;
            String message;

            if (RendezVous.Status.ACCEPTED.equals(status)) {
                try {
                    if (authentication == null) {
                        logger.error("No authentication provided for Google Calendar integration");
                        throw new IllegalStateException("User must be authenticated to add to Google Calendar");
                    }
                    googleCalendarService.ajouterRendezVous(rendezVous, authentication);  // Pass Authentication
                    message = "Rendez-vous approuvé et ajouté avec succès à Google Calendar";
                    logger.info("Rendezvous ID {} added to Google Calendar for user {}", id, authentication.getName());
                } catch (Exception e) {
                    success = false;
                    message = "Erreur lors de l’ajout à Google Calendar : " + e.getMessage();
                    logger.error("Failed to add rendezvous ID {} to Google Calendar: {}", id, e.getMessage(), e);
                    return ResponseEntity.status(500).body(new ResponseMessage(message, false));
                }
            } else {
                message = "Statut du rendez-vous mis à jour avec succès";
                logger.info("Rendezvous ID {} status updated to {}", id, status);
            }

            return ResponseEntity.ok(new ResponseMessage(message, success));
        } catch (Exception e) {
            logger.error("Error updating rendezvous status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseMessage("Erreur lors de la mise à jour du statut : " + e.getMessage(), false));
        }
    }



    @PostMapping("/approve/{appointmentId}")
    public ResponseEntity<String> approveAppointment(@PathVariable Long appointmentId) {
        // Fetch appointment details from the database
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        // Send email with Google Calendar invitation
        try {
            emailService.sendAppointmentEmail(
                    appointment.getUserEmail(),
                    "Appointment with Coach",
                    appointment.getStartDateTime(),
                    appointment.getEndDateTime(),
                    "Appointment details",
                    "Virtual Meeting"
            );
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }

        return ResponseEntity.ok("Appointment approved and email sent");
    }
}
    // Classe interne pour la réponse JSON
    public static class ResponseMessage {
        private String message;
        private boolean success;

        public ResponseMessage(String message, boolean success) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    @PostMapping("/add")
    public ResponseEntity<RendezVous> createRendezVous(
            @RequestBody RendezVous rendezVous,
            @RequestParam Long coachId,
            @RequestParam Long entrepreneurId
    ) {
        try {
            // Fetch the coach and entrepreneur from the database
            Optional<Coach> coach = coachRepository .findById(coachId);
            Optional<Entrepreneur> entrepreneur = entrepreneurRepository.findById(entrepreneurId);

            if (coach.isEmpty() || entrepreneur.isEmpty()) {
                return ResponseEntity.badRequest().body(null); // Coach or entrepreneur not found
            }

            // Set the coach and entrepreneur for the rendezvous
            rendezVous.setCoach(coach.get());
            rendezVous.setEntrepreneur(entrepreneur.get());

            // Vérifier les rendez-vous approuvés pour la même date
            List<RendezVous> acceptedAppointments = rendezVousService.getRendezVousByDateAndStatus(rendezVous.getDate(), RendezVous.Status.ACCEPTED);

            // Convertir les heures en objets LocalTime pour comparer
            String newHeure = rendezVous.getHeure();
            if (newHeure == null || newHeure.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null); // ou une réponse d'erreur personnalisée
            }

            java.time.LocalTime newTime = java.time.LocalTime.parse(newHeure);
            int bufferMinutes = 45; // Augmenté à 45 minutes au lieu de 15

            for (RendezVous existing : acceptedAppointments) {
                java.time.LocalTime existingTime = java.time.LocalTime.parse(existing.getHeure());
                long minutesDiff = java.time.Duration.between(existingTime, newTime).toMinutes();
                if (Math.abs(minutesDiff) < bufferMinutes || minutesDiff == 0) {
                    return ResponseEntity.badRequest()
                            .body(null); // ou { error: "Cette heure n’est pas disponible ou trop proche d’un rendez-vous existant." }
                }
            }

            // Créer le rendez-vous si pas de conflit (statut PENDING par défaut)
            if (rendezVous.getStatus() == null) {
                rendezVous.setStatus(RendezVous.Status.PENDING);
            }

            // Save the appointment
            RendezVous savedRendezVous = rendezVousService.createRendezVous(rendezVous);
            return ResponseEntity.ok(savedRendezVous);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du rendez-vous : {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null); // ou une réponse d'erreur personnalisée
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        logger.info("Fetching all rendezvous");
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        logger.info("Fetching rendezvous by ID: {}", id);
        Optional<RendezVous> rendezVous = rendezVousService.getRendezVousById(id);
        return rendezVous.map(ResponseEntity::ok).orElseGet(() -> {
            logger.warn("Rendezvous with ID {} not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RendezVous> updateRendezVous(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        logger.info("Updating rendezvous ID: {}", id);
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, rendezVous));
    }

    @Operation(summary = "Obtenir les rendez-vous approuvés pour une date spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous approuvés récupérée avec succès"),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour cette date"),
            @ApiResponse(responseCode = "400", description = "Format de date invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/accepted")
    public ResponseEntity<List<RendezVous>> getAcceptedAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "ACCEPTED") RendezVous.Status status) {
        try {
            logger.info("Fetching accepted appointments for date: {} with status: {}", date, status);
            List<RendezVous> appointments = rendezVousService.getRendezVousByDateAndStatus(date, status);
            if (appointments.isEmpty()) {
                logger.info("No accepted appointments found for date: {}", date);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format for date {}: {}", date, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Server error fetching appointments for date {} and status {}: {}", date, status, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        logger.info("Deleting rendezvous ID: {}", id);
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByCoach(@PathVariable Long coachId) {
        logger.info("Fetching rendezvous for coach ID: {}", coachId);
        List<RendezVous> rendezVous = rendezVousService.getRendezVousByCoachId(coachId);
        return ResponseEntity.ok(rendezVous);
    }
}*/
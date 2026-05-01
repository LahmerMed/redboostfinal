package team.project.redboost.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.project.redboost.dto.StatutReclamationDTO;
import team.project.redboost.entities.Reclamation;
import team.project.redboost.entities.ReponseReclamation;
import team.project.redboost.services.ReponseReclamationService;
import team.project.redboost.services.ReclamationService;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations/{idReclamation}/reponses")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReponseReclamationController {

    private final ReponseReclamationService reponseService;
    private final ReclamationService reclamationService;

    // Récupérer toutes les réponses pour une réclamation
    @GetMapping
    public List<ReponseReclamation> getReponsesByReclamation(@PathVariable Long idReclamation) {
        return reponseService.getReponsesByReclamation(idReclamation);
    }

    // Ajouter une nouvelle réponse utilisateur
    @PostMapping("/user/{userId}")
    public ResponseEntity<ReponseReclamation> createUserReponse(
            @PathVariable Long idReclamation,
            @PathVariable Long userId,
            @RequestBody ReponseReclamation reponse) {
        ReponseReclamation saved = reponseService.createUserReponse(idReclamation, reponse, userId);
        return saved != null ? ResponseEntity.ok(saved) : ResponseEntity.notFound().build();
    }

    // Ajouter une nouvelle réponse admin
    @PostMapping("/admin/{adminId}")
    public ResponseEntity<ReponseReclamation> createAdminReponse(
            @PathVariable Long idReclamation,
            @PathVariable Long adminId,
            @RequestBody ReponseReclamation reponse) {
        ReponseReclamation saved = reponseService.createAdminReponse(idReclamation, reponse, adminId);
        return saved != null ? ResponseEntity.ok(saved) : ResponseEntity.notFound().build();
    }

    // Méthode générique (pour la compatibilité avec le code existant)
    @PostMapping
    public ResponseEntity<ReponseReclamation> createReponse(
            @PathVariable Long idReclamation,
            @RequestBody ReponseReclamation reponse) {
        ReponseReclamation saved = reponseService.createReponse(idReclamation, reponse);
        return saved != null ? ResponseEntity.ok(saved) : ResponseEntity.notFound().build();
    }

    // Mettre à jour une réponse existante
    @PutMapping("/{idReponse}")
    public ResponseEntity<ReponseReclamation> updateReponse(
            @PathVariable Long idReponse,
            @RequestBody ReponseReclamation updatedReponse) {
        ReponseReclamation updated = reponseService.updateReponse(idReponse, updatedReponse);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Supprimer une réponse
    @DeleteMapping("/{idReponse}")
    public ResponseEntity<Void> deleteReponse(@PathVariable Long idReponse) {
        return reponseService.deleteReponse(idReponse) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
/*package team.project.redboost.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.project.redboost.entities.RendezVous;
import team.project.redboost.entities.Coach;
import team.project.redboost.repositories.CoachRepository;
import team.project.redboost.repositories.RendezVousRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RendezVousService {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private CoachRepository coachRepository;

    // Créer un rendez-vous avec statut "PENDING" par défaut
    public RendezVous createRendezVous(RendezVous rendezVous) {
        if (rendezVous.getStatus() == null) {
            rendezVous.setStatus(RendezVous.Status.PENDING); // Assurer que le statut est "PENDING" par défaut
        }
        return rendezVousRepository.save(rendezVous);
    }

    // Récupérer les rendez-vous par coach
    public List<RendezVous> getRendezVousByCoachId(Long coachId) {
        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isEmpty()) {
            throw new RuntimeException("Coach non trouvé avec l'id: " + coachId);
        }
        return rendezVousRepository.findByCoach(coach.get());
    }

    // Récupérer tous les rendez-vous
    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    // Récupérer un rendez-vous par ID
    public Optional<RendezVous> getRendezVousById(Long id) {
        return rendezVousRepository.findById(id);
    }

    // Mettre à jour un rendez-vous (y compris le statut si fourni)
    public RendezVous updateRendezVous(Long id, RendezVous newRendezVous) {
        return rendezVousRepository.findById(id)
                .map(existingRdv -> {
                    existingRdv.setTitle(newRendezVous.getTitle());
                    existingRdv.setEmail(newRendezVous.getEmail());
                    existingRdv.setDate(newRendezVous.getDate());
                    existingRdv.setHeure(newRendezVous.getHeure());
                    existingRdv.setDescription(newRendezVous.getDescription());
                    existingRdv.setCoach(newRendezVous.getCoach());
                    // Mettre à jour le statut si fourni
                    if (newRendezVous.getStatus() != null) {
                        existingRdv.setStatus(newRendezVous.getStatus());
                    }
                    return rendezVousRepository.save(existingRdv);
                })
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé avec l'id: " + id));
    }

    // Récupérer les rendez-vous par date et statut
    public List<RendezVous> getRendezVousByDateAndStatus(LocalDate date, RendezVous.Status status) {
        return rendezVousRepository.findByDateAndStatus(date, status);
    }

    // Mettre à jour uniquement le statut d’un rendez-vous
    public RendezVous updateRendezVousStatus(Long id, RendezVous.Status status) {
        return rendezVousRepository.findById(id)
                .map(existingRdv -> {
                    existingRdv.setStatus(status);
                    return rendezVousRepository.save(existingRdv);
                })
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé avec l'id: " + id));
    }

    // Supprimer un rendez-vous
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé avec l'id: " + id);
        }
        rendezVousRepository.deleteById(id);
    }
}*/
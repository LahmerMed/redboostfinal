package team.project.redboost.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.project.redboost.entities.Reclamation;
import team.project.redboost.entities.User;
import team.project.redboost.repositories.ReclamationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReclamationService {

    @Autowired
    private ReclamationRepository reclamationRepository;

    public Reclamation createReclamation(Reclamation reclamation) {
        return reclamationRepository.save(reclamation);
    }

    public List<Reclamation> getAllReclamationsByUser(User user) {
        return reclamationRepository.findByUser(user);
    }

    public Reclamation getReclamationByIdAndUser(Long idReclamation, User user) {
        Optional<Reclamation> reclamation = reclamationRepository.findByIdReclamationAndUser(idReclamation, user);
        return reclamation.orElse(null);
    }


    public Reclamation updateReclamation(Long id, Reclamation reclamationDetails, User user) {
        Optional<Reclamation> reclamationOptional = reclamationRepository.findByIdReclamationAndUser(id, user);
        if (reclamationOptional.isPresent()) {
            Reclamation reclamation = reclamationOptional.get();
            reclamation.setSujet(reclamationDetails.getSujet());
            reclamation.setDate(reclamationDetails.getDate());
            reclamation.setStatut(reclamationDetails.getStatut());
            reclamation.setDescription(reclamationDetails.getDescription());
            reclamation.setCategorie(reclamationDetails.getCategorie());
            reclamation.setFichierReclamation(reclamationDetails.getFichierReclamation());
            return reclamationRepository.save(reclamation);
        } else {
            return null;
        }
    }

    public boolean deleteReclamation(Long id, User user) {
        Optional<Reclamation> reclamationOptional = reclamationRepository.findByIdReclamationAndUser(id, user);
        if (reclamationOptional.isPresent()) {
            reclamationRepository.delete(reclamationOptional.get());
            return true;
        } else {
            return false;
        }
    }
}
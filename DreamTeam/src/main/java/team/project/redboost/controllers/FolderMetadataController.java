package team.project.redboost.controllers;

import team.project.redboost.dtos.CreateFolderRequest;
import team.project.redboost.entities.Category;
import team.project.redboost.entities.FolderMetadata;
import team.project.redboost.entities.User;
import team.project.redboost.services.CategoryService;
import team.project.redboost.services.FolderMetadataService;
import team.project.redboost.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/folders")
public class FolderMetadataController {

    private static final Logger logger = LoggerFactory.getLogger(FolderMetadataController.class);

    private final FolderMetadataService folderMetadataService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public FolderMetadataController(FolderMetadataService folderMetadataService, CategoryService categoryService, UserService userService) {
        this.folderMetadataService = folderMetadataService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // Get username (email)
        return userService.findByEmail(currentUsername);
    }

    @PostMapping
    public ResponseEntity<FolderMetadata> createFolderMetadata(@RequestBody CreateFolderRequest request) {
        logger.info("Received CreateFolderRequest: {}", request);

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Unauthorized access: User not found.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        FolderMetadata folderMetadata = new FolderMetadata();
        folderMetadata.setFolderName(request.getFolderName());
        folderMetadata.setFolderPath(request.getFolderPath());

        if (request.getCategoryId() == null && request.getParentFolderId() == null) {
            logger.warn("Bad Request: CategoryId and ParentFolderId are null!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (request.getCategoryId() != null) {
            try {
                Category category = categoryService.getCategoryById(request.getCategoryId());
                folderMetadata.setCategory(category);
            } catch (NoSuchElementException e) {
                logger.warn("Category not found with id: {}", request.getCategoryId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Or a more specific error
            }
        }

        folderMetadata.setUser(currentUser);
        FolderMetadata createdFolderMetadata = folderMetadataService.createFolderMetadata(folderMetadata);

        // Ensure categoryId is included in the response
        createdFolderMetadata.setCategoryId(createdFolderMetadata.getCategory() != null ? createdFolderMetadata.getCategory().getId() : null);

        logger.info("Folder created with ID: {} for user: {}", createdFolderMetadata.getId(), currentUser.getEmail());
        return new ResponseEntity<>(createdFolderMetadata, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderMetadata> getFolderMetadataById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            FolderMetadata folderMetadata = folderMetadataService.getFolderMetadataById(id);

            if (!folderMetadata.getUser().getId().equals(currentUser.getId())) {
                logger.warn("Forbidden access: User {} does not own folder with ID: {}", currentUser.getEmail(), id);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Ensure categoryId is included in the response
            folderMetadata.setCategoryId(folderMetadata.getCategory() != null ? folderMetadata.getCategory().getId() : null);

            return new ResponseEntity<>(folderMetadata, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<FolderMetadata>> getAllFolderMetadata() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<FolderMetadata> folderMetadataList = folderMetadataService.getAllFolderMetadataForUser(currentUser.getId());

        // Ensure categoryId is included in the response
        folderMetadataList.forEach(folder -> folder.setCategoryId(folder.getCategory() != null ? folder.getCategory().getId() : null));

        return new ResponseEntity<>(folderMetadataList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderMetadata> updateFolderMetadata(@PathVariable Long id, @RequestBody FolderMetadata folderMetadataDetails) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            FolderMetadata existingFolderMetadata = folderMetadataService.getFolderMetadataById(id);

            if (existingFolderMetadata == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!existingFolderMetadata.getUser().getId().equals(currentUser.getId())) {
                logger.warn("Forbidden access: User {} does not own folder with ID: {}", currentUser.getEmail(), id);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (folderMetadataDetails.getCategory() != null) {
                try {
                    Category category = categoryService.getCategoryById(folderMetadataDetails.getCategory().getId());
                    existingFolderMetadata.setCategory(category);
                } catch (NoSuchElementException e) {
                    logger.warn("Category not found with id: {}", folderMetadataDetails.getCategory().getId());
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            existingFolderMetadata.setFolderName(folderMetadataDetails.getFolderName());
            existingFolderMetadata.setFolderPath(folderMetadataDetails.getFolderPath());

            FolderMetadata updatedFolderMetadata = folderMetadataService.updateFolderMetadata(id, existingFolderMetadata);

            // Ensure categoryId is included in the response
            updatedFolderMetadata.setCategoryId(updatedFolderMetadata.getCategory() != null ? updatedFolderMetadata.getCategory().getId() : null);

            return new ResponseEntity<>(updatedFolderMetadata, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolderMetadata(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            FolderMetadata folderMetadata = folderMetadataService.getFolderMetadataById(id);

            if (!folderMetadata.getUser().getId().equals(currentUser.getId())) {
                logger.warn("Forbidden access: User {} does not own folder with ID: {}", currentUser.getEmail(), id);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            folderMetadataService.deleteFolderMetadata(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

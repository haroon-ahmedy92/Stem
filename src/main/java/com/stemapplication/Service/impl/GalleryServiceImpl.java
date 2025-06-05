//package com.stemapplication.Service.impl;
//
//
//import com.stemapplication.DTO.GalleryCreateDto;
//import com.stemapplication.DTO.GalleryDto;
//import com.stemapplication.DTO.UserDto;
//import com.stemapplication.Models.Category;
//import com.stemapplication.Models.Gallery;
//import com.stemapplication.Models.UserEntity;
//import com.stemapplication.Repository.CategoryRepository;
//import com.stemapplication.Repository.GalleryRepository;
//import com.stemapplication.Repository.UserRepository;
//import com.stemapplication.Service.GalleryService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Service
//public class GalleryServiceImpl implements GalleryService {
//
//    private final GalleryRepository galleryRepository;
//    private final UserRepository userRepository;
//    private final CategoryRepository categoryRepository;
//
//    @Autowired
//    public GalleryServiceImpl(GalleryRepository galleryRepository,
//                              UserRepository userRepository,
//                              CategoryRepository categoryRepository) {
//        this.galleryRepository = galleryRepository;
//        this.userRepository = userRepository;
//        this.categoryRepository = categoryRepository;
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<Map<String, String>> createGalleryItem(GalleryCreateDto galleryCreateDto, String username) {
//        try {
//            UserEntity user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//            Category category = categoryRepository.findById(galleryCreateDto.getCategory())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//            Gallery gallery = new Gallery();
//            gallery.setTitle(galleryCreateDto.getTitle());
//            gallery.setDescription(galleryCreateDto.getDescription());
//            gallery.setCategory(category);
//
//            List<String> tagNames = galleryCreateDto.getTags().stream()
//                    .map(tag -> tag.get("name"))
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//            gallery.setTags(tagNames);
//
//            gallery.setImage(galleryCreateDto.getImage());
//            gallery.setCreatedBy(user);
//            gallery.setCreatedAt(LocalDateTime.now());
//            gallery.setFeatured(galleryCreateDto.isFeatured());
//
//            galleryRepository.save(gallery);
//
//            Map<String, String> responseMessage = new HashMap<>();
//            responseMessage.put("message", "Gallery item created successfully");
//            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
//
//        } catch (EntityNotFoundException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create gallery item: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<List<GalleryDto>> getAllGalleryItems() {
//        List<Gallery> galleries = galleryRepository.findAll();
//        List<GalleryDto> galleryDtos = galleries.stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(galleryDtos);
//    }
//
//    @Override
//    public ResponseEntity<GalleryDto> getGalleryItemById(Long id) {
//        Gallery gallery = galleryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));
//        return ResponseEntity.ok(mapToDto(gallery));
//    }
//
//    @Override
//    public ResponseEntity<List<GalleryDto>> getGalleryItemsByCategory(String categoryId) {
//        List<Gallery> galleries = galleryRepository.findByCategory_Id(categoryId);
//        List<GalleryDto> galleryDtos = galleries.stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(galleryDtos);
//    }
//
//    @Override
//    public ResponseEntity<List<GalleryDto>> getGalleryItemsByUser(Long userId) {
//        List<Gallery> galleries = galleryRepository.findByCreatedBy_Id(userId);
//        List<GalleryDto> galleryDtos = galleries.stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(galleryDtos);
//    }
//
//    @Override
//    public ResponseEntity<List<GalleryDto>> getGalleryItemsByTag(String tag) {
//        List<Gallery> galleries = galleryRepository.findByTagsContaining(tag);
//        List<GalleryDto> galleryDtos = galleries.stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(galleryDtos);
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<Map<String, String>> updateGalleryItem(Long id, GalleryCreateDto galleryCreateDto, String username) {
//        try {
//            Gallery gallery = galleryRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));
//
//            UserEntity user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//            boolean isCreator = gallery.getCreatedBy().getId().equals(user.getId());
//            boolean isAdmin = user.getRoles().stream()
//                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN"));
//
//            if (!isCreator && !isAdmin) {
//                throw new AccessDeniedException("You don't have permission to update this gallery item");
//            }
//
//            Category category = categoryRepository.findById(galleryCreateDto.getCategory())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//            gallery.setTitle(galleryCreateDto.getTitle());
//            gallery.setDescription(galleryCreateDto.getDescription());
//            gallery.setCategory(category);
//            gallery.setTags(galleryCreateDto.getTags().stream()
//                    .map(tag -> tag.get("name"))
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList()));
//            gallery.setImage(galleryCreateDto.getImage());
//            gallery.setUpdatedAt(LocalDateTime.now());
//            gallery.setFeatured(galleryCreateDto.isFeatured());
//
//            galleryRepository.save(gallery);
//
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Gallery item updated successfully");
//            return ResponseEntity.ok(response);
//
//        } catch (EntityNotFoundException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (AccessDeniedException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//
//        } catch (Exception e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Failed to update gallery item: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> deleteGalleryItem(Long id, String username) {
//        try {
//            Gallery gallery = galleryRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));
//
//            UserEntity user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//            // Check if user is the creator or has admin/super-admin rights
//            boolean isCreator = gallery.getCreatedBy().getId().equals(user.getId());
//            boolean isAdmin = user.getRoles().stream()
//                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN"));
//
//            if (!isCreator && !isAdmin) {
//                throw new AccessDeniedException("You don't have permission to delete this gallery item");
//            }
//
//            galleryRepository.delete(gallery);
//
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Gallery item deleted successfully");
//            return ResponseEntity.ok(response);
//
//        } catch (EntityNotFoundException | AccessDeniedException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//
//            if (e instanceof EntityNotFoundException) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//            }
//        } catch (Exception e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Failed to delete gallery item: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> incrementViewCount(Long id) {
//        try {
//            Gallery gallery = galleryRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));
//
//            gallery.setViewCount(gallery.getViewCount() + 1);
//            galleryRepository.save(gallery);
//
//            Map<String, Integer> response = new HashMap<>();
//            response.put("viewCount", gallery.getViewCount());
//            return ResponseEntity.ok(response);
//        } catch (EntityNotFoundException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//
//    @Override
//    public ResponseEntity<List<GalleryDto>> getFeaturedGalleryItems() {
//        List<Gallery> galleries = galleryRepository.findByFeaturedTrue();
//        List<GalleryDto> galleryDtos = galleries.stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(galleryDtos);
//    }
//
//    private GalleryDto mapToDto(Gallery gallery) {
//        GalleryDto galleryDto = new GalleryDto();
//        galleryDto.setId(gallery.getId());
//        galleryDto.setTitle(gallery.getTitle());
//        galleryDto.setDescription(gallery.getDescription());
//
//        // Map Category to CategoryInfo
//        GalleryDto.CategoryInfo categoryInfo = new GalleryDto.CategoryInfo();
//        categoryInfo.setId(gallery.getCategory().getId());
//        categoryInfo.setName(gallery.getCategory().getName());
//        galleryDto.setCategory(categoryInfo);
//
//        // Map tags to List<Map<String, String>>
//        List<Map<String, String>> tagsList = gallery.getTags().stream()
//                .map(tag -> {
//                    Map<String, String> tagMap = new HashMap<>();
//                    tagMap.put("name", tag);
//                    return tagMap;
//                })
//                .collect(Collectors.toList());
//        galleryDto.setTags(tagsList);
//
//        galleryDto.setImage(gallery.getImage());
//        galleryDto.setCreatedAt(gallery.getCreatedAt());
//        galleryDto.setUpdatedAt(gallery.getUpdatedAt());
//        galleryDto.setViewCount(gallery.getViewCount());
//        galleryDto.setFeatured(gallery.isFeatured());
//
//        UserDto userDto = new UserDto();
//        userDto.setId(gallery.getCreatedBy().getId());
//        userDto.setUsername(gallery.getCreatedBy().getUsername());
//        userDto.setName(gallery.getCreatedBy().getName());
//        galleryDto.setCreatedBy(userDto);
//
//        return galleryDto;
//    }
//}






package com.stemapplication.Service.impl;

import com.stemapplication.DTO.GalleryCreateDto;
import com.stemapplication.DTO.GalleryDto;
import com.stemapplication.DTO.UserDto;
import com.stemapplication.Models.Category;
import com.stemapplication.Models.Gallery;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.CategoryRepository;
import com.stemapplication.Repository.GalleryRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Service.GalleryService;
import com.stemapplication.Utils.ActivityLogger;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Gallery Service implementation with activity logging integration.
 * This service handles operations related to gallery items including creation,
 * retrieval, update, and deletion with comprehensive activity logging.
 */
@Service
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityLogger activityLogger;

    @Autowired
    public GalleryServiceImpl(GalleryRepository galleryRepository,
                                  UserRepository userRepository,
                                  CategoryRepository categoryRepository,
                                  ActivityLogger activityLogger) {
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.activityLogger = activityLogger;
    }

    /**
     * Create a new gallery item
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> createGalleryItem(GalleryCreateDto galleryCreateDto, String username) {
        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Category category = categoryRepository.findById(galleryCreateDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            Gallery gallery = new Gallery();
            gallery.setTitle(galleryCreateDto.getTitle());
            gallery.setDescription(galleryCreateDto.getDescription());
            gallery.setCategory(category);

            List<String> tagNames = galleryCreateDto.getTags().stream()
                    .map(tag -> tag.get("name"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            gallery.setTags(tagNames);

            gallery.setImage(galleryCreateDto.getImage());
            gallery.setCreatedBy(user);
            gallery.setCreatedAt(LocalDateTime.now());
            gallery.setFeatured(galleryCreateDto.isFeatured());

            Gallery savedGallery = galleryRepository.save(gallery);

            // Log gallery creation
            activityLogger.logGalleryCreation(username, savedGallery.getTitle(), savedGallery.getId());

            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Gallery item created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create gallery item: " + e.getMessage());
        }
    }

    /**
     * Get all gallery items
     */
    @Override
    public ResponseEntity<List<GalleryDto>> getAllGalleryItems() {
        List<Gallery> galleries = galleryRepository.findAll();
        List<GalleryDto> galleryDtos = galleries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(galleryDtos);
    }

    /**
     * Get a gallery item by ID
     */
    @Override
    public ResponseEntity<GalleryDto> getGalleryItemById(Long id) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));
        return ResponseEntity.ok(mapToDto(gallery));
    }

    /**
     * Get gallery items by category
     */
    @Override
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByCategory(String categoryId) {
        List<Gallery> galleries = galleryRepository.findByCategory_Id(categoryId);
        List<GalleryDto> galleryDtos = galleries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(galleryDtos);
    }

    /**
     * Get gallery items by user
     */
    @Override
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByUser(Long userId) {
        List<Gallery> galleries = galleryRepository.findByCreatedBy_Id(userId);
        List<GalleryDto> galleryDtos = galleries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(galleryDtos);
    }

    /**
     * Get gallery items by tag
     */
    @Override
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByTag(String tag) {
        List<Gallery> galleries = galleryRepository.findByTagsContaining(tag);
        List<GalleryDto> galleryDtos = galleries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(galleryDtos);
    }

    /**
     * Update a gallery item
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> updateGalleryItem(Long id, GalleryCreateDto galleryCreateDto, String username) {
        try {
            Gallery gallery = galleryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));

            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            boolean isCreator = gallery.getCreatedBy().getId().equals(user.getId());
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN"));

            if (!isCreator && !isAdmin) {
                throw new AccessDeniedException("You don't have permission to update this gallery item");
            }

            Category category = categoryRepository.findById(galleryCreateDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            // Store original data for logging
            String originalTitle = gallery.getTitle();
            Long galleryId = gallery.getId();

            gallery.setTitle(galleryCreateDto.getTitle());
            gallery.setDescription(galleryCreateDto.getDescription());
            gallery.setCategory(category);
            gallery.setTags(galleryCreateDto.getTags().stream()
                    .map(tag -> tag.get("name"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            gallery.setImage(galleryCreateDto.getImage());
            gallery.setUpdatedAt(LocalDateTime.now());
            gallery.setFeatured(galleryCreateDto.isFeatured());

            galleryRepository.save(gallery);

            // Log gallery update
            activityLogger.logGalleryUpdate(username, gallery.getTitle(), galleryId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Gallery item updated successfully");
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (AccessDeniedException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update gallery item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a gallery item
     */
    @Override
    @Transactional
    public ResponseEntity<?> deleteGalleryItem(Long id, String username) {
        try {
            Gallery gallery = galleryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));

            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Check if user is the creator or has admin/super-admin rights
            boolean isCreator = gallery.getCreatedBy().getId().equals(user.getId());
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN"));

            if (!isCreator && !isAdmin) {
                throw new AccessDeniedException("You don't have permission to delete this gallery item");
            }

            // Store data for logging before deletion
            String galleryTitle = gallery.getTitle();
            Long galleryId = gallery.getId();

            galleryRepository.delete(gallery);

            // Log gallery deletion
            activityLogger.logGalleryDeletion(username, galleryTitle, galleryId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Gallery item deleted successfully");
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException | AccessDeniedException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());

            if (e instanceof EntityNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete gallery item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Increment view count for a gallery item
     */

    @Override
    @Transactional
    public ResponseEntity<?> incrementViewCount(Long id) {
        try {
            Gallery gallery = galleryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Gallery item not found"));

            gallery.setViewCount(gallery.getViewCount() + 1);
            galleryRepository.save(gallery);

            Map<String, Integer> response = new HashMap<>();
            response.put("viewCount", gallery.getViewCount());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get featured gallery items
     */
    @Override
    public ResponseEntity<List<GalleryDto>> getFeaturedGalleryItems() {
        List<Gallery> galleries = galleryRepository.findByFeaturedTrue();
        List<GalleryDto> galleryDtos = galleries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(galleryDtos);
    }

    /**
     * Map Gallery entity to GalleryDto
     */
    private GalleryDto mapToDto(Gallery gallery) {
        GalleryDto galleryDto = new GalleryDto();
        galleryDto.setId(gallery.getId());
        galleryDto.setTitle(gallery.getTitle());
        galleryDto.setDescription(gallery.getDescription());

        // Map Category to CategoryInfo
        GalleryDto.CategoryInfo categoryInfo = new GalleryDto.CategoryInfo();
        categoryInfo.setId(gallery.getCategory().getId());
        categoryInfo.setName(gallery.getCategory().getName());
        galleryDto.setCategory(categoryInfo);

        // Map tags to List<Map<String, String>>
        List<Map<String, String>> tagsList = gallery.getTags().stream()
                .map(tag -> {
                    Map<String, String> tagMap = new HashMap<>();
                    tagMap.put("name", tag);
                    return tagMap;
                })
                .collect(Collectors.toList());
        galleryDto.setTags(tagsList);

        galleryDto.setImage(gallery.getImage());
        galleryDto.setCreatedAt(gallery.getCreatedAt());
        galleryDto.setUpdatedAt(gallery.getUpdatedAt());
        galleryDto.setViewCount(gallery.getViewCount());
        galleryDto.setFeatured(gallery.isFeatured());

        UserDto userDto = new UserDto();
        userDto.setId(gallery.getCreatedBy().getId());
        userDto.setUsername(gallery.getCreatedBy().getUsername());
        userDto.setName(gallery.getCreatedBy().getName());
        galleryDto.setCreatedBy(userDto);

        return galleryDto;
    }
}

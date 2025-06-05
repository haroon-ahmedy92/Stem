package com.stemapplication.Controller;

import com.stemapplication.DTO.GalleryCreateDto;
import com.stemapplication.DTO.GalleryDto;
import com.stemapplication.Models.Category;
import com.stemapplication.Repository.CategoryRepository;
import com.stemapplication.Service.GalleryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryService galleryService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public GalleryController(GalleryService galleryService, CategoryRepository categoryRepository) {
        this.galleryService = galleryService;
        this.categoryRepository = categoryRepository;
    }

    // POST endpoints (require authentication)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> createGalleryItem(
            @Valid @RequestBody GalleryCreateDto galleryCreateDto,
            Principal principal) {
        return galleryService.createGalleryItem(galleryCreateDto, principal.getName());
    }

    // GET endpoints (public access) - ORDER MATTERS: Specific routes BEFORE parameterized routes
    @GetMapping
    public ResponseEntity<List<GalleryDto>> getAllGalleryItems() {
        return galleryService.getAllGalleryItems();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<GalleryDto>> getFeaturedGalleryItems() {
        return galleryService.getFeaturedGalleryItems();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByCategory(@PathVariable String categoryId) {
        return galleryService.getGalleryItemsByCategory(categoryId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByUser(@PathVariable Long userId) {
        return galleryService.getGalleryItemsByUser(userId);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<GalleryDto>> getGalleryItemsByTag(@PathVariable String tag) {
        return galleryService.getGalleryItemsByTag(tag);
    }

    // POST view count (public access)
    @PostMapping("/{id}/view")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long id) {
        return galleryService.incrementViewCount(id);
    }

    // GET by ID (public access) - MUST be LAST to avoid conflicts with specific routes above
    @GetMapping("/{id}")
    public ResponseEntity<GalleryDto> getGalleryItemById(@PathVariable Long id) {
        return galleryService.getGalleryItemById(id);
    }

    // PUT and DELETE endpoints (require authentication)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> updateGalleryItem(
            @PathVariable Long id,
            @Valid @RequestBody GalleryCreateDto galleryCreateDto,
            Principal principal) {
        return galleryService.updateGalleryItem(id, galleryCreateDto, principal.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteGalleryItem(@PathVariable Long id, Principal principal) {
        return galleryService.deleteGalleryItem(id, principal.getName());
    }
}
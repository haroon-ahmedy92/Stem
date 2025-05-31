package com.stemapplication.Controller;

import com.stemapplication.DTO.GalleryCreateDto;
import com.stemapplication.DTO.GalleryDto;
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

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> createGalleryItem(
            @Valid @RequestBody GalleryCreateDto galleryCreateDto,
            Principal principal) {
        return galleryService.createGalleryItem(galleryCreateDto, principal.getName());
    }

    @GetMapping
    public ResponseEntity<List<GalleryDto>> getAllGalleryItems() {
        return galleryService.getAllGalleryItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GalleryDto> getGalleryItemById(@PathVariable Long id) {
        return galleryService.getGalleryItemById(id);
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

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateGalleryItem(
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

    @PostMapping("/{id}/view")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long id) {
        return galleryService.incrementViewCount(id);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<GalleryDto>> getFeaturedGalleryItems() {
        return galleryService.getFeaturedGalleryItems();
    }
}

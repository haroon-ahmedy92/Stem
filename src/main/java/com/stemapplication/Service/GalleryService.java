package com.stemapplication.Service;

import com.stemapplication.DTO.GalleryCreateDto;
import com.stemapplication.DTO.GalleryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface GalleryService {
    ResponseEntity<Map<String, String>> createGalleryItem(GalleryCreateDto galleryCreateDto, String username);
    ResponseEntity<List<GalleryDto>> getAllGalleryItems();
    ResponseEntity<GalleryDto> getGalleryItemById(Long id);
    ResponseEntity<List<GalleryDto>> getGalleryItemsByCategory(String categoryId);
    ResponseEntity<List<GalleryDto>> getGalleryItemsByUser(Long userId);
    ResponseEntity<List<GalleryDto>> getGalleryItemsByTag(String tag);
    ResponseEntity<Map<String, String>> updateGalleryItem(Long id, GalleryCreateDto galleryCreateDto, String username);
    ResponseEntity<?> deleteGalleryItem(Long id, String username);
    ResponseEntity<?> incrementViewCount(Long id);
    ResponseEntity<List<GalleryDto>> getFeaturedGalleryItems();
}
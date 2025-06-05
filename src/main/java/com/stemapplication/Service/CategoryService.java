package com.stemapplication.Service;

import org.springframework.http.ResponseEntity;
import java.util.List;

public interface CategoryService {
    ResponseEntity<?> getAllCategories();
}
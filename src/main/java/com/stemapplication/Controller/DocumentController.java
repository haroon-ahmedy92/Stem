package com.stemapplication.Controller;
import com.stemapplication.Models.BlogPost;
import com.stemapplication.Service.PostService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PostService postService;

    public DocumentController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long postId) {
        Optional<BlogPost> postOptional = Optional.ofNullable(postService.getPostById(postId));
        if (postOptional.isPresent()) {
            BlogPost blogPost = postOptional.get();
            String documentUrl = blogPost.getDocumentUrl();
            if (documentUrl != null && !documentUrl.isEmpty()) {
                try {
                    Path filePath = Paths.get(documentUrl);
                    Resource resource = new UrlResource(filePath.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        String contentType = "application/octet-stream"; // Default binary stream
                        String filename = filePath.getFileName().toString();

                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                                .body(resource);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } catch (MalformedURLException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

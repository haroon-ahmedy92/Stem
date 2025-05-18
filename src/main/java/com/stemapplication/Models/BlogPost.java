package com.stemapplication.Models;

import jakarta.persistence.*;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "blog_posts")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;
    private String alt;
    private String title;

    @Column(length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Embedded
    private Reactions reactions;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "document_url")
    private String documentUrl;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Reactions getReactions() {
        return reactions;
    }

    public void setReactions(Reactions reactions) {
        this.reactions = reactions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public Optional<ResponseEntity<BlogPost>> map(Object o) {
        if (o instanceof BlogPost) {
            BlogPost blogPost = (BlogPost) o;
            return Optional.of(ResponseEntity.ok(blogPost));
        }
        return Optional.empty();
    }

    // Utility method for converting to response entity
    public static Optional<ResponseEntity<BlogPost>> toResponseEntity(BlogPost blogPost) {
        return Optional.ofNullable(blogPost)
                .map(ResponseEntity::ok);
    }

    // Override equals and hashCode for proper entity comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogPost)) return false;
        BlogPost blogPost = (BlogPost) o;
        return id != null && id.equals(blogPost.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author=" + (author != null ? author.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
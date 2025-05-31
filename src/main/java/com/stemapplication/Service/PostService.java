package com.stemapplication.Service;

import com.stemapplication.DTO.*;
import com.stemapplication.Models.*; // Still need to import actual Entities
import com.stemapplication.Repository.BlogPostRepository;
import com.stemapplication.Repository.CategoryRepository;
import com.stemapplication.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stemapplication.DTO.FeaturedPost;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(BlogPostRepository blogPostRepository,
                       CategoryRepository categoryRepository,
                       UserRepository userRepository) {
        this.blogPostRepository = blogPostRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // CREATE Post (Input is still BlogPost entity, return BlogPost entity for simplicity)
    @Transactional
    public BlogPost createPost(BlogPost post, String username) {
        UserEntity author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Author user not found for username: " + username));
        post.setAuthor(author);
        if (post.getReactions() == null) {
            post.setReactions(new Reactions(0, 0));
        }
        if (post.getCategory() != null && post.getCategory().getId() != null) {
            Category existingCategory = categoryRepository.findById(post.getCategory().getId())
                    .orElse(null);
            if (existingCategory == null) {
                throw new EntityNotFoundException("Category with ID " + post.getCategory().getId() + " not found.");
            }
            post.setCategory(existingCategory);
        } else {
            post.setCategory(null);
        }

        return blogPostRepository.save(post);
    }

    // READ (Now returns DTOs)
    public List<BlogPostDto> getAllPostsDto() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToBlogPostDto)
                .collect(Collectors.toList());
    }

    public BlogPostDto getPostByIdDto(Long id) {
        return blogPostRepository.findById(id)
                .map(this::convertToBlogPostDto)
                .orElse(null);
    }

    public List<BlogPostDto> getPostsByCategoryDto(String categoryId) {
        if ("all".equalsIgnoreCase(categoryId)) {
            return getAllPostsDto();
        }
        return blogPostRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToBlogPostDto)
                .collect(Collectors.toList());
    }

    // --- CRITICAL CHANGE HERE ---
    // Now returns BlogPostDto
    @Transactional
    public BlogPostDto updatePost(Long id, BlogPost postDetails, String username, List<String> userRoles) {
        BlogPost updatedEntity = blogPostRepository.findById(id)
                .map(post -> {
                    boolean isAuthor = post.getAuthor().getUsername().equals(username);
                    boolean isAdmin = userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_SUPER_ADMIN");

                    if (!isAuthor && !isAdmin) {
                        throw new SecurityException("User is not authorized to update this post.");
                    }

                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    post.setImage(postDetails.getImage());
                    post.setAlt(postDetails.getAlt());
                    post.setDocumentUrl(postDetails.getDocumentUrl());

                    if (postDetails.getCategory() != null && postDetails.getCategory().getId() != null) {
                        Category existingCategory = categoryRepository.findById(postDetails.getCategory().getId())
                                .orElse(null);
                        if (existingCategory == null) {
                            throw new EntityNotFoundException("Category with ID " + postDetails.getCategory().getId() + " not found.");
                        }
                        post.setCategory(existingCategory);
                    } else {
                        post.setCategory(null);
                    }
                    // Save the updated entity
                    return blogPostRepository.save(post);
                })
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with ID: " + id));

        // Convert the updated entity to DTO before returning
        return convertToBlogPostDto(updatedEntity);
    }
    // --- END CRITICAL CHANGE ---


    // DELETE
    @Transactional
    public boolean deletePost(Long id, String username, List<String> userRoles) {
        return blogPostRepository.findById(id)
                .map(post -> {
                    boolean isAuthor = post.getAuthor().getUsername().equals(username);
                    boolean isAdmin = userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_SUPER_ADMIN");

                    if (!isAuthor && !isAdmin) {
                        throw new SecurityException("User is not authorized to delete this post.");
                    }
                    blogPostRepository.delete(post);
                    return true;
                })
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with ID: " + id));
    }

    // MAPPING METHODS (New methods to convert Entity to DTO)

    private BlogPostDto convertToBlogPostDto(BlogPost blogPost) {
        BlogPostDto dto = new BlogPostDto();
        dto.setId(blogPost.getId());
        dto.setImage(blogPost.getImage());
        dto.setAlt(blogPost.getAlt());
        dto.setTitle(blogPost.getTitle());
        dto.setContent(blogPost.getContent());
        dto.setCreatedAt(blogPost.getCreatedAt());
        dto.setUpdatedAt(blogPost.getUpdatedAt());
        dto.setDocumentUrl(blogPost.getDocumentUrl());

        if (blogPost.getAuthor() != null) {
            // Ensure the author entity is loaded if it's lazy-loaded.
            // This is crucial here. If you're inside a transaction, Hibernate will load it.
            // If not, you might need to fetch it explicitly or ensure your repository query does.
            // For @Transactional methods, accessing getAuthor() should trigger loading.
            dto.setAuthor(convertToUserDto(blogPost.getAuthor()));
        }
        if (blogPost.getCategory() != null) {
            // Same for category: ensure it's loaded if lazy
            dto.setCategory(convertToCategoryDto(blogPost.getCategory()));
        }
        if (blogPost.getReactions() != null) {
            dto.setReactions(new ReactionsDto(blogPost.getReactions().getLikes(), blogPost.getReactions().getComments()));
        }
        return dto;
    }

    private UserDto convertToUserDto(UserEntity userEntity) {
        UserDto dto = new UserDto();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        dto.setName(userEntity.getName()); // Assuming UserEntity has a 'name' field
        dto.setEmail(userEntity.getEmail());
        dto.setProfilePictureUrl(userEntity.getProfilePictureUrl());
        return dto;
    }

    private CategoryDto convertToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public List<CategoryDto> getAllCategoriesDto() { // Returns DTOs
        return categoryRepository.findAll().stream()
                .map(this::convertToCategoryDto)
                .collect(Collectors.toList());
    }

    // Featured Post related methods (now return DTOs)
    public FeaturedPost getFeaturedPost() { // Returns DTO version
        List<BlogPost> posts = blogPostRepository.findAllByOrderByCreatedAtDesc();
        if (posts.isEmpty()) {
            return null;
        }
        BlogPost firstPost = posts.get(0);

        FeaturedPost featuredPost = new FeaturedPost(); // Instantiate DTO
        featuredPost.setId(firstPost.getId()); // Set ID for consistency
        featuredPost.setTitle(firstPost.getTitle());
        featuredPost.setExcerpt(firstPost.getContent().substring(0,
                Math.min(150, firstPost.getContent().length())) + "...");

        featuredPost.setAuthor(convertToFeaturedPostAuthor(firstPost.getAuthor())); // Convert to FeaturedAuthorDto
        featuredPost.setDate(firstPost.getCreatedAt().toLocalDate().toString());
        featuredPost.setReadTime("5 min read"); // Placeholder
        return featuredPost;
    }

    private FeaturedAuthorDto convertToFeaturedPostAuthor(UserEntity userEntity) {
        FeaturedAuthorDto featuredAuthor = new FeaturedAuthorDto(); // Instantiate DTO
        featuredAuthor.setId(userEntity.getId());
        featuredAuthor.setName(userEntity.getName());
        featuredAuthor.setInitials(userEntity.getName() != null && userEntity.getName().length() >= 2 ?
                userEntity.getName().substring(0, 2).toUpperCase() : "");
        featuredAuthor.setImage(userEntity.getProfilePictureUrl());
        return featuredAuthor;
    }

    public List<PopularArticle> getPopularArticles() {
        List<BlogPost> posts = blogPostRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .limit(4)
                .map(this::convertToPopularArticle)
                .collect(Collectors.toList());
    }

    private PopularArticle convertToPopularArticle(BlogPost post) {
        PopularArticle article = new PopularArticle();
        article.setImage(post.getImage());
        article.setAlt(post.getAlt());
        article.setTitle(post.getTitle());
        article.setViews("1,000 views"); // Placeholder for dynamic views
        return article;
    }

    // Main method for blog data, returns the DTO version
    public BlogResponse getBlogData() {
        BlogResponse response = new BlogResponse(); // Instantiate DTO version

        BlogData blogData = new BlogData(); // This remains the Model if it's a simple POJO
        blogData.setTitle("Blog | Publication");
        blogData.setDescription("Exploring innovations in STEM Education");
        blogData.setFeaturedPost(getFeaturedPost()); // This returns DTO now
        response.setBlogData(blogData);

        response.setCategories(getAllCategoriesDto()); // This returns DTOs
        response.setBlogPosts(getAllPostsDto()); // This returns DTOs
        response.setPopularArticles(getPopularArticles());

        return response;
    }

    // Methods for reactions (remain unchanged as they operate on entities)
    @Transactional
    public void incrementCommentCount(Long blogPostId) {
        blogPostRepository.findById(blogPostId).ifPresent(blogPost -> {
            if (blogPost.getReactions() == null) {
                blogPost.setReactions(new Reactions(0, 1));
            } else {
                blogPost.getReactions().setComments(blogPost.getReactions().getComments() + 1);
            }
            blogPostRepository.save(blogPost);
        });
    }

    @Transactional
    public void decrementCommentCount(Long blogPostId) {
        blogPostRepository.findById(blogPostId).ifPresent(blogPost -> {
            if (blogPost.getReactions() != null && blogPost.getReactions().getComments() > 0) {
                blogPost.getReactions().setComments(blogPost.getReactions().getComments() - 1);
                blogPostRepository.save(blogPost);
            }
        });
    }
}
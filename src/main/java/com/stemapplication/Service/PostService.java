package com.stemapplication.Service;

import com.stemapplication.Models.*;
import com.stemapplication.Repository.BlogPostRepository;
import com.stemapplication.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostService(BlogPostRepository blogPostRepository,
                       CategoryRepository categoryRepository) {
        this.blogPostRepository = blogPostRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public BlogPost createPost(BlogPost post) {
        return blogPostRepository.save(post);
    }

    // READ
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc();
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id).orElse(null);
    }

    public List<BlogPost> getPostsByCategory(String categoryId) {
        if ("all".equals(categoryId)) {
            return getAllPosts();
        }
        return blogPostRepository.findByCategoryId(categoryId);
    }

    // UPDATE
    public BlogPost updatePost(Long id, BlogPost postDetails) {
        return blogPostRepository.findById(id)
                .map(post -> {
                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    post.setImage(postDetails.getImage());
                    post.setAlt(postDetails.getAlt());
                    post.setCategory(postDetails.getCategory());
                    return blogPostRepository.save(post);
                })
                .orElse(null);
    }

    // DELETE
    public boolean deletePost(Long id) {
        if (blogPostRepository.existsById(id)) {
            blogPostRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Additional methods
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public FeaturedPost getFeaturedPost() {
        List<BlogPost> posts = getAllPosts();
        if (posts.isEmpty()) {
            return null;
        }
        BlogPost firstPost = posts.get(0);
        FeaturedPost featuredPost = new FeaturedPost();
        featuredPost.setTitle(firstPost.getTitle());
        featuredPost.setExcerpt(firstPost.getContent().substring(0,
                Math.min(150, firstPost.getContent().length())) + "...");
        return featuredPost;
    }

    public List<PopularArticle> getPopularArticles() {
        List<BlogPost> posts = getAllPosts();
        return posts.stream()
                .limit(4)
                .map(this::convertToPopularArticle)
                .toList();
    }

    private PopularArticle convertToPopularArticle(BlogPost post) {
        PopularArticle article = new PopularArticle();
        article.setImage(post.getImage());
        article.setAlt(post.getAlt());
        article.setTitle(post.getTitle());
        article.setViews("1,000 views");
        return article;
    }

    public BlogResponse getBlogData() {
        BlogResponse response = new BlogResponse();

        BlogData blogData = new BlogData();
        blogData.setTitle("Blog | Publication");
        blogData.setDescription("Exploring innovations in STEM Education");
        blogData.setFeaturedPost(getFeaturedPost());
        response.setBlogData(blogData);

        response.setCategories(getAllCategories());
        response.setBlogPosts(getAllPosts());
        response.setPopularArticles(getPopularArticles());

        return response;
    }
}
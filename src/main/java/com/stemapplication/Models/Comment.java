//package com.stemapplication.Models;
//
//import java.util.List;
//
//public class Comment {
//    private String author;
//    private String initials;
//    private String time;
//    private String content;
//    private int likes;
//    private List<Comment> replies;
//
//    // Constructors
//    public Comment() {
//    }
//
//    // Getters and Setters
//    public String getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public String getInitials() {
//        return initials;
//    }
//
//    public void setInitials(String initials) {
//        this.initials = initials;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public int getLikes() {
//        return likes;
//    }
//
//    public void setLikes(int likes) {
//        this.likes = likes;
//    }
//
//    public List<Comment> getReplies() {
//        return replies;
//    }
//
//    public void setReplies(List<Comment> replies) {
//        this.replies = replies;
//    }
//
//    // equals and hashCode
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Comment comment = (Comment) o;
//        return author.equals(comment.author) &&
//                time.equals(comment.time) &&
//                content.equals(comment.content);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = author.hashCode();
//        result = 31 * result + time.hashCode();
//        result = 31 * result + content.hashCode();
//        return result;
//    }
//
//    // toString
//    @Override
//    public String toString() {
//        return "Comment{" +
//                "author='" + author + '\'' +
//                ", initials='" + initials + '\'' +
//                ", time='" + time + '\'' +
//                ", content='" + content + '\'' +
//                ", likes=" + likes +
//                ", replies=" + replies +
//                '}';
//    }
//}



package com.stemapplication.Models;

import com.stemapplication.Models.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private String author; // Can be a guest name or a registered user's display name

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // Assuming comments are linked to a Post
    private Post post; // You'll need a Post entity or replace this with a postId string

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Optional: if comments can be made by registered users
    private UserEntity user; // The registered user who made the comment (can be null for guests)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    // Optional: Fields to track moderation
    private LocalDateTime dateModerated;
    private String moderatedBy; // Username of the admin/superadmin who moderated it
}

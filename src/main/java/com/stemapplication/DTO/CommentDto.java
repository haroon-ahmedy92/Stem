package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private boolean approved;
    private String guestAuthorName; // For guest comments
    // For registered user comments, you might include a UserDto here if needed,
    // but the `Comment` entity currently doesn't store a direct UserEntity reference for the commenter,
    // only for `approvedBy`.
    // If you add private UserEntity commenter; to Comment model, then add private UserDto commenter; here.
    // private UserDto commenter;

    // For moderation panel, you might include who approved it
    private LocalDateTime approvedAt;
    private UserDto approvedBy; // Use UserDto for the approver
}
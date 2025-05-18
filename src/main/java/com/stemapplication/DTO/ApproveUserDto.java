package com.stemapplication.DTO;

import lombok.Data;

@Data
public class ApproveUserDto {
    private Long userId;
    // private String roleToAssign; // Optional: if admin can set role during approval. For now, approval just activates. Promotion is separate.
}
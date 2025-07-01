package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMembersResponseDto {
    
    private List<TeamMemberDto> teamMembers;
    private PaginationDto pagination;
}

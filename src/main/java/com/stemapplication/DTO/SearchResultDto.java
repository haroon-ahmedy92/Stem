package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String type;
    private Long id;
    private String title;
    private String description;
    private String section;
    @JsonProperty("relevance_score")
    private Double relevanceScore;
    private String url;
}

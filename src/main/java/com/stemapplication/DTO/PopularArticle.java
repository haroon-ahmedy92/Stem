package com.stemapplication.DTO;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PopularArticle {
    private String image;
    private String alt;
    private String title;
    private String views;

}
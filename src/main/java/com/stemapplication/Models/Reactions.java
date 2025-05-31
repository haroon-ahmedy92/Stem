package com.stemapplication.Models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Reactions {
    private int likes = 0;
    private int comments = 0;

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reactions reactions = (Reactions) o;
        return likes == reactions.likes &&
                comments == reactions.comments;
    }

    @Override
    public int hashCode() {
        int result = likes;
        result = 31 * result + comments;
        return result;
    }

    // toString
    @Override
    public String toString() {
        return "Reactions{" +
                "likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}
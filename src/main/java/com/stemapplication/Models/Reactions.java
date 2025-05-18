package com.stemapplication.Models;

import jakarta.persistence.Embeddable;

@Embeddable
public class Reactions {
    private int likes;
    private int comments;

    // Constructors
    public Reactions() {
    }

    public Reactions(int likes, int comments) {
        this.likes = likes;
        this.comments = comments;
    }

    // Getters and Setters
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

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
package com.example.myapplication123;

public class Post {
    private String postId;
    private String title;
    private String description;
    private String imageUrl; // Поле для URL изображения

    public Post() {
        // Пустой конструктор требуется для Firebase
    }

    public Post(String postId, String title, String description, String imageUrl) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

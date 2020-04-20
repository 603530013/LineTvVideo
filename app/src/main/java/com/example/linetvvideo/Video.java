package com.example.linetvvideo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "VideoTable")
public class Video {
    @PrimaryKey
    private int drama_id;

    private String name = "";
    private int total_views;
    private String created_at = "";
    private String thumb = "";
    private double rating;

    public Video(int drama_id, String name, int total_views, String created_at, String thumb, double rating) {
        this.drama_id = drama_id;
        this.total_views = total_views;
        this.name = name;
        this.created_at = created_at;
        this.thumb = thumb;
        this.rating = rating;
    }

    public int getDrama_id() {
        return drama_id;
    }

    public int getTotal_views() {
        return total_views;
    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getThumb() {
        return thumb;
    }

    public double getRating() {
        return rating;
    }

    public void setDrama_id(int drama_id) {
        this.drama_id = drama_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotal_views(int total_views) {
        this.total_views = total_views;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

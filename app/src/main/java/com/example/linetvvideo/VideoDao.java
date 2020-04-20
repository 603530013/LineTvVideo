package com.example.linetvvideo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM VideoTable")
    LiveData<List<Video>> getAllVideos();

    @Query("SELECT * FROM VideoTable WHERE drama_id = :id")
    Video getVideoById(int id);

    @Query("SELECT * FROM VideoTable WHERE name LIKE '%' || :context || '%'")
    List<Video> getVideoByContext(String context);

    @Insert
    void insert(Video video);

    @Insert
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);
}

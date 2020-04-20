package com.example.linetvvideo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = { Video.class }, version = 1)
public abstract class VideoDb extends RoomDatabase {
    private static final String DB_NAME = "VideoDb.db";
    private static volatile VideoDb instance;

    static synchronized VideoDb getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static VideoDb create(final Context context) {
        return Room.databaseBuilder(
                context,
                VideoDb.class,
                DB_NAME).build();
    }
    public abstract VideoDao getVideoDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}

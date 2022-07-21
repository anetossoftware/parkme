package com.anetos.parkme.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.anetos.parkme.data.database.dao.NoteDao;
import com.anetos.parkme.data.model.Note;


@Database(
        entities = {Note.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "db_anetosnote";
    private static AppDatabase appDb = null;

    public static AppDatabase getInstance(Context context) {
        if (appDb == null) {
            appDb = buildDatabaseInstance(context);
        }
        return appDb;
    }

    private static AppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME
        ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    abstract NoteDao noteDao();
}

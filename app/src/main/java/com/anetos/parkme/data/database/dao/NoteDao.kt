package com.anetos.parkme.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anetos.parkme.data.model.Note

@Dao
public interface NoteDao {
    @Query("Select * from Note")
    fun getAll(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(articles: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articles: Note)

    @Query("DELETE FROM Note")
    fun deleteAll()
}
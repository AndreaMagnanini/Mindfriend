package com.example.mindfriend.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mindfriend.Model.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NotesDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);
    @Delete
    void deleteNOte(Note note);
    @Update
    void updateNote(Note note);
    @Query("SELECT * FROM notes")
    List<Note> getNotes();
    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(int noteId);
    @Query("DELETE FROM notes WHERE id = :noteId")
    void deleteNoteById(int noteId);
}

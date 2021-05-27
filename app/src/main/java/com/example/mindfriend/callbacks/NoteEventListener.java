package com.example.mindfriend.callbacks;

import com.example.mindfriend.Model.Note;

public interface NoteEventListener {

    void onNoteClick(Note note);

    void onNoteLongClick(Note note);
}

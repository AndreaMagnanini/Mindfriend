package com.example.mindfriend.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo (name="title")
    private String noteTitle;
    @ColumnInfo(name="text")
    private String noteText;
    @ColumnInfo(name="date")
    private long noteDate;
    @Ignore
    private boolean checked = false;
    @Ignore
    private boolean isNew=true;

    public Note(String noteTitle, String noteText, long noteDate) {
        this.noteTitle= noteTitle;
        this.noteText = noteText;
        this.noteDate = noteDate;
    }

    public Note() {
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked(){ return checked;}

    public void setChecked(boolean checked){this.checked=checked;}

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public String toString() {
        return "Note{" + noteTitle +
                "id=" + id +
                ", noteDate=" + noteDate +
                '}';
    }
}

package com.example.mindfriend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mindfriend.DB.NotesDB;
import com.example.mindfriend.DB.NotesDao;
import com.example.mindfriend.Model.Note;
import com.example.mindfriend.callbacks.NoteEventListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.Date;

import static com.example.mindfriend.MainActivity.THEME_Key;

public class EditNoteActivity extends AppCompatActivity{
    private EditText inputTitle;
    private EditText inputNote;
    private NotesDao dao;
    private Note temp;
    public static final String NOTE_EXTRA_Key = "note_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.Theme_MindFriend);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.edit_note_activity_toolbar);
        setSupportActionBar(toolbar);
        inputTitle = findViewById(R.id.input_title);
        inputNote = findViewById(R.id.input_note);
        dao = NotesDB.getInstance(this).notesDao();
        if(getIntent().getExtras() !=null){
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_Key, 0);
            temp = dao.getNoteById(id);
            inputTitle.setText(temp.getNoteTitle());
            inputNote.setText(temp.getNoteText());
        }else temp=new Note();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.save_note)
            onSaveNote();
        return super.onOptionsItemSelected(item);
    }

    private void onSaveNote() {
        String title =inputTitle.getText().toString();
        String text = inputNote.getText().toString();
        if(!text.isEmpty()){
            long date = new Date().getTime();
            temp.setNoteDate(date);
            temp.setNoteText(text);
            if(!title.isEmpty())
                temp.setNoteTitle(title);
            else
                temp.setNoteTitle("Untitled");
            if(temp.isNew()){
                temp.setNew(false);
                dao.insertNote(temp);
            }
            else dao.updateNote(temp);

            finish();
        }
        else Toast.makeText(this, "Add text to your note", Toast.LENGTH_SHORT).show();
    }


}
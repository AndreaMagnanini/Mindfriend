package com.example.mindfriend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.mindfriend.DB.NotesDB;
import com.example.mindfriend.DB.NotesDao;
import com.example.mindfriend.Model.Note;
import com.example.mindfriend.adapters.NotesAdapter;
import com.example.mindfriend.callbacks.MainActionModeCallback;
import com.example.mindfriend.callbacks.NoteEventListener;
import com.example.mindfriend.utils.NoteUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.SubMenu;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.mindfriend.EditNoteActivity.NOTE_EXTRA_Key;
import static com.example.mindfriend.R.id.action_guide;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NoteEventListener, Drawer.OnDrawerItemClickListener {

    public static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapters;
    private NotesDao dao;
    private MainActionModeCallback mainActionModeCallback;
    private int checkCount = 0;
    private FloatingActionButton fab;


    private SharedPreferences settings;
    public static final String APP_PREFERENCES="MindFriend_settings";
    public static final String THEME_Key="app_theme";

    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme=settings.getInt(THEME_Key, R.style.Theme_MindFriend);
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigation(savedInstanceState, toolbar);

        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar){
        List<IDrawerItem> iDrawerItems = new ArrayList<>();
        iDrawerItems.add(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_baseline_home_24));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Notes").withIcon(R.drawable.ic_baseline_sticky_note_2_24));
        iDrawerItems.add(new PrimaryDrawerItem()
                .withName("Reminders")
                .withIcon(R.drawable.ic_baseline_event_note_24)
                .withOnDrawerItemClickListener( new Drawer.OnDrawerItemClickListener() {
         @Override
         public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            new AlertDialog.Builder(MainActivity.this).setMessage("Open Reminder?").setPositiveButton("Open", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                Intent intent=getPackageManager().getLaunchIntentForPackage("com.example.projectkm");
                startActivity(intent);
            }
       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){ dialogInterface.dismiss();
           }
        }).setCancelable(false).create().show();

              return true;
        }
         }));

        List<IDrawerItem> stockyItems = new ArrayList<>();

        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem()
                .withName("Dark mode")
                .withChecked(theme == R.style.Theme_Dark)
                .withIcon(R.drawable.ic_baseline_invert_colors_24)
                .withOnCheckedChangeListener(new OnCheckedChangeListener(){
                    public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton buttonView, boolean isChecked){
                        if(isChecked){
                            settings.edit().putInt(THEME_Key, R.style.Theme_Dark).apply();
                        }else {
                            settings.edit().putInt(THEME_Key, R.style.Theme_MindFriend).apply();
                        }

                        MainActivity.this.recreate();
                    }
                });
        stockyItems.add(new PrimaryDrawerItem()
                .withName("Credits")
                .withIcon(R.drawable.ic_baseline_settings_24)
                .withOnDrawerItemClickListener( new Drawer.OnDrawerItemClickListener() {
                                                    @Override
                                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                                        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.app_name).setMessage("Applicazione sviluppata\nin collaborazione da\nArnold Kumaraku e Andrea Magnanini\n----2020/2021----\nProgetto di Programmazione\nad oggetti\n07/04/2021").setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        }).setCancelable(false).create().show();
                                                        return true;
                                                    }
                                                }));
        stockyItems.add(switchDrawerItem);

        AccountHeader header = new AccountHeaderBuilder().withActivity(this).addProfiles(new ProfileDrawerItem()
                .withEmail("By Andrea Magnanini & Arnold Kumaraku")
                .withName("MindFriend - OOP project")
                .withIcon(R.mipmap.ic_launcher_round))
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.color.colorPrimary)
                .withSelectionListEnabledForSingleProfile(false).build();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withDrawerItems(iDrawerItems)
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stockyItems)
                .withAccountHeader(header)
                .withOnDrawerItemClickListener(this).build();
    }

    private void loadNotes() {
        this.notes = new ArrayList<Note>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);
        //for(int i=0; i<12;i++){
        //    notes.add(new Note("This note is empty", new Date().getTime()));
        //}
        this.adapters = new NotesAdapter(this, this.notes);

        this.adapters.setListener(this);
        this.recyclerView.setAdapter(adapters);
        //showEmptyView();
        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
        //adapters.notifyDataSetChanged();
    }

    //private void showEmptyView(){
        //if(notes.size()==0){
            //this.recyclerView.setVisibility(View.GONE);
           // findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);
        //}else{
            //this.recyclerView.setVisibility(View.VISIBLE);
            //(findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        //}
    //}

    private void onAddNewNote() {
    //    if(notes !=null){
                //notes.add(new Note("Title","This is a new note", new Date().getTime()));
        //}
        //if(adapters!=null){
            //.notifyDataSetChanged();

        //}
        startActivity(new Intent(this, EditNoteActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        if(id == action_guide) {
            comunication();
            download();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    // metodi per aprire la pagina contenete il pdf contenente la Guida all'applicazione
    public void comunication (){
        Toast.makeText(this, "Download ...", Toast.LENGTH_SHORT).show();
    }

    public void download(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://flipbookpdf.net/web/site/d7953267ed45d24840c0069143111c2a5a72d80d202104.pdf.html")));
    }

    @Override
    public void onNoteClick(Note note) {
        //Toast.makeText(this, note.getId(), Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "OnNoteClick: " + note.toString());
        Intent edit = new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);
    }

    @Override
    public void onNoteLongClick(Note note) {
        //Log.d(TAG, "OnNoteLongClick: "+ note.getId());
        /*new AlertDialog.Builder(this).setTitle(R.string.app_name).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.deleteNOte(note);
                loadNotes();
            }
        }).setNegativeButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent share = new Intent(Intent.ACTION_SEND);
                String text = note.getNoteText() + "\n Create on: " + NoteUtils.dateFromLong(note.getNoteDate()) + "By " + getString(R.string.app_name);
                //Log.d(TAG, "onClick: " + text);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(share);
            }
        }).create().show();*/

        note.setChecked(true);
        checkCount = 1;
        adapters.setMultiCheckMode(true);
        adapters.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked());
                if(note.isChecked())
                    checkCount++;
                else
                    checkCount--;
                if(checkCount >0){
                    mainActionModeCallback.changeShareItemVisible(false);
                }else mainActionModeCallback.changeShareItemVisible(true);
                if(checkCount==0)
                    mainActionModeCallback.getAction().finish();
                mainActionModeCallback.setCount(checkCount+"/"+notes.size());
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });
        mainActionModeCallback = new MainActionModeCallback() {

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_delete_notes){
                    onDeleteMultipleNotes();
                }
                else if(menuItem.getItemId()==R.id.action_share_notes)
                    onShareNotes();
                actionMode.finish();
                return false;
            }
        };

        startActionMode(mainActionModeCallback);
        fab.setVisibility(View.GONE);
        mainActionModeCallback.setCount(checkCount+"/"+notes.size());

    }


    private void onShareNotes() {
        Note note = adapters.getCheckedNotes().get(0);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteTitle() +":\n"+
                note.getNoteText() + "\n\n Create on: "+
                NoteUtils.dateFromLong(note.getNoteDate())+
                "\n By " +getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);
    }

    private void onDeleteMultipleNotes() {
        List<Note> checkedNotes=adapters.getCheckedNotes();
        if(checkedNotes.size()!=0){

            for(Note note : checkedNotes)
                dao.deleteNOte(note);
            loadNotes();
            Toast.makeText(this, checkedNotes.size()+" Note(s) Deleted Successfully", Toast.LENGTH_SHORT).show();


        }else Toast.makeText(this, "No notes selected", Toast.LENGTH_SHORT).show();

    }

    public void onActionModeFinished (ActionMode mode){
        super.onActionModeFinished(mode);
        adapters.setMultiCheckMode(false);
        adapters.setListener(this);
        fab.setVisibility(View.VISIBLE);
    }

    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(notes!=null){
                Note swipedNote = notes.get(viewHolder.getAbsoluteAdapterPosition());
                if (swipedNote != null) {
                    swipeToDelete(swipedNote, viewHolder);
                }
            }
        }
    });

    public void swipeToDelete(Note swipedNote, RecyclerView.ViewHolder viewHolder){
        new AlertDialog.Builder(MainActivity.this).setMessage("Delete note?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                dao.deleteNOte(swipedNote);
                notes.remove(swipedNote);
                adapters.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                recyclerView.getAdapter().notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
            }
        }).setCancelable(false).create().show();

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Toast.makeText(this, " "+position, Toast.LENGTH_SHORT).show();
        return false;
    }


}
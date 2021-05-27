package com.example.mindfriend.callbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mindfriend.R;

public abstract class MainActionModeCallback implements ActionMode.Callback{
    private ActionMode action;
    private MenuItem countItem;
    private MenuItem shareItem;
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
        this.action = actionMode;
        this.countItem=menu.findItem(R.id.action_checked_count);
        this.shareItem=menu.findItem(R.id.action_share_notes);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    public void setCount(String checkedCount){
        if(countItem!=null)
            this.countItem.setTitle(checkedCount);
    }

    public ActionMode getAction(){
        return action;
    }

    public void changeShareItemVisible(boolean b) {
        shareItem.setVisible(b);
    }
}

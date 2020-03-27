package com.me.android.noteeditor;

import android.content.Intent;
import android.os.Bundle;

import com.me.android.noteeditor.CommonUtility.searchViewUtility;
import com.me.android.noteeditor.NotesClass;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class fragment_container extends CyaneaAppCompatActivity {

    @Override
    public void onBackPressed() {
        if (searchViewUtility.isSearchViewAdded()){
            searchViewUtility.onBackPressed(this, false);
            return;
        }
        NotesClass.setActivity_identifier(0);
        NavUtils.navigateUpFromSameTask(this);
        finish();
        Runtime.getRuntime().gc();
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     *
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     *
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String folderName = getIntent().getExtras().getString(getString(R.string.from_folder_to_fragment_container_intent_key));
        assert folderName != null;

        int activity_identifier = getIntent().getExtras().getInt(getString(R.string.from_folder_to_fragment_container_identify_key), 0);

        /*boolean shouldFilterTheNotesInstead = false;
        try {
            shouldFilterTheNotesInstead = getIntent().getExtras().getBoolean(getString(R.string.from_folder_to_fragment_container_boolean_argument), false);
        }catch (Exception e){
            //empty
        }*/

        actionBar.setTitle(folderName);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment notesClass = new NotesClass();

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.from_folder_to_notes_fragment_argument_key), folderName);
        bundle.putInt(getString(R.string.from_folder_to_fragment_container_identify_key)
                , activity_identifier);
        //bundle.putBoolean(getString(R.string.from_folder_to_fragment_container_boolean_argument), shouldFilterTheNotesInstead);
        notesClass.setArguments(bundle);

        ft.replace(R.id.fragmentContainer, notesClass);
        ft.commit();
    }
}

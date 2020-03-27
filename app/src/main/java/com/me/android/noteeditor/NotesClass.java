package com.me.android.noteeditor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.me.android.noteeditor.CommonUtility.searchViewUtility;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.IMM.IMMHandler;
import com.me.android.noteeditor.IMM.IMMResult;
import com.me.android.noteeditor.Recyclerview_adapter.contentClass_adapter;
import com.me.android.noteeditor.Recyclerview_adapter.itemTouchHelper.SimpleItemTouchHelper;
import com.me.android.noteeditor.Recyclerview_adapter.suggestion_adapter;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.contract.FoldersDataBase.FoldersDataBaseManager;
import com.me.android.noteeditor.contract.content_class;
import com.me.android.noteeditor.contract.searchQueryDatabase.searchQueryDatabaseManager;
import com.me.android.noteeditor.contract.searchQueryDatabase.suggestion_model;
import com.me.android.noteeditor.customListener.Adapter_refresh_listener;
import com.me.android.noteeditor.customListener.EmptyData_listener;
import com.me.android.noteeditor.customListener.EmptySuggestionListener;
import com.me.android.noteeditor.customListener.FolderCountChangeListener;
import com.me.android.noteeditor.customListener.Open_external_file_listener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.cyanea.Cyanea;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.me.android.noteeditor.CommonUtility.utilityClass.IDEAL_LIGHTEN_FACTOR;
import static com.me.android.noteeditor.CommonUtility.utilityClass.checkIfHostContainsSubstring;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getNoteTextDirectory;
import static com.me.android.noteeditor.CommonUtility.utilityClass.lightenTheColor;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;

public class NotesClass extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 23;
    private DataBaseManager dbManager;
    private FoldersDataBaseManager fdbManager;
    private searchQueryDatabaseManager sqdbManager;
    /*private ArrayList<String> titleList = new ArrayList<>();*/
    private ArrayList<content_class> datalist = new ArrayList<>();
    private contentClass_adapter arrayAdapter;
    private RecyclerView listView;
    private TextView emptyTextWarning;
    private ProgressBar recyclerViewProgressBar;

    private static final int REQUEST_EXTERNAL_FILE_CODE = 1;
    private static final int EXTERNAL_FILE_ID = -1;

    private final utilityClass utilityClass = new utilityClass();
    private final searchViewUtility svUtility = new searchViewUtility();

    Typeface typeface;

    private static Uri uri;
    static String title, content;

    private SharedPreferences sharedPreferences;

    private Bundle argumentsBundle;
    private static int activity_identifier = 0;

    private View mainView;

    public NotesClass(){
       //public empty class
    }
    /*private Resources resources;

    @Override
    public Resources getResources() {
        if (resources == null){
            resources = new customResources(super.getAssets(), super.getResources().getDisplayMetrics(),
                    super.getResources().getConfiguration());
        }
        return resources;
    }*/

    public static int getActivity_identifier() {
        return activity_identifier;
    }

    public static void setActivity_identifier(int activity_identifier) {
        NotesClass.activity_identifier = activity_identifier;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.class_notes, container, false);

        setHasOptionsMenu(true);

        svUtility.init(getContext(), false);

        dbManager = new DataBaseManager(getContext());
        fdbManager = new FoldersDataBaseManager(getContext());
        sqdbManager = new searchQueryDatabaseManager(getContext());
        /*FileProvider fileProvider = new FileProvider();
        fileProvider.initiate(getActivity());*/

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        emptyTextWarning = (TextView) mainView.findViewById(R.id.empty_text);
        emptyTextWarning.setVisibility(View.GONE);

        recyclerViewProgressBar = mainView.findViewById(R.id.notes_loader_progress_bar);
        recyclerViewProgressBar.setVisibility(View.VISIBLE);

        listView = (RecyclerView) mainView.findViewById(R.id.listView);
        listView.setVisibility(View.VISIBLE);
        datalist = new ArrayList<>();

        argumentsBundle = getArguments();
        if (argumentsBundle != null){
            activity_identifier = argumentsBundle.getInt(getString(R.string.from_folder_to_fragment_container_identify_key));
        }

        listView.post(new Runnable() {
            @Override
            public void run() {
                /*if (argumentsBundle != null) {

                    String folderName = argumentsBundle.getString(getString(R.string.from_folder_to_notes_fragment_argument_key));

                    if (activity_identifier == 1) {
                        for (content_class item : dbManager.getAll()) {
                            if (item.getFolderName().equals(folderName)) {
                                datalist.add(item);
                            }
                        }
                    }
                    if (activity_identifier == 2){
                        for (content_class item : dbManager.getAll()){
                            if (checkIfHostContainsSubstring(item.getTitle(), folderName)){
                                datalist.add(item);
                            }
                        }
                    }
                }else {
                    if (dbManager.getAll() != null) {
                        datalist.addAll(dbManager.getAll());
                    }
                }*/
                try {
                    String folderName = argumentsBundle.getString(getString(R.string.from_folder_to_notes_fragment_argument_key));
                    if (activity_identifier == 1) {
                        for (content_class item : dbManager.getAll()) {
                            if (item.getFolderName().equals(folderName)) {
                                datalist.add(item);
                            }
                        }
                    }
                    else {
                        for (content_class item : dbManager.getAll()){
                            if (checkIfHostContainsSubstring(item.getTitle(), folderName)){
                                datalist.add(item);
                            }
                        }
                    }
                }catch (NullPointerException e){
                    if (dbManager.getAll() != null) {
                        datalist.addAll(dbManager.getAll());
                    }
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());/*{

                    private static final float SPEED = 300f;

                    @Override
                    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(getContext()){
                            *//**
                             * Calculates the scroll speed.
                             *
                             * @param displayMetrics DisplayMetrics to be used for real dimension calculations
                             * @return The time (in ms) it should take for each pixel. For instance, if returned value is
                             * 2 ms, it means scrolling 1000 pixels with LinearInterpolation should take 2 seconds.
                             *//*
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return SPEED/displayMetrics.densityDpi;
                            }
                        };
                        linearSmoothScroller.setTargetPosition(position);
                        startSmoothScroll(linearSmoothScroller);
                    }
                }*/

                listView.setLayoutManager(linearLayoutManager);
                arrayAdapter = new contentClass_adapter(datalist, getContext());
                listView.setAdapter(arrayAdapter);

                if (datalist.isEmpty() || datalist == null) {
                    emptyTextWarning.setVisibility(View.VISIBLE);
                }

                recyclerViewProgressBar.setVisibility(View.GONE);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                        new SimpleItemTouchHelper(arrayAdapter, listView, getActivity())
                );
                itemTouchHelper.attachToRecyclerView(listView);
            }
        });
        /*contentClass_adapter.setOnEditTitleButtonClickListener(new contentClass_adapter.EditTitleButtonClickListener() {
            @Override
            public void onEditTitleButtonClicked(final content_class dataItem, final int position) {


            }
        });*/

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                content_class content_class = dbManager.getSingleData(datalist.get(i).getId());
                Intent intent = new Intent(MainActivity.this, editingActivity.class);
                intent.putExtra(getString(R.string.key_value_title), content_class.getTitle());
                intent.putExtra(getString(R.string.key_value_content), content_class.getContent());
                intent.putExtra(getString(R.string.key_value_id), content_class.getId());
                startActivity(intent);
            }
        });*/

        EmptyData_listener.setOnDataEmptyListener(new EmptyData_listener.EmptyDataListener() {
            @Override
            public void onDataEmpty(boolean isDataEmpty) {
                if (isDataEmpty){
                    emptyTextWarning.setVisibility(View.VISIBLE);
                }else emptyTextWarning.setVisibility(View.GONE);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddingDataActivity.class);
                startActivity(intent);
            }
        });
        setTint(getContext(), R.drawable.ic_add_black_24dp, fab);
        return mainView;
    }

    private void KeyboardHiding(View editTitle){
        InputMethodManager imm = IMMHandler.getIMManager(getContext());
        IMMResult result = IMMHandler.getIMMResult();
        imm.showSoftInput(editTitle, 0, result);
        int res = result.getResult();
        if (res == InputMethodManager.RESULT_UNCHANGED_SHOWN){
            View view1 = getActivity().getCurrentFocus();

            if(view1 == null) view1 = new View(getContext());

            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            /*confirmKKeyboardIsHidden(true);*/
        }
        /*confirmKKeyboardIsHidden(false);*/
    }

    //this listener is useless, remove it later on
    private interruptThread mInterruptThread;
    private void confirmKKeyboardIsHidden(boolean value){
        mInterruptThread.isKeyboardHidden(value);
    }
    private void keyboardHiddenListener(interruptThread interruptThread){
        mInterruptThread = interruptThread;
    }
    private interface interruptThread{
        void isKeyboardHidden(boolean value);
    }

    @Deprecated
    //todo : delete this method as it's not complete and will never be used.
    private int checkReadRationalePermission(){
        int requestRationale = -1;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            AlertDialog.Builder permissionDialog = new AlertDialog.Builder(getContext());
            permissionDialog.setMessage(getString(R.string.permission_denied));
            permissionDialog.setPositiveButton(getString(R.string.permission_allow_text)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity()
                                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                        }
                    });
            requestRationale = 1;
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(getContext());
            permissionDialog.setMessage(getString(R.string.permission_denied_upon_request));
            permissionDialog.setPositiveButton("OK", null);
            requestRationale = 0;
        }
        return requestRationale;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

        //Associate the search manager with thr search view
        /*SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_view_implementation).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final AlertDialog.Builder deleteAllDialog = new AlertDialog.Builder(getContext());
            deleteAllDialog.setMessage(getString(R.string.confirm_deleteAll_message));
            deleteAllDialog.setPositiveButton(getString(R.string.confirm_delete), null);
            deleteAllDialog.setNegativeButton(getString(R.string.confirm_cancel), null);

            final AlertDialog deleteAll = utilityClass.DialogBlur(getContext(), deleteAllDialog);

            deleteAll.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    utilityClass.cancelDialog();
                }
            });


            deleteAll.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (datalist.isEmpty()){
                        Toast.makeText(getContext(), getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }else {
                        if (activity_identifier == 1) {
                            String str =
                                    argumentsBundle.getString(getString(R.string.from_folder_to_notes_fragment_argument_key));
                            for (content_class item : dbManager.getAll()) {
                                if (item.getFolderName().equals(str)) {
                                    dbManager.deleteEntry(item.getId());
                                }
                            }
                            datalist.clear();
                            listView.setVisibility(View.GONE);
                            emptyTextWarning.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                            try {
                                FolderCountChangeListener.confirmFolderCountChangeListner(true);
                                Adapter_refresh_listener.confirmAdapterRefresh(true);
                            } catch (NullPointerException e) {
                                //empty
                            }
                        }else if (activity_identifier == 2){
                            return;
                        } else {
                            dbManager.deleteAll();
                            Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                            datalist.clear();
                            listView.setVisibility(View.GONE);
                            emptyTextWarning.setVisibility(View.VISIBLE);
                            try {
                                FolderCountChangeListener.confirmFolderCountChangeListner(true);
                                Adapter_refresh_listener.confirmAdapterRefresh(true);
                            } catch (NullPointerException e) {
                                //empty
                            }
                        }
                    }
                    utilityClass.cancelDialog();
                }
            });

            deleteAll.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilityClass.cancelDialog();
                }
            });

            return true;
        }/*else if (id == R.id.action_settingsOpen){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }*/else if (id == R.id.open_external_file){
            /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivityForResult(intent, REQUEST_EXTERNAL_FILE_CODE);
            }*/
            Open_external_file_listener.confirmOpenFile(true);
            return true;
        }else if (id == R.id.action_customSettings){
            startActivity(new Intent(getActivity(), customSettingsScreen.class));
            return true;
        }else if (id == R.id.search_view_implementation){
            svUtility.show(lightenTheColor(Cyanea.getInstance().getBackgroundColor(), IDEAL_LIGHTEN_FACTOR));
            final suggestion_adapter suggestion_adapter =
                    setSuggestionAdapter(lightenTheColor(Cyanea.getInstance().getBackgroundColor(), IDEAL_LIGHTEN_FACTOR));
            //final EditText editText = svUtility.getEdittext();
            /*svUtility.getSearchButton().setVisibility(View.GONE);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            svUtility.getHintSearchIcon().setVisibility(View.VISIBLE);*/

            EmptySuggestionListener.setOnEmptySuggestionListener(new EmptySuggestionListener.emptySuggestionListener() {
                @Override
                public void onSuggestionEmpty(boolean isFolderListEmpty) {
                    if (isFolderListEmpty){
                        svUtility.getEmptySearchResultText().setText(getString(R.string.no_search_history));
                        svUtility.getEmptySearchResultText().setVisibility(View.VISIBLE);
                    }else svUtility.getEmptySearchResultText().setVisibility(View.GONE);
                }
            });
            svUtility.setSearchViewTextChangeListener(new searchViewUtility.SearchViewTextChangeListener() {
                @Override
                public void textChangeListener(Editable editable) {
                    RecyclerView recyclerView = svUtility.getRecyclerView();
                    if (recyclerView.getVisibility() != View.VISIBLE)
                        recyclerView.setVisibility(View.VISIBLE);
                    if (editable.toString().isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        svUtility.getEdittextClearButton().setVisibility(View.GONE);
                    }
                    setSuggestion(suggestion_adapter, editable.toString());
                }
            });
            return true;
        }
        /*else if (id == R.id.open_folder){
            startActivity(new Intent(getActivity(), fragment_container.class));
            *//*intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 962);*//*
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*private void setSuggestionHistoryData(String str, int associated_id, int type, int isHistory){
        if (!str.isEmpty())
            sqdbManager.insertQueryEntry(str, associated_id, type, isHistory);
    }*/

    private void setSuggestion(suggestion_adapter suggestion_adapter, String str){
        ArrayList<content_class> arrayList = dbManager.getAll();
        //ArrayList<Folder_content_class> arrayList1 = (ArrayList<Folder_content_class>) fdbManager.getAll();
        ArrayList<suggestion_model> arrayList2 = sqdbManager.getAll();
        ArrayList<suggestion_model> arrayList3 = new ArrayList<>();

        for (content_class item : arrayList){
            if (checkIfHostContainsSubstring(item.getTitle(), str)){
                arrayList3.add(new suggestion_model(item.getTitle(), item.getId(), suggestion_model.TYPE_NOTE, suggestion_model.ISNOTHISTORY));
            }
        }

        /*for (Folder_content_class item : arrayList1){
            if (checkIfHostContainsSubstring(item.getFolderName(), str)){
                arrayList3.add(new suggestion_model(item.getFolderName(), item.getId(), suggestion_model.TYPE_FOLDER, suggestion_model.ISNOTHISTORY));
            }
        }*/

        for (suggestion_model item : arrayList2){
            if (checkIfHostContainsSubstring(item.getSuggestionText(), str)){
                arrayList3.add(new suggestion_model(item.getId(), item.getSuggestionText(), item.getAssociated_id(), item.getType(), suggestion_model.ISHISTORY));
            }
        }

        if (arrayList3.isEmpty()){
            svUtility.getEmptySearchResultText().setText(getString(R.string.empty_search_text));
           svUtility.getEmptySearchResultText().setVisibility(View.VISIBLE);
           svUtility.getRecyclerView().setVisibility(View.GONE);
        }else {
            if (svUtility.getRecyclerView().getVisibility() != View.VISIBLE)
                svUtility.getRecyclerView().setVisibility(View.VISIBLE);
            if (svUtility.getEmptySearchResultText().getVisibility() == View.VISIBLE)
                svUtility.getEmptySearchResultText().setVisibility(View.GONE);
            suggestion_adapter.setData(arrayList3);
        }
    }

    private suggestion_adapter setSuggestionAdapter(int color){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        if (sqdbManager.getAll().isEmpty() || sqdbManager.getAll() == null){
            svUtility.getEmptySearchResultText().setText(getString(R.string.no_search_history));
            svUtility.getEmptySearchResultText().setVisibility(View.VISIBLE);
            svUtility.getRecyclerView().setVisibility(View.GONE);
        }else {
            svUtility.getEmptySearchResultText().setVisibility(View.GONE);
            svUtility.getRecyclerView().setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = svUtility.getRecyclerView();
        recyclerView.setLayoutManager(linearLayoutManager);
        suggestion_adapter suggestion_adapter = new suggestion_adapter(getContext(), sqdbManager.getAll()
                , svUtility.getEdittext(), color);
        recyclerView.setAdapter(suggestion_adapter);
        return suggestion_adapter;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getActivity().invalidateOptionsMenu();
        if (datalist.isEmpty()) {
            menu.findItem(R.id.action_settings).setVisible(false);
            return true;
        }
        else
            return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().invalidateOptionsMenu();
        if (datalist.isEmpty() || activity_identifier == 2)
            menu.findItem(R.id.action_settings).setVisible(false);
    }
    /*private int fetchPrimaryColor(){
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int colorAccent = typedArray.getColor(0, ContextCompat.getColor(this, R.color.colorPrimary));
        typedArray.recycle();

        return colorAccent;
    }*/
}

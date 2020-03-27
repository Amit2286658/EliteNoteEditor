package com.me.android.noteeditor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.ThemeUtils.ThemeChangeListener;
import com.me.android.noteeditor.fontManager.fontManager;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity;
import com.jaredrummler.cyanea.prefs.CyaneaThemePickerActivity;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;

public class customSettingsScreen extends CyaneaAppCompatActivity {

    private final int BASE_THEME = 1;
    private final int LIGHT_THEME = 2;
    private final int DARK_THEME = 3;

    public static final int ROUNDD_THUMBNAIL = 1, SQUARE_THUMBNAIL = 2;

    private SharedPreferences sp;

    private SharedPreferences.Editor spEditor;

    private utilityClass util;

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        finish();
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

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_settings_screen);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        util = new utilityClass();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpTextSizeSelector();
        setUpFontSelector();
        setUpHtmlSelector();
        setUpThemeSelector();
        setupMoreTheme();
        setupThemeSettings();
        setUpAbout();
        setUpCaches();
        setUpDownscaledBitmaps();
        setUpRoundedThumbnails();

        /*ThemeChangeListener.setOnThemeChangeListener(new ThemeChangeListener.themeChangeListener() {
            @Override
            public void isThemeChanged(boolean isThemeChanged, int themeIdentifier) {
                if (isThemeChanged) {
                    *//*ThemeUtils.RecreateActivity(Settings.this);*//*
                    switch (themeIdentifier){
                        case ThemeUtils.BASE_THEME :
                            getCyanea().edit()
                                    .baseTheme(Cyanea.BaseTheme.LIGHT)
                                    .accentResource(R.color.black)
                                    .primaryResource(R.color.light_pink)
                                    .backgroundResource(R.color.white_background)
                                    .apply()
                                    .recreate(customSettingsScreen.this, 50, true);
                            *//*utilityClass.restartApplication(customSettingsScreen.this);*//*
                            //Toast.makeText(customSettingsScreen.this, "Restart is required", Toast.LENGTH_LONG).show();
                            break;
                        case ThemeUtils.LIGHT_THEME :
                            getCyanea().edit()
                                    .baseTheme(Cyanea.BaseTheme.LIGHT)
                                    .accentResource(R.color.accent_material_)
                                    .primaryResource(R.color.primary_material_)
                                    .backgroundResource(R.color.white_background)
                                    .apply()
                                    .recreate(customSettingsScreen.this, 50, true);
                            *//*utilityClass.restartApplication(customSettingsScreen.this);*//*
                            //Toast.makeText(customSettingsScreen.this, "Restart is required", Toast.LENGTH_LONG).show();
                            break;
                        case ThemeUtils.DARK_THEME :
                            getCyanea().edit()
                                    .baseTheme(Cyanea.BaseTheme.DARK)
                                    .accent(Color.parseColor("#ff80cbc4"))
                                    .primary(Color.parseColor("#ff212121"))
                                    .background(Color.parseColor("#ff303030"))
                                    .apply()
                                    .recreate(customSettingsScreen.this, 50, true);
                            *//*utilityClass.restartApplication(customSettingsScreen.this);*//*
                            //Toast.makeText(customSettingsScreen.this, "Restart is required", Toast.LENGTH_LONG).show();
                            break;
                        default: break;
                    }
                }
            }
        });

        MainActivity.setOnThemeModifiedListener(new MainActivity.themeModifedListener() {
            @Override
            public void on_theme_modified(boolean isThemeModified) {
                if (isThemeModified)
                    ThemeUtils.RecreateActivity(customSettingsScreen.this);
            }
        });*/
    }

    private void setUpTextSizeSelector(){
        int textSize;
        textSize = sp.getInt(getString(R.string.custom_text_size_key),
                1);
        SeekBar textSizeSeekbar = (SeekBar)findViewById(R.id.edit);
        textSizeSeekbar.setMax(8);
        textSizeSeekbar.setProgress(textSize);
        textSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                spEditor.putInt(getString(R.string.custom_text_size_key),
                        progress);
                spEditor.apply();
            }
        });
    }
    private void setUpFontSelector(){

        final String initialFont = sp.getString(
                getString(R.string.font_preference_key),
                "Aller_Lt"
        );

        final fontManager fm = new fontManager(this);
        fm.initializeFontManager();

        final Spinner fontSelector = findViewById(R.id.fontSelectionSpinner);

        final ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("Loading");

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, R.layout.spinner_item_1, arrayList1);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        fontSelector.post(new Runnable() {
            @Override
            public void run() {
                if (!arrayList1.isEmpty()) arrayList1.clear();
                if (!arrayAdapter.isEmpty()) arrayAdapter.clear();
                try {
                    for (String str : fm.listAllFileNames()) {
                        if (str.endsWith(".ttf")){
                            arrayAdapter.add(str.replace(".ttf", ""));
                        }else if (str.endsWith(".otf")){
                            arrayAdapter.add(str.replace(".otf", ""));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fontSelector.setAdapter(arrayAdapter);
                fontSelector.setSelection(arrayAdapter.getPosition(initialFont));
            }
        });

        fontSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String font = (String) parent.getItemAtPosition(position);
                spEditor.putString(
                        getString(R.string.font_preference_key),
                        font
                ).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing here
            }
        });
    }

    private void setUpHtmlSelector(){
        boolean isChecked = sp.getBoolean(getString(R.string.html_selector_preference_key), false);

        AppCompatCheckBox compatCheckBox = findViewById(R.id.html_checkBox);

        if (isChecked)
            compatCheckBox.setChecked(true);
        else compatCheckBox.setChecked(false);

        compatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    spEditor.putBoolean(getString(R.string.html_selector_preference_key), true).apply();
                }else
                    spEditor.putBoolean(getString(R.string.html_selector_preference_key), false).apply();
            }
        });

    }

    private void setUpThemeSelector(){

        int theme = sp.getInt(
                getString(R.string.customThemeChangekey),
                BASE_THEME
        );

        //check images
        final ImageView baseThemeCheckImage = (ImageView)findViewById(R.id.BaseThemeCheckImage);
        final ImageView DarkThemeImagecheck = (ImageView)findViewById(R.id.DarkThemeCheckImage);
        final ImageView LightThemeCheckImage = (ImageView)findViewById(R.id.LightthemeCheckImage);

        //setting the initial visibility state
        baseThemeCheckImage.setVisibility(View.INVISIBLE);
        DarkThemeImagecheck.setVisibility(View.INVISIBLE);
        LightThemeCheckImage.setVisibility(View.INVISIBLE);

        switch (theme){
            case BASE_THEME :
                baseThemeCheckImage.setVisibility(View.VISIBLE);
                break;
            case LIGHT_THEME :
                LightThemeCheckImage.setVisibility(View.VISIBLE);
                break;
            case DARK_THEME :
                DarkThemeImagecheck.setVisibility(View.VISIBLE);
                break;
                default: break;
        }

        FrameLayout baseTheme = (FrameLayout) findViewById(R.id.BaseThemeCheckFrameLayout);
        baseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spEditor.putInt(
                        getString(R.string.customThemeChangekey),
                        BASE_THEME
                ).apply();

                baseThemeCheckImage.setVisibility(View.VISIBLE);
                DarkThemeImagecheck.setVisibility(View.INVISIBLE);
                LightThemeCheckImage.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, BASE_THEME);
            }
        });
        FrameLayout lightTheme = (FrameLayout) findViewById(R.id.LightThemeCheckFrameLayout);
        lightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spEditor.putInt(
                        getString(R.string.customThemeChangekey),
                        LIGHT_THEME
                ).apply();

                LightThemeCheckImage.setVisibility(View.VISIBLE);
                baseThemeCheckImage.setVisibility(View.INVISIBLE);
                DarkThemeImagecheck.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, LIGHT_THEME);
            }
        });
        FrameLayout darkTheme = (FrameLayout) findViewById(R.id.DarkThemeCheckFrameLayout);
        darkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spEditor.putInt(
                        getString(R.string.customThemeChangekey),
                        DARK_THEME
                ).apply();

                DarkThemeImagecheck.setVisibility(View.VISIBLE);
                baseThemeCheckImage.setVisibility(View.INVISIBLE);
                LightThemeCheckImage.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, DARK_THEME);
            }
        });
    }

    private void setupMoreTheme(){
        View more_themes_preference = findViewById(R.id.more_themes_custom_preference_id);
        more_themes_preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(customSettingsScreen.this, CyaneaThemePickerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupThemeSettings(){
        View theme_settings = findViewById(R.id.theme_configuration_custom_preference_id);
        theme_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(customSettingsScreen.this, CyaneaSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpAbout(){
        View about = findViewById(R.id.about_custom_preference_id);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(customSettingsScreen.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpCaches(){
        boolean isChecked = sp.getBoolean(getString(R.string.cache_preference_key), true);

        AppCompatCheckBox appCompatCheckBox = findViewById(R.id.image_chaches);

        appCompatCheckBox.setChecked(isChecked);

        appCompatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spEditor.putBoolean(getString(R.string.cache_preference_key), isChecked).apply();
            }
        });

    }

    private void setUpDownscaledBitmaps(){

        boolean isChecked = sp.getBoolean(getString(R.string.downscaled_bitmaps_preference_key), true);

        AppCompatCheckBox compatCheckBox = findViewById(R.id.dowscaled_bitmaps);

        compatCheckBox.setChecked(isChecked);

        compatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spEditor.putBoolean(getString(R.string.downscaled_bitmaps_preference_key), isChecked).apply();
            }
        });

    }

    private void setUpRoundedThumbnails(){

        View layout = findViewById(R.id.roundImageChoice);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.thumbnail_selector, null, false);
                TextView roundThumbnails, squareThumbnail;

                AlertDialog alertDialog;

                roundThumbnails = view.findViewById(R.id.round_the_thumbnails);
                squareThumbnail = view.findViewById(R.id.square_the_thumbnails);

                roundThumbnails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spEditor.putInt(getString(R.string.round_thumbnail_preference_key), ROUNDD_THUMBNAIL).apply();
                        util.cancelDialog();
                    }
                });

                squareThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spEditor.putInt(getString(R.string.round_thumbnail_preference_key), SQUARE_THUMBNAIL).apply();
                        util.cancelDialog();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(customSettingsScreen.this);
                builder.setView(view);
                builder.setTitle(getString(R.string.thumbnail_selector_dialog_title));
                builder.setPositiveButton(getString(R.string.confirm_cancel), null);

                alertDialog = util.DialogBlur(customSettingsScreen.this, builder);

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        util.cancelDialog();
                    }
                });

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        util.cancelDialog();
                    }
                });
            }
        });


        /*LinearLayout linearLayout = findViewById(R.id.roundImageChoice);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ApplyBlurOnDialog apr = new ApplyBlurOnDialog(customSettingsScreen.this);
                final AlertDialog alertDialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(customSettingsScreen.this);
                builder.setTitle(getString(R.string.thumbnail_selector_dialog_title));
                builder.setItems(R.array.roundThumbnail_settings_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0 :
                                apr.removeView();
                                spEditor.putBoolean(getString(R.string.round_thumbnail_preference_key), true).apply();
                                break;
                            case 1 :
                                apr.removeView();
                                spEditor.putBoolean(getString(R.string.round_thumbnail_preference_key), false).apply();
                        }
                    }
                });
                builder.setPositiveButton(getString(R.string.confirm_cancel), null);

                alertDialog = apr.getRealDialog(builder);

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        apr.removeView();
                        cancelDialog(alertDialog);
                    }
                });

                apr.setBackgroundDrawable(apr);

                WindowManager.LayoutParams windowManager = alertDialog.getWindow().getAttributes();
                windowManager.windowAnimations = R.style.dialog_alpha_animation;
                alertDialog.getWindow().setAttributes(windowManager);

                alertDialog.show();
                apr.clearDialogDimming(new Dialog[]{alertDialog});

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        apr.removeView();
                        cancelDialog(alertDialog);
                    }
                });

            }
        });*/

        /*boolean isChecked = sp.getBoolean(getString(R.string.round_thumbnail_preference_key), false);

        AppCompatCheckBox compatCheckBox = findViewById(R.id.round_thumbnails);

        compatCheckBox.setChecked(isChecked);

        compatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spEditor.putBoolean(getString(R.string.round_thumbnail_preference_key), isChecked).apply();
            }
        });*/
    }

    private void setUpTransparencySelector(){
        int progress = sp.getInt(
                getString(R.string.cardView_transparency_key),
                0
        );

        View parentView = LayoutInflater.from(this).inflate(R.layout.transparency_selector, null, false);
        SeekBar transparencySeekbar = (SeekBar)parentView.findViewById(R.id.transparency_level);
        transparencySeekbar.setMax(9);
        transparencySeekbar.setProgress(progress);
    }
}

package com.me.android.noteeditor;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity;
import com.jaredrummler.cyanea.prefs.CyaneaThemePickerActivity;
import com.me.android.noteeditor.AboutActivity;
import com.me.android.noteeditor.R;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;

/*import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;*/

/*import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;*/

public class Settings extends CyaneaAppCompatActivity {

    private static final int REQUEST_CODE = 1;

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setTheme(R.style.AppTheme);*/

        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int theme_identifier = sharedPreferences.getInt(
                getString(R.string.customThemeChangekey),
                1
        );

        ThemeUtils.onCreateSetTheme(Settings.this, theme_identifier);*/



        /*imagePickerListener imagePickerListener = new imagePickerListener();
        imagePickerListener.setOnImageSelectedListener(new imagePickerListener.imageSelectedListener() {
            @Override
            public void isImageSelected(Bitmap image) {
                bitmap = image;
                image_identifier = 1;
                ThemeUtils.RecreateActivity(Settings.this);
            }
        });*/
        /*if (bitmap != null && image_identifier == 1){
            this.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            getSupportActionBar().setCustomView(findViewById(R.id.actionBarBlurView));
            BlurKit.getInstance().blur(getSupportActionBar().getCustomView(), 12);
        }*/

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new Preferences()).commit();


    }

    public static class Preferences extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            findPreference(getString(R.string.color_preference_key)).setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            startActivity(new Intent(getActivity(), CyaneaThemePickerActivity.class));
                            return true;
                        }
                    }
            );

            findPreference(getString(R.string.color_accent_preference_key)).setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            startActivity(new Intent(getActivity(), CyaneaSettingsActivity.class));
                            return true;
                        }
                    }
            );

            findPreference(getString(R.string.about_preference_key)).setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            startActivity(new Intent(getActivity(), AboutActivity.class));
                            return true;
                        }
                    }
            );

            /*DialogPreference dp = (DialogPreference) findPreference(getString(R.string.cardView_transparency_key));
            dp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ApplyBlurOnDialog apr = new ApplyBlurOnDialog(getActivity());
                    return false;
                }
            });*/

            /*findPreference(getString(R.string.image_selection_preference)).setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivityForResult(intent, REQUEST_CODE);
                            return true;
                        }
                    }
            );*/
        }

        /*@Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE){
                if (resultCode == Activity.RESULT_OK){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        *//*imagePickerListener.confirmImageSelection(bitmap);*//*
                        getActivity().getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        *//*getActivity().getSupportActionBar().setCustomView(getActivity().findViewById(R.id.actionBarBlurView));
                        BlurKit.getInstance().blur(getActivity().getActionBar().getCustomView(), 12);*//*
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/

       /* @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE){
                if (resultCode == Activity.RESULT_OK){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        imagePickerListener.confirmImageSelection(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }
}

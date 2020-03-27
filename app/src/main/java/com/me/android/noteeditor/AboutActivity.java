package com.me.android.noteeditor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinalwb.are.render.AreTextView;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.fileProviderUtility.FileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.me.android.noteeditor.CommonUtility.utilityClass;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;

import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;

public class AboutActivity extends CyaneaAppCompatActivity {

    private FileProvider fileProvider = new FileProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        fileProvider.initiate(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextView seeAllCredits = findViewById(R.id.seeAll_credits);
        seeAllCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.credits, null, false);

                final AreTextView areTextView = view.findViewById(R.id.credits_textView);

                final ProgressBar progressBar = view.findViewById(R.id.credits_loadingBar);
                progressBar.setVisibility(View.VISIBLE);

                areTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String credits = fileProvider.readAssetFileContent("credits/svg icons credit.txt");
                            areTextView.fromHtml(credits);
                            areTextView.setTextSize(12);
                        } catch (IOException e) {
                            areTextView.setText("There's a problem loading the credits file");
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                builder.setView(view);
                builder.setTitle("Credits");
                builder.setPositiveButton("Close", null);

                final utilityClass utilityClass = new utilityClass();
                AlertDialog realDialog = utilityClass.DialogBlur(AboutActivity.this, builder);
                realDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();
                    }
                });
                realDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

            }
        });


        final FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.email);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        "Amitkumar13234353@gmail.com", null));
                /*intent.setData(Uri.parse("mailto:"));*/
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Amitkumar13234353@gmail.com"});

                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(Intent.createChooser(intent, getString(R.string.send_mail_via)));
                }
                else {
                    Snackbar.make(floatingActionButton, getString(R.string.email_is_disabled_or_uninstalled), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        setTint(this, R.drawable.ic_email_black_24dp, floatingActionButton);

        final TextView freepik = (TextView)findViewById(R.id.smashiconLink);
        freepik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    intent.setData(Uri.parse(getString(R.string.smashicon_link)));
                    startActivity(intent);
                } else Snackbar.make(freepik, "No Application found", Snackbar.LENGTH_LONG).show();
            }
        });

        final TextView flaticon = (TextView)findViewById(R.id.flaticonLink);
        flaticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (intent.resolveActivity(getPackageManager()) != null){
                    intent.setData(Uri.parse(getString(R.string.about_flaticon)));
                    startActivity(intent);
                } else Snackbar.make(flaticon, "No Application found", Snackbar.LENGTH_LONG).show();
            }
        });

    }
}

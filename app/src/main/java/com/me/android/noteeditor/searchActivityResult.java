package com.me.android.noteeditor;

import android.os.Bundle;
import android.widget.Toast;

import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

public class searchActivityResult extends CyaneaAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        Toast.makeText(this, "search activity is called", Toast.LENGTH_SHORT).show();
    }
}

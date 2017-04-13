package com.glenn.talktool.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.glenn.talktool.R;

/**
 * This activity answers questions on how to accomplish tasks in the app
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        init();
    }

    public void init(){
        this.setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Help");
    }
}

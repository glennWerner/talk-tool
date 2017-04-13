/*
 * Copyright 2016. Glenn Werner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glenn.talktool.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.VideoView;

import com.glenn.talktool.R;

import java.util.List;

/**
 * This activity shows users how to add content to the app from the Gospel Library app
 * Some of the code on this page was taken from https://github.com/drmercer/Ponderizer
 */
public class AddTutorial extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check operating system version and set the layout file based on the outcome
        if(Build.VERSION.SDK_INT == 18 || Build.VERSION.SDK_INT == 19)
        {
            setContentView(R.layout.activity_add_tutorial_no_material);
        }
        else
        {
            setContentView(R.layout.activity_add_tutorial);
        }

        init();
    }

    public void init(){
        this.setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Tutorial");

        findViewById(R.id.go_gospel_library).setOnClickListener(this);
        findViewById(R.id.add_custom).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set tutorial video for the video view
        VideoView videoView = (VideoView) findViewById(R.id.tutorial_video);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.add_video));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_custom:
                Intent intent1 = new Intent(this, AddText.class);
                // Flag that will be check in AddText activity
                intent1.putExtra("From Tutorial", 1);
                startActivity(intent1);
                break;
            case R.id.go_gospel_library:
                // Check if Gospel Library is installed. If it is then send the user there. Otherwise call installGospelLibrary()
                if(isPackageInstalled(this, "org.lds.ldssa"))
                {
                    Intent intent2 = getPackageManager().getLaunchIntentForPackage("org.lds.ldssa");
                    startActivity(intent2);
                }
                else
                    installGospelLibrary();
                break;
        }
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Sends user to Google Play Store if Gospel Library is not installed
     */
    private void installGospelLibrary() {
        finish();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=" + "org.lds.ldssa")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                            + "org.lds.ldssa")));
        }
    }
}

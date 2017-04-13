package com.glenn.talktool.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.glenn.talktool.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
    }

    public void init(){
        this.setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("About");

        findViewById(R.id.apache).setOnClickListener(this);
        findViewById(R.id.rate).setOnClickListener(this);
        findViewById(R.id.report).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.apache:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
                startActivity(intent);
                break;
            case R.id.rate:
                launchMarket();
                break;
            case R.id.report:
                sendEmail();
                break;
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sends email to timo24apps@gmail.com to report problem
     */
    private void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:timo24apps@gmail.com"));
        i.putExtra(Intent.EXTRA_SUBJECT, "Issue with Talk Tool");
        i.putExtra(Intent.EXTRA_TEXT   , "Please describe what problem you encountered or feel free to give me any suggestions for improving the app!" + System.getProperty("line.separator") + System.getProperty("line.separator"));
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

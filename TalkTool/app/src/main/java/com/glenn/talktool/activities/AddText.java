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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glenn.talktool.handlers.DBHandler;
import com.glenn.talktool.tables.QuoteTable;
import com.glenn.talktool.R;
import com.glenn.talktool.tables.ScripTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This activity handles the adding of quotes or scriptures to the app
 * Some of the code on this page was taken from https://github.com/drmercer/Ponderizer
 */

public class AddText extends AppCompatActivity implements View.OnClickListener {

    /**
     * For manipulating the toolbar
     */
    public Toolbar toolbar;
    /**
     * Edit text for the title
     */
    private EditText titleTextView;
    /**
     * Edit text for the body text
     */
    private EditText bodyTextView;
    /**
     * Button for adding a scripture
     */
    public Button add_scripture;
    /**
     * Button for adding a quote
     */
    public Button add_quote;
    /**
     * Handler for the app database
     */
    private DBHandler db;
    /**
     * Key for shared preferences
     */
    public static final String MY_PREFS = "MyPrefs";
    /**
     * Key for "AddQuote" field in shared preferences
     */
    public static final String ADD_QUOTE = "AddQuote";
    /**
     * Key for "AddScripture" field in shared preferences
     */
    public static final String ADD_SCRIPTURE = "AddScripture";
    /**
     * Variable to check if user entered page from Talk Tool or Gospel Library
     */
    public int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startIntent = getIntent();
        check = startIntent.getIntExtra("From Tutorial", 0);

        // Check operating system version and set the layout file based on the outcome
        if(Build.VERSION.SDK_INT == 18 || Build.VERSION.SDK_INT == 19)
        {
            setContentView(R.layout.activity_add_text_no_material);
        }
        else
            setContentView(R.layout.activity_add_text);


        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar != null)
        {
            // If user came to this page from the Gospel Library app then hide back button on toolbar otherwise show it
            if(check != 1)
            {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setTitle("Add to Talk Tool");
            }
            else
            {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Add to Talk Tool");
            }
        }


        db = new DBHandler(this);

        titleTextView = (EditText) findViewById(R.id.add_text_title);
        bodyTextView = (EditText) findViewById(R.id.add_text_body);
        add_scripture = (Button) findViewById(R.id.addScriptureButton);
        add_quote = (Button) findViewById(R.id.addQuoteButton);

        add_scripture.setOnClickListener(this);
        add_quote.setOnClickListener(this);


        // If user entered page from app then skip block of code in if statement
        if(check != 1)
        {
            String text = startIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (text == null) {
                text = startIntent.getStringExtra(Intent.EXTRA_TITLE);
            } else {
                String titleString = startIntent.getStringExtra(Intent.EXTRA_TITLE);
                if (titleString != null && !titleString.isEmpty()) {
                    Log.e("TAG", "Title: " + titleString + " Body: " + text);
                    titleTextView.setText(titleString);
                }
            }
            if (text == null) {
                Log.e("AddText",
                        "Started without TITLE or TITLE_EXTRA");
            }

            bodyTextView.setText(cleanupText(text));


            if (titleTextView.getText().toString().isEmpty()) {
                // If we didn't get a title from the intent, try to parse the scripture reference
                if (text != null) {
                    Matcher m = Pattern.compile(
                            "lds.org/scriptures/[\\w-]+/([\\w-]+)/(\\d+).(\\d+)(?:(?:,\\d+)*,(\\d+))?"
                    ).matcher(text);
                    if (m.find()) {
                        String book = m.group(1);
                        String chap = m.group(2);
                        String verseStart = m.group(3);
                        String verseEnd = m.group(4);

                        if (book.equals("dc")) {
                            // Special case for "dc" --> "D&C"
                            book = "D&C";
                        } else {
                            // Replace all "-" characters with blank space
                            book = book.replaceAll("-", " ");
                            // Make all words uppercase, unless they are "of"
                            Matcher m1 = Pattern.compile("\\b[a-z][A-z]*\\b").matcher(book);
                            while (m1.find()) {
                                String word = m1.group();
                                if (word.equalsIgnoreCase("of"))
                                    continue; // We don't want to capitalize "of"
                                char[] wordChars = word.toCharArray();
                                wordChars[0] = Character.toUpperCase(wordChars[0]);
                                book = book.replaceFirst(word, new String(wordChars));
                            }
                        }

                        String ref; // Will be filled with the parsed scripture reference
                        if (verseEnd == null || verseEnd.isEmpty()) { // If the reference is just one verse, not a range
                            ref = String.format("%s %s:%s", book, chap, verseStart);
                        } else {
                            ref = String.format("%s %s:%s-%s", book, chap, verseStart, verseEnd);
                        }
                        titleTextView.setText(ref);
                    }
                }
            }
        }

    }

    /**
     * Gets rid of weird characters in text
     */
    private String cleanupText(String text) {
        String[] tokens = text.split("\\s"); // Split into word-like tokens
        for (String token : tokens) {
            // If the token has any slashes or carets, remove it
            if (token.matches(".*[\\\\/<>].*")) {
                text = text.replace(token, "");
            }
        }
        return text.trim(); // Trim any extra whitespace
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addScriptureButton:
                addScripToDb();
                break;
            case R.id.addQuoteButton:
                addQuoteToDb();
                break;
        }
    }

    private void addScripToDb() {
        // If nothing entered for title or author then show toast
        if(!titleTextView.getText().toString().equals(""))
        {
            // If nothing entered for body text then show toast
            if(!bodyTextView.getText().toString().matches(""))
            {
                ContentValues values = new ContentValues();
                values.put(ScripTable.ScripEntry.COLUMN_NAME_TITLE, titleTextView.getText().toString());
                values.put(ScripTable.ScripEntry.COLUMN_NAME_BODY, bodyTextView.getText().toString());
                SQLiteDatabase database = db.getWritableDatabase();
                database.insert(ScripTable.ScripEntry.TABLE_NAME, null, values);
                database.close();

                // Set flag in shared preferences for scripture added. This will be checked in the Main Activity and will
                // update table view if necessary
                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(ADD_SCRIPTURE, 1);
                editor.apply();

                // If entered page from Gospel Library then show toast and call finish() else send user to main screen
                if(check != 1)
                {
                    Toast.makeText(this, "Added scripture", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                {
                    Toast.makeText(this, "Added scripture", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
            else
                Toast.makeText(this, "Please fill all text fields", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Please fill all text fields", Toast.LENGTH_SHORT).show();
    }

    private void addQuoteToDb() {
        // If nothing entered for title or author then show toast
        if(!titleTextView.getText().toString().equals(""))
        {
            // If nothing entered for body text then show toast
            if(!bodyTextView.getText().toString().matches(""))
            {
                ContentValues values = new ContentValues();
                values.put(QuoteTable.QuoteEntry.COLUMN_NAME_TITLE, titleTextView.getText().toString());
                values.put(QuoteTable.QuoteEntry.COLUMN_NAME_BODY, bodyTextView.getText().toString());
                SQLiteDatabase database = db.getWritableDatabase();
                database.insert(QuoteTable.QuoteEntry.TABLE_NAME, null, values);
                database.close();

                // Set flag in shared preferences for quote added. This will be checked in the Main Activity and will
                // update table view if necessary
                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(ADD_QUOTE, 1);
                editor.apply();

                // If entered page from Gospel Library then show toast and call finish() else send user to main screen
                if(check != 1)
                {
                    Toast.makeText(this, "Added Quote", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
            else
                Toast.makeText(this, "Please fill all text fields", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Please fill all text fields", Toast.LENGTH_SHORT).show();
    }
}

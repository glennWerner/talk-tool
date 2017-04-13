package com.glenn.talktool.handlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.glenn.talktool.tables.NotesTable;
import com.glenn.talktool.tables.QuoteTable;
import com.glenn.talktool.tables.ScripTable;

/**
 * Handler for the app database
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UpdatedTalkInfo.db";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCRIPTURES_TABLE = "CREATE TABLE " + ScripTable.ScripEntry.TABLE_NAME + "("
                + ScripTable.ScripEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," + ScripTable.ScripEntry.COLUMN_NAME_TITLE + " TEXT,"
                + ScripTable.ScripEntry.COLUMN_NAME_BODY + " TEXT" + ")";
        db.execSQL(CREATE_SCRIPTURES_TABLE);
        String CREATE_QUOTES_TABLE = "CREATE TABLE " + QuoteTable.QuoteEntry.TABLE_NAME + "("
                + QuoteTable.QuoteEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," + QuoteTable.QuoteEntry.COLUMN_NAME_TITLE + " TEXT,"
                + QuoteTable.QuoteEntry.COLUMN_NAME_BODY + " TEXT" + ")";
        db.execSQL(CREATE_QUOTES_TABLE);
        String CREATE_NOTES_TABLE = "CREATE TABLE " + NotesTable.NotesEntry.TABLE_NAME + "("
                + NotesTable.NotesEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY,"
                + NotesTable.NotesEntry.COLUMN_NAME_BODY + " TEXT" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ScripTable.ScripEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuoteTable.QuoteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NotesTable.NotesEntry.TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

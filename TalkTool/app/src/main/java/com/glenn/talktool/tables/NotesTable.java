package com.glenn.talktool.tables;

import android.provider.BaseColumns;

/**
 * Database table for the notes
 */
public class NotesTable {

    public NotesTable(){}

    public static abstract class NotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "Noteentry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid3";
        public static final String COLUMN_NAME_BODY = "body";
    }
}

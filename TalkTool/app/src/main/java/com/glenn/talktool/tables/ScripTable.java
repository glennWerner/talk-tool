package com.glenn.talktool.tables;

import android.provider.BaseColumns;

/**
 * Database table for the scriptures
 */
public final class ScripTable {

    public ScripTable(){}

    public static abstract class ScripEntry implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BODY = "body";
    }
}

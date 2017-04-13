package com.glenn.talktool.tables;

import android.provider.BaseColumns;

/**
 * Database table for the quotes
 */
public final class QuoteTable {
    public QuoteTable(){}

    public static abstract class QuoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "QuoteEntry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid2";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BODY = "body";
    }
}

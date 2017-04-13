package com.glenn.talktool.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.glenn.talktool.R;
import com.glenn.talktool.handlers.DBHandler;
import com.glenn.talktool.tables.NotesTable;

/**
 * Fragment that takes care of the notes page of the ViewPager
 */
public class NoteFragment extends Fragment {

    /**
     * Handler for the database
     */
    private DBHandler db;
    /**
     * Edit text for notes
     */
    EditText editText;
    /**
     * String containing the notes retrieved from the database
     */
    private String notes_string = "";
    /**
     * To check if text has changed
     */
    public boolean has_changed = false;


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check database for entries and add to EditText
        db = new DBHandler(getActivity());

        SQLiteDatabase database = db.getReadableDatabase();
        Cursor c = database.query(NotesTable.NotesEntry.TABLE_NAME,null,null,null,null,null,null);
        if(c.moveToFirst()){
            do {
                notes_string = c.getString(1);
            } while (c.moveToNext());
        }
        c.close();

        editText = (EditText) getActivity().findViewById(R.id.notes_edit_text);
        editText.addTextChangedListener(textWatcher);

        editText.setText(notes_string);
        editText.setSelection(editText.getText().length());

        // Checks if keyboard is shown or dismissed
        final View activityRootView = getActivity().findViewById(R.id.note_relative_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!isKeyboardShown(editText.getRootView()))
                {
                    if(has_changed)
                    {
                        if(!editText.getText().toString().equals(""))
                        {
                            ContentValues values = new ContentValues();
                            values.put(NotesTable.NotesEntry.COLUMN_NAME_BODY, editText.getText().toString());
                            SQLiteDatabase database = db.getWritableDatabase();
                            database.insert(NotesTable.NotesEntry.TABLE_NAME, null, values);
                            database.close();
                        }
                        has_changed = false;
                    }
                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "Application closes");
        if(!editText.getText().toString().equals(""))
        {
            ContentValues values = new ContentValues();
            values.put(NotesTable.NotesEntry.COLUMN_NAME_BODY, editText.getText().toString());
            SQLiteDatabase database = db.getWritableDatabase();
            database.insert(NotesTable.NotesEntry.TABLE_NAME, null, values);
            database.close();
        }
    }

    /**
     * TextWatcher for the EditText
     */
    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        /**
         * If text has changed then set boolean has_changed to true;
         */
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            has_changed = true;
        }
    };

    /**
     * Checks if keyboard is visible
     */
    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("TAG", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }




}

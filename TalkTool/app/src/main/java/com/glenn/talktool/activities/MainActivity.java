package com.glenn.talktool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.glenn.talktool.dialogs.DeleteAllDialog;
import com.glenn.talktool.handlers.DBHandler;
import com.glenn.talktool.interfaces.LifeCycleInterface;
import com.glenn.talktool.fragments.NoteFragment;
import com.glenn.talktool.fragments.QuoteFragment;
import com.glenn.talktool.R;
import com.glenn.talktool.fragments.ScriptureFragment;
import com.glenn.talktool.tables.NotesTable;
import com.glenn.talktool.tables.QuoteTable;
import com.glenn.talktool.tables.ScripTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main activity for the application. It primarily takes care of the ViewPager
 */
public class MainActivity extends AppCompatActivity implements DeleteAllDialog.DeleteAllListener {

    /**
     * For manipulating the toolbar
     */
    public Toolbar toolbar;
    /**
     * TabLayout for the page
     */
    public TabLayout tabLayout;
    /**
     * ViewPager for the page
     */
    public ViewPager viewPager;
    /**
     * Adapter for the ViewPager
     */
    ViewPagerAdapter adapter;
    /**
     * Key for shared preferences
     */
    public static final String MY_PREFS = "MyPrefs";
    /**
     * Key for "AddScripture" field in shared preferences
     */
    public static final String ADD_SCRIPTURE = "AddScripture";
    /**
     * Key for "AddQuote" field in shared preferences
     */
    public static final String ADD_QUOTE = "AddQuote";
    /**
     * Handler for the database
     */
    public DBHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int position = 0;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Closes contextual action bar if page is changed
                if(ScriptureFragment.actionMode != null)
                    ScriptureFragment.actionMode.finish();
                else if(QuoteFragment.actionMode != null)
                    QuoteFragment.actionMode.finish();
            }
        });

        viewPager.setCurrentItem(position);
    }

    /**
     * Check if quote or scripture has been added. If so then call interface method to update content
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        int check = sharedPreferences.getInt(ADD_SCRIPTURE, 0);
        int check2 = sharedPreferences.getInt(ADD_QUOTE, 0);
        Log.e("TAG", "check: " + check);
        Log.e("TAG", "check2: " + check2);
        if(check != 0)
        {
            LifeCycleInterface fragmentToShow = (LifeCycleInterface)adapter.getItem(1);
            fragmentToShow.onResumeFragment();

            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putInt(ADD_SCRIPTURE, 0);
            editor.apply();
        }

        if(check2 != 0)
        {
            LifeCycleInterface fragmentToShow = (LifeCycleInterface)adapter.getItem(2);
            fragmentToShow.onResumeFragment();

            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putInt(ADD_QUOTE, 0);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles events from the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.item_1:
                // Show delete all dialog
                DeleteAllDialog deleteAllDialog = new DeleteAllDialog();
                deleteAllDialog.show(getFragmentManager(), "delete dialog");
                return true;
            case R.id.item_3:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_4:
                compileContent();
                return true;
            case R.id.menu_refresh:
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                return true;
            case R.id.item_6:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Deletes everything from the database and refreshes the page
     */
    private void clearDatabase(){
        this.deleteDatabase("UpdatedTalkInfo.db");

        EditText editText = (EditText) findViewById(R.id.notes_edit_text);
        if(editText != null)
        {
            editText.setText("");
        }

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    /**
     * Creates email with all of the data from the notes, scripture list, and quote list
     * @param data
     *          The data from compileContent()
     */
    private void sendEmailWithNotes(String data){
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_SUBJECT, "Notes from Talk Tool");
        i.putExtra(Intent.EXTRA_TEXT   , "**Send email to yourself to access these notes on a computer.**" + System.getProperty("line.separator") + System.getProperty("line.separator") + data);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Compile data from the notes, scripture list, and quote list
     */
    private void compileContent(){
        String the_data = "NOTES:" + System.getProperty("line.separator") + System.getProperty("line.separator");

        db = new DBHandler(this);
        SQLiteDatabase database = db.getReadableDatabase();

        //Get data from notes table
        Cursor c = database.query(NotesTable.NotesEntry.TABLE_NAME,null,null,null,null,null,null);
        if(c.moveToLast()){
            the_data += c.getString(1) + System.getProperty("line.separator") + System.getProperty("line.separator");
        }
        c.close();

        the_data += "SCRIPTURES:" + System.getProperty("line.separator") + System.getProperty("line.separator");

        //Get data from scripture table
        Cursor d = database.query(ScripTable.ScripEntry.TABLE_NAME,null,null,null,null,null,null);
        boolean scripture_empty = true;
        if(d.moveToFirst()){
            scripture_empty = false;
            do {
                the_data += d.getString(1) + System.getProperty("line.separator");
                the_data += d.getString(2) + System.getProperty("line.separator") + System.getProperty("line.separator");
            } while (d.moveToNext());
        }
        d.close();

        if(!scripture_empty)
            the_data += System.getProperty("line.separator");

        the_data += "QUOTES:" + System.getProperty("line.separator") + System.getProperty("line.separator");

        //Get data from quote table
        Cursor e = database.query(QuoteTable.QuoteEntry.TABLE_NAME,null,null,null,null,null,null);
        if(e.moveToFirst()){
            do {
                the_data += e.getString(1) + System.getProperty("line.separator");
                the_data += e.getString(2) + System.getProperty("line.separator") + System.getProperty("line.separator");
            } while (e.moveToNext());
        }
        e.close();

        sendEmailWithNotes(the_data);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NoteFragment(), "Notes");
        adapter.addFragment(new ScriptureFragment(), "Scriptures");
        adapter.addFragment(new QuoteFragment(), "Quotes");
        viewPager.setAdapter(adapter);
    }

    /**
     * Function gets called from DeleteAllDialog
     */
    @Override
    public void onFinishDialog() {
        clearDatabase();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

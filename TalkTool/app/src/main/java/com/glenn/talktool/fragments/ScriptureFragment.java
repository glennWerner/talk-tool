package com.glenn.talktool.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.glenn.talktool.R;
import com.glenn.talktool.activities.AddTutorial;
import com.glenn.talktool.dialogs.ScriptureDialog;
import com.glenn.talktool.handlers.DBHandler;
import com.glenn.talktool.interfaces.LifeCycleInterface;
import com.glenn.talktool.tables.ScripTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that takes care of the scriptures page of the ViewPager
 */
public class ScriptureFragment extends ListFragment implements View.OnClickListener, LifeCycleInterface {

    /**
     * Footer for the list that contains the add button
     */
    View footer;
    /**
     * Add button contained in the footer
     */
    public ImageButton addButton;
    /**
     * Items in the ListView
     */
    private ArrayList<ListViewItem> mItems;
    /**
     * Adapter for the ListView
     */
    ListViewDemoAdapter adapter;
    /**
     * ActionMode for showing the contextual action bar
     */
    public static ActionMode actionMode;
    /**
     * Handler for the database
     */
    private DBHandler db;

    public ScriptureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scripture, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Add footer to end of list
        if(footer != null)
            this.getListView().addFooterView(footer, null, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHandler(getActivity());
        mItems = new ArrayList<ListViewItem>();

        if(savedInstanceState == null)
        {
            // Check database for entries and add to to list
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor c = database.query(ScripTable.ScripEntry.TABLE_NAME,null,null,null,null,null,null);
            if(c.moveToFirst()){
                do {
                    c.getString(1);
                    mItems.add(new ListViewItem(c.getString(1), c.getString(2)));
                } while (c.moveToNext());
            }
            c.close();
        }
        else
        {
            //Restore the saved state
            mItems = savedInstanceState.getParcelableArrayList("key");
        }

        footer = View.inflate(getActivity(), R.layout.footer, null);
        addButton = (ImageButton) footer.findViewById(R.id.scriptureAddButton);
        addButton.setOnClickListener(this);

        // initialize and set the list adapter
        adapter = new ListViewDemoAdapter(getActivity(), mItems);
        setListAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", mItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListView)parent).setItemChecked(position, ((ListView)parent).isItemChecked(position));
                return false;
            }
        });
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int nr = 0;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                } else {
                    nr--;
                }
                if(nr == 1)
                    mode.setTitle(nr + " row selected");
                else
                    mode.setTitle(nr + " rows selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getActivity().getMenuInflater().inflate(R.menu.contextual_menu,
                        menu);
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_delete:
                        SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                        int itemCount = getListView().getCount();


                        for(int i=itemCount-1; i >= 0; i--){
                            if(checkedItemPositions.get(i)){
                                deleteScripture(mItems.get(i).title);
                                adapter.remove(mItems.get(i));
                            }
                        }
                        checkedItemPositions.clear();
                        adapter.notifyDataSetChanged();
                        actionMode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                nr = 0;
                actionMode = null;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);
        Intent intent = new Intent(getActivity(), ScriptureDialog.class);

        intent.putExtra("theTitle", item.title);
        intent.putExtra("theBody", item.description);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scriptureAddButton:
                Intent intent = new Intent(getActivity(), AddTutorial.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onResumeFragment() {
        if(mItems != null)
        {
            if(mItems.size() != 0)
                mItems.clear();

            SQLiteDatabase database = db.getReadableDatabase();
            Cursor c = database.query(ScripTable.ScripEntry.TABLE_NAME,null,null,null,null,null,null);
            if(c.moveToFirst()){
                do {
                    c.getString(1);
                    mItems.add(new ListViewItem(c.getString(1), c.getString(2)));
                } while (c.moveToNext());
            }
            c.close();
            adapter.notifyDataSetChanged();
        }
    }

    private static class ListViewItem implements Parcelable{       // the drawable for the ListView item ImageView
        public String title;        // the text for the ListView item title
        public String description;  // the text for the ListView item description

        public ListViewItem(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public ListViewItem(Parcel in){
            title = in.readString();
            description = in.readString();
        }

        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            // create a bundle for the key value pairs
            Bundle bundle = new Bundle();

            // insert the key value pairs to the bundle
            bundle.putString("Key_name", title);
            bundle.putString("Key_age", description);

            // write the key value pairs to the parcel
            dest.writeBundle(bundle);
        }

        public static final Parcelable.Creator<ListViewItem> CREATOR = new Creator<ListViewItem>() {

            @Override
            public ListViewItem createFromParcel(Parcel source) {
                // read the bundle containing key value pairs from the parcel
                Bundle bundle = source.readBundle();

                // instantiate a person using values from the bundle
                return new ListViewItem(bundle.getString("Key_name"),
                        bundle.getString("Key_age"));
            }

            @Override
            public ListViewItem[] newArray(int size) {
                return new ListViewItem[size];
            }

        };
    }

    public class ListViewDemoAdapter extends ArrayAdapter<ListViewItem> {

        public ListViewDemoAdapter(Context context, List<ListViewItem> items) {
            super(context, android.R.layout.simple_list_item_activated_1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null) {
                // inflate the GridView item layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item, parent, false);

                // initialize the view holder
                viewHolder = new ViewHolder();
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
                convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            ListViewItem item = getItem(position);
            viewHolder.tvTitle.setText(item.title);
            viewHolder.tvDescription.setText(item.description);

            return convertView;
        }

        /**
         * The view holder design pattern prevents using findViewById()
         * repeatedly in the getView() method of the adapter.
         *
         */
        private class ViewHolder {
            TextView tvTitle;
            TextView tvDescription;
        }
    }

    /**
     * Deletes scripture from database based off of title
     * @param title
     *              The title from the selected scripture
     */
    public void deleteScripture(String title){
        SQLiteDatabase database = db.getWritableDatabase();

        try{
            database.delete(ScripTable.ScripEntry.TABLE_NAME, "title = ?", new String[]{title});
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            database.close();
        }
    }

}

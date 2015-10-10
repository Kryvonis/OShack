package com.example.user.oshack;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by dnt on 10/10/15.
 */
public class ListItemsAdapter extends ArrayAdapter<ListItemsAdapter.ListItem> {

    private Context context;

    private ArrayList<String> data = null;

    public ListItemsAdapter(Context context, int resource, ArrayList<String> data) {
        super(context, resource);

        this.data = data;
        this.context = context;
    }

   /* @Override
    public View getView(int position, View ) {

    } */

    public class ListItem {

        public int id;

        public String task;

    }

}

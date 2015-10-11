package com.example.user.oshack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dnt on 10/11/15.
 */
public class TasksAdapter extends ArrayAdapter<String> {

    private Context context;

    private int resource;

    private ArrayList<String> items;

    public TasksAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);

        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {

        View view = convertedView;

        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.task_item, null);
        }

        String item = getItem(position);

        if(item != null) {
            ((TextView) view.findViewById(R.id.task_text)).setText(item);
        }

        return view;

    }

}

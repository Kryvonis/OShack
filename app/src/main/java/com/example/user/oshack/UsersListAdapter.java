package com.example.user.oshack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by dnt on 10/10/15.
 */
public class UsersListAdapter extends ArrayAdapter<> {

    private Context context;

    private int resource;

    private ArrayList<> items;

    public UsersListAdapter(Context context, int resource, ArrayList<> items) {
        super(context, resource, items);

        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {

        View view = convertedView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            layoutInflater.inflate(R.layout.users_list_item, null);
        }

        


    }

}

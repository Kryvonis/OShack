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
public class UsersStatusAdapter extends ArrayAdapter<User> {

    private Context context;

    private int resource;

    private boolean isAllReady = false;

    private ArrayList<User> items;

    public UsersStatusAdapter(Context context, int resource, ArrayList<User> items) {
        super(context, resource, items);

        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    public void setAllReady() {
        isAllReady = true;
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
            view = layoutInflater.inflate(R.layout.user_status_list_item, null);
        }

        User item = getItem(position);

        if(item != null) {
            ((TextView) view.findViewById(R.id.user_name)).setText(item.getName());
            TextView status = (TextView) view.findViewById(R.id.answer_status);

            if (isAllReady) {
                status.setBackgroundResource(android.R.color.transparent);
                status.setText(String.valueOf(item.getTime()));
            } else if (item.isReady()) {
                status.setBackgroundResource(R.drawable.answer_ready);
            } else {
                status.setBackgroundResource(R.drawable.answer_not_ready);
            }

        }

        return view;
    }

}

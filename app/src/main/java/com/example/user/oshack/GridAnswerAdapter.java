package com.example.user.oshack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kryvonis on 10/11/15.
 */

public class GridAnswerAdapter extends ArrayAdapter<String> {

    private Context context;

    private int resource;

    private String[] items;

    public GridAnswerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);

        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    public String getElement(int position) {
        return items[position];
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {

        View view = convertedView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.answer_item, null);
        }

        String item = getItem(position);

        if (item != null) {
            ((TextView) view.findViewById(R.id.answer_text)).setText(item);
        }

        return view;
    }

}

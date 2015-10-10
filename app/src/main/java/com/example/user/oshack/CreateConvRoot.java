package com.example.user.oshack;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 10/10/15.
 */
public class CreateConvRoot extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_conv_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new AddTasksFragment())
                    .commit();
        }
    }

    public static class AddTasksFragment extends Fragment {

        private ListView listView;
        private Button addButton;
        private EditText addTaskEditor;

        private ArrayAdapter<String> adapter;
        private ArrayList<String> tasks = new ArrayList<>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstacneState) {
            return inflater.inflate(R.layout.add_tasks_fragment, container, false);
        }

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            listView = (ListView) view.findViewById(R.id.list_view);
            addButton = (Button) view.findViewById(R.id.add_button);
            addTaskEditor = (EditText) view.findViewById(R.id.new_task);

            adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, tasks);

            listView.setAdapter(adapter);

            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (addTaskEditor.getText().toString().equals("")) {
                        return;
                    }
                    tasks.add(addTaskEditor.getText().toString());
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }
}

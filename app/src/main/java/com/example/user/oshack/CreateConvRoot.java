package com.example.user.oshack;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by user on 10/10/15.
 */
public class CreateConvRoot extends Activity {

    private static ArrayList<String> tasks = new ArrayList<>();

    private static ArrayList<User> users = new ArrayList<>();

    public void startPickAndChekActivity() {
        startActivity(new Intent(this, PickAndCheckAnswers.class));
    }

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
        private Button addButton, nextButton;
        private EditText addTaskEditor;

        private ArrayAdapter<String> adapter;

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
            nextButton = (Button) view.findViewById(R.id.next_button);

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

            nextButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new AddUsersFragment())
                            .commit();
                }
            });
        }

    }

    public static class AddUsersFragment extends Fragment {

        private ListView usersList;

        private Button startButton;

        private UsersListAdapter usersListAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.add_users_fragment, container, false);
        }

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            usersList = (ListView) view.findViewById(R.id.list_user);
            startButton = (Button) view.findViewById(R.id.start_button);


            usersListAdapter = new UsersListAdapter(view.getContext(), R.layout.users_list_item, users);
            usersList.setAdapter(usersListAdapter);

            try {
                users.add(new User("Misha", InetAddress.getByName("127.0.0.1")));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            usersListAdapter.notifyDataSetChanged();

            startButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PickAndCheckAnswers.class));
                }
            });

        }

    }
}

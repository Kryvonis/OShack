package com.example.user.oshack;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/10/15.
 */
public class CreateConvRoot extends Activity {
    static final int PORT = 7777;
    static final String HOSTIP = "192.168.1.1";

    private static ArrayList<String> tasks = new ArrayList<>();

    private static ArrayList<User> users = new ArrayList();

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

        ReceiveUserTask recieverTask;

        ServerSocket server;
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


            recieverTask = new ReceiveUserTask();
            recieverTask.execute();
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        server.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Socket socket = null;
                            ObjectOutputStream oos = null;
                            try {
                                for(User usr:users) {
                                    socket = new Socket(usr.getIp(), PORT);
                                    oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(tasks);
                                    oos.flush();
                                }
                                for(User usr: users){
                                    List<User> tmp = new ArrayList<User>(users);
                                    tmp.remove(usr);
                                    socket = new Socket(usr.getIp(), PORT);
                                    oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(tmp);
                                    oos.flush();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    t.start();
                    Intent intent  = new Intent(getActivity(), PickAndCheckAnswers.class);
                    intent.putExtra("tasks", tasks);//save as Object
                    intent.putExtra("users", users);
                    startActivity(intent);
                }
            });


        }


        class ReceiveUserTask extends AsyncTask<Void, Void, Void> {

            Socket client = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    server = new ServerSocket(PORT);

                    while (true) {
                        Log.d("user", "Start");
                        try {
                            client = server.accept();
                        } catch (IOException e) {
                            break;
                        }
                        ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
                        User user = (User) oin.readObject();

                        users.add(user);
                        publishProgress();
                        Log.d("user", "Added");

                    }


                } catch (ClassNotFoundException e) {
                    Log.d("user", "Error");
                    e.printStackTrace();
                } catch (OptionalDataException e) {
                    e.printStackTrace();
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("user", "OK");
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                usersListAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}

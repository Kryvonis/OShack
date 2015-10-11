package com.example.user.oshack;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by dnt on 10/10/15.
 */
public class PickAndCheckAnswers extends Activity {

    static private ArrayList<User> users = new ArrayList<>();
    static private ArrayList<String> tasks = new ArrayList<>();
    static private User currentUser = null;

    static final int PORT = 7777;
    static final String HOSTIP = "192.168.1.1";

    public static final String IS_ROOT = "is_root";

    private static boolean isRoot;

    public static boolean isAllReady() {
        for (User user : users) {
            if (!user.isReady()) return false;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_and_check_answers_main);

        SharedPreferences sharedPreferences = getSharedPreferences(IS_ROOT, MODE_PRIVATE);
        isRoot = sharedPreferences.getBoolean(IS_ROOT, false);

        if (savedInstanceState == null) {
            if (!isRoot) {
                getFragmentManager().beginTransaction()
                        .add(R.id.pick_and_check_answers_container, new LoadingFragment())
                        .commit();
            } else {
                tasks = getIntent().getStringArrayListExtra("tasks");
                users = (ArrayList<User>)getIntent().getSerializableExtra("users");
                Log.d("user", users.get(0).getName());
                getFragmentManager().beginTransaction()
                        .add(R.id.pick_and_check_answers_container, new CheckAnswersFragment())
                        .commit();
            }
        }
    }

    public static class LoadingFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.spiner_fragment, container, false);
        }

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ((ProgressBar) view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            ((ProgressBar) view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            ReceiverTask task = new ReceiverTask();
            task.execute();
        }
        class ReceiverTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... params) {
                ServerSocket server = null;
                Socket socket = null;
                try {
                    server = new ServerSocket(PORT);
                    socket = server.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    tasks = (ArrayList<String>)ois.readObject();
                    socket = server.accept();
                    ois = new ObjectInputStream(socket.getInputStream());
                    users = (ArrayList<User>)ois.readObject();
                    currentUser = (User)ois.readObject();


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);

                getFragmentManager().beginTransaction()
                        .replace(R.id.pick_and_check_answers_container, new PickAnswerFragment())
                        .commit();
            }
        }

    }

    public static class PickAnswerFragment extends Fragment {

        private TextView questionText;

        private String[] answers = {"0","1", "3","5"};

        private GridAnswerAdapter adapter;

        private GridView answersGrid;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.pick_answer_fragment, container, false);
        }

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            questionText = (TextView) view.findViewById(R.id.question_text);
            questionText.setText(tasks.remove(0));
            answersGrid = (GridView) view.findViewById(R.id.answers_grid);

            adapter = new GridAnswerAdapter(view.getContext(), R.layout.answer_item, answers);
            answersGrid.setAdapter(adapter);
            answersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    /**
                     * Here we have to set answer to user
                     * and set his isReady true
                     */
                    currentUser.setTime(Integer.valueOf(adapter.getElement(position)));
                    currentUser.setReady(true);
                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            ServerSocket server = null;
                            Socket socket = null;
                            ObjectInputStream ois = null;
                            ObjectOutputStream oos = null;
                            try {
                                socket = new Socket(HOSTIP, PORT);
                                oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(currentUser);
                                oos.flush();
                                Log.d("tag", "1");
//                                server = new ServerSocket(PORT);
//                                socket = server.accept();
//                                Log.d("tag", "2");
//                                ois = new ObjectInputStream(socket.getInputStream());
//                                users = (ArrayList<User>) ois.readObject();
                            } catch (IOException e) {
                                e.printStackTrace();

                            } finally {
                                try {
                                    oos.close();
                                    // ois.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    t.start();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.pick_and_check_answers_container, new CheckAnswersFragment())
                            .commit();
                }


            });
        }

    }

    public static class CheckAnswersFragment extends Fragment {

        private ListView usersAndStatusList;

        private Button nextQuestionButton;

        private static UsersStatusAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.check_answers_fragment, container, false);
        }

        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            usersAndStatusList = (ListView) view.findViewById(R.id.users_status_list);
            nextQuestionButton = (Button) view.findViewById(R.id.next_question_button);
            nextQuestionButton.setVisibility(View.INVISIBLE);
            nextQuestionButton.setEnabled(false);

            nextQuestionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tasks.isEmpty()) Toast.makeText(view.getContext(), "There are no tasks", Toast.LENGTH_SHORT).show();
                    else{
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Socket socket = null;
                                OutputStream out= null;
                                String YES = "yes";
                                for(User usr:users){
                                    try {
                                        socket = new Socket(usr.getIp(), PORT);
                                        out.write(YES.getBytes());
                                        out.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }finally {
                                        try {
                                            out.close();
                                            socket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                        });
                        t.start();
                    }
                }
            });

            adapter = new UsersStatusAdapter(view.getContext(), R.layout.user_status_list_item, users);
            usersAndStatusList.setAdapter(adapter);

//            AsyncTask task = null;
//            if(isRoot)task = new HostTask();
//            else task = new UserTask();
//            task.execute();
            Log.d("user", String.valueOf(isRoot));
            if(isRoot){
                HostTask task = new HostTask();
                task.execute();
                Log.d("user", "12");
            }
            else{
                UserTask task = new UserTask();
                task.execute();
            }




        }

        class UserTask extends AsyncTask<Void, Boolean, Void>{

            @Override
            protected Void doInBackground(Void... params) {
                Log.d("user", "2");
                ServerSocket server = null;
                Socket socket = null;
                ObjectInputStream ois = null;


                while(!isAllReady()){
                    try {
                        server = new ServerSocket(PORT);
                        socket = server.accept();
                        Log.d("aaa", "us");
                        ois = new ObjectInputStream(socket.getInputStream());
                        users = (ArrayList<User>)ois.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    publishProgress(false);
                }
                publishProgress(true);
                return null;
            }

            @Override
            protected void onProgressUpdate(Boolean... values) {
                super.onProgressUpdate(values);
                if(values[0]== true){
                    adapter.setAllReady();
                }
                adapter.notifyDataSetChanged();
            }
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Socket socket = null;
//                        ServerSocket server = null;
//                        BufferedReader buf =  null;
//                        String YES = "yes";
//                        try {
//                            server = new ServerSocket(PORT);
//                            socket = server.accept();
//                            buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                            String isContinue = buf.readLine();
//                            if(isContinue.equals(YES)){
//                                for(User user: users)user.setReady(false);
//                                currentUser = null;
//                                getFragmentManager().beginTransaction()
//                                        .replace(R.id.pick_and_check_answers_container, new PickAnswerFragment())
//                                        .commit();
//
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }finally {
//                            try {
//                                if(server != null)server.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                });
//                t.start();
//            }
       }

        class HostTask extends AsyncTask<Void, Boolean, Void>{

            @Override
            protected Void doInBackground(Void... params) {
                Log.d("user", "2");
                ServerSocket server = null;
                Socket socket = null;
                ObjectInputStream ois = null;
                ObjectOutputStream oos = null;
                User user = null;
                while(!isAllReady()){

                    try {
                        Log.d("HOST", "OK");
                        server = new ServerSocket(PORT);

                        socket = server.accept();
                        Log.d("HOST", "AFTER ACCEPT");
                        ois = new ObjectInputStream(socket.getInputStream());
                        user = (User)ois.readObject();
                        for(int i = 0; i<users.size(); i++)
                            if(users.get(i).getId() == user.getId()){
                                users.set(i,user);
                                break;
                            }
                        Log.d("user", "3");
                        publishProgress(false);
                        for(User usr: users){
                            if(usr.isReady()) {
                                socket = new Socket(usr.getIp(), PORT);
                                oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(users);
                                oos.flush();
                                socket.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                publishProgress(true);
                return null;
            }
            @Override
            protected void onProgressUpdate(Boolean... values) {
                super.onProgressUpdate(values);
                if(values[0]){
                    adapter.setAllReady();

                    nextQuestionButton.setVisibility(View.VISIBLE);
                    nextQuestionButton.setEnabled(true);

                }
                adapter.notifyDataSetChanged();

            }
        }

    }

}

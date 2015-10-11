package com.example.user.oshack;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by dnt on 10/10/15.
 */
public class PickAndCheckAnswers extends Activity {

    private static ArrayList<User> users;

    public static final String IS_ROOT = "is_root";

    private boolean isRoot;

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
        }

    }

    public static class PickAnswerFragment extends Fragment {

        private TextView questionText;

        private String[] answers = {"1", "3", "4"};

        private ArrayAdapter<String> adapter;

        private GridView answersGrid;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.pick_answer_fragment, container, false);
        }

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            questionText = (TextView) view.findViewById(R.id.question_text);
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

                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new CheckAnswersFragment())
                            .commit();
                }
            });

            adapter.notifyDataSetChanged();


        }

    }

    public static class CheckAnswersFragment extends Fragment {

        private ListView usersAndStatusList;

        private Button nextQuestionButton;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.check_answers_fragment, container, false);
        }

        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            usersAndStatusList = (ListView) view.findViewById(R.id.users_status_list);
            nextQuestionButton = (Button) view.findViewById(R.id.next_question_button);


        }

    }

}

package com.example.user.oshack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends Activity {

    private EditText nameField;

    private Button createNewConversation, connect;

    static final int PORT = 7777;
    static final String HOSTIP = "192.168.1.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameField = (EditText) findViewById(R.id.name_field);
        createNewConversation = (Button) findViewById(R.id.create_button);
        connect = (Button) findViewById(R.id.connect_button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = nameField.getText().toString();
                if(userName.equals(""))return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(HOSTIP, PORT);
                            User user = new User(userName, socket.getLocalAddress());
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        createNewConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateConvRoot.class));
            }
        });
    }


}

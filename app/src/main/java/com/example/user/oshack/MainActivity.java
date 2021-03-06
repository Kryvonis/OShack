package com.example.user.oshack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    private EditText nameField;

    private Button createNewConversation, connect;

    public static final String IS_ROOT = "is_root";

    private SharedPreferences.Editor ed;

    static final int PORT = 7777;
    static final String HOSTIP = "192.168.1.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameField = (EditText) findViewById(R.id.name_field);
        createNewConversation = (Button) findViewById(R.id.create_button);
        connect = (Button) findViewById(R.id.connect_button);

        SharedPreferences sharedPreferences = getSharedPreferences(IS_ROOT, MODE_PRIVATE);
        ed = sharedPreferences.edit();

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.putBoolean(IS_ROOT, false);
                ed.commit();
                final String userName = nameField.getText().toString();
                if(userName.equals(""))return;
                Thread t =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        ObjectOutputStream oos = null;
                        try {
                            socket = new Socket(HOSTIP, PORT);
                            oos = new ObjectOutputStream(socket.getOutputStream());
                            User user = new User(userName, socket.getLocalAddress());

                            oos.writeObject(user);

                            oos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                oos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
                t.start();
                startActivity(new Intent(MainActivity.this, PickAndCheckAnswers.class));
            }
        });

        createNewConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.putBoolean(IS_ROOT, true);
                ed.commit();
                startActivity(new Intent(MainActivity.this, CreateConvRoot.class));
            }
        });
    }



}

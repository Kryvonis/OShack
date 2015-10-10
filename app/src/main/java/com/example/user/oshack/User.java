package com.example.user.oshack;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by dmytro on 10.10.15.
 */
public class User implements Serializable {
    private static int count = 0;
    private final int id = ++count;

    private String name;
    private InetAddress ip;
    private boolean isReady;
    private int time;

    public User(String name, InetAddress ip){
        this.name = name;
        this.ip = ip;
    }

    public String getName(){return name;}
    public InetAddress getIp(){return ip;}
    public boolean isReady(){return isReady;}
    public int getTime(){return time;}


    public void setReady(boolean ready){isReady = ready;}
    public void setTime(int time){this.time = time;}
}

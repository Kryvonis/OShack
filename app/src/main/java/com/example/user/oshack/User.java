package com.example.user.oshack;

import java.io.Serializable;
import java.net.InetAddress;


public class User implements Serializable {
    private  int id = 0;

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
    public int getId(){return id;}

    public void setReady(boolean ready){isReady = ready;}
    public void setTime(int time){this.time = time;}
    public void setId(int id){this.id = id;}
}

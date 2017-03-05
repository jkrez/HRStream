package com.streamteam.pushpoint;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by john on 6/9/16.
 */
public class ClassModel {
    public String ClassId; // aka SessionId
    public int Port;
    public InetAddress IpAddress;
    public String SessionName;
    public boolean Active;

    public ClassModel() {
        this.Active = false;
    }

    public ClassModel(String classId, String ipAddress, String sessionName, String port)
    {
        this.ClassId = classId;

        try {
            this.IpAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.IpAddress = null;
        }

        this.SessionName = sessionName;
        this.Port = Integer.parseInt(port);
    }
}

package com.streamteam.pushpoint;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by john on 6/9/16.
 */

public class UdpMessageListener extends AsyncTask<Void, Void, Void> {

    int port;
    byte[] receiveData;
    DatagramSocket udpListeningSocket;
    private final UdpPacketProcessor udpPacketProcessor;
    public UdpMessageListener(UdpPacketProcessor processor, int localPort){
        port = localPort;
        this.udpPacketProcessor = processor;
        receiveData = new byte[Constants.MaxPacketSize];
        try {
            udpListeningSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Socket bind error in port: " + port);
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute()
    {
       super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... params) {
        Log.ConsoleLog("Entering background task for UdpMessageListener");
        while(true){
            if (isCancelled()) {
                Log.ConsoleLog("UdpMessageListener task was cancelled ");
                break;
            }

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                Log.ConsoleLog("UdpMessageListener waiting for packet... ");
                udpListeningSocket.receive(receivePacket);
                System.out.println("Received UDP Packet from Port:" + port);
                udpPacketProcessor.onPacketReceived(receivePacket, port);

            } catch (IOException e) {
                System.out.println("UDP Listener end up with an exception:");
                e.printStackTrace();
            }
        }

        return null;
    }

    public interface UdpPacketProcessor {
        void onPacketReceived(DatagramPacket receivedPacket, int localPort);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
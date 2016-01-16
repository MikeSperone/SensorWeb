package com.mikesperone.sensorweb;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable {
    public static final String SERVERIP = "68.198.36.58";
    public static final int SERVERPORT = 55056;
    public String message = "";

    public void setDataToSend(String dataToSend) {
        message = dataToSend;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVERIP);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = new byte[1024];
            String sentence = message;
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVERPORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
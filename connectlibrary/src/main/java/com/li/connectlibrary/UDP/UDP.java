package com.li.connectlibrary.UDP;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDP implements Runnable {
    public static final String TAG = "Connect.UDP";
    public static final String RECEIVE_ACTION = "GetUDPReceive";
    public static final String RECEIVE_STRING = "ReceiveString";
    public static final String RECEIVE_BYTES = "ReceiveBytes";

    private int port;
    private String ServerIp;
    private boolean isOpen;
    private static DatagramSocket ds = null;
    private Context context;
    private UDP_CallBack udp_callBack;
    public static int dataSize = 1024 * 4;

    /**切換伺服器監聽狀態*/
    public void changeServerStatus(boolean isOpen) {
        this.isOpen = isOpen;
        if (!isOpen) {
            ds.close();
            Log.e(TAG, "UDP-Server已關閉");
        }
    }

    //切換Port
    public void setPort(int port){
        this.port = port;
    }

    /**初始化建構子*/
    public UDP(String ServerIp,Context context) {
        this.context = context;
        this.ServerIp = ServerIp;
        this.isOpen = true;
    }

    public void SetCallBack(UDP_CallBack callback)
    {
        udp_callBack = callback;
    }

    /**發送訊息*/
    public void send(String string, String remoteIp, int remotePort) throws IOException {
        send(string.getBytes(),remoteIp,remotePort);
    }

    public void send(byte[] data, String remoteIp, int remotePort) throws IOException {
        Log.d(TAG, "客户端IP：" + remoteIp + ":" + remotePort);
        InetAddress inetAddress = InetAddress.getByName(remoteIp);
        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket dpSend = new DatagramPacket(data, data.length, inetAddress, remotePort);
        datagramSocket.send(dpSend);
    }

    @Override
    public void run() {
        //在本機上開啟Server監聽
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerIp, port);
        try {
            ds = new DatagramSocket(inetSocketAddress);
            Log.d(TAG, "UDP-Server已啟動");
        } catch (SocketException e) {
            Log.e(TAG, "啟動失敗，原因: " + e.getMessage());
            e.printStackTrace();
        }
        //預備一組byteArray來放入回傳得到的值(PS.回傳為格式為byte[]，本範例將值轉為字串了)
        byte[] msgRcv = new byte[dataSize];
        DatagramPacket dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
        //建立while迴圈持續監聽來訪的數值
        while (isOpen) {
            Log.e(TAG, "UDP-Server監聽資訊中..");
            try {
                //執行緒將會在此打住等待有值出現
                ds.receive(dpRcv);
                if(udp_callBack ==null)
                    Log.e(TAG, "udp_callBack is null");
                else {
                    if (dpRcv.getData() == null)
                        Log.e(TAG, "dpRcv.getData() is null");
                    else
                        Log.d(TAG, "UDP-Server收到資料, size:" + dpRcv.getData().length);
                    udp_callBack.OnGetDatas(dpRcv.getData());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
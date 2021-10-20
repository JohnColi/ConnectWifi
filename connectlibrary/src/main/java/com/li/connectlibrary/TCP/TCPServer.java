package com.li.connectlibrary.TCP;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class TCPServer implements Runnable {
    public static final String TAG = "Connect.TCP";
    public static final String RECEIVE_ACTION = "GetTCPReceive";
    public static final String RECEIVE_STRING = "ReceiveString";
    public static final String RECEIVE_BYTES = "ReceiveBytes";
    private int port;
    private boolean isOpen;
    private Context context;
    private TCP_CallBack tcp_callBack;

    public ArrayList<ServerSocketThread> SST = new ArrayList<>();
    /**建立建構子*/
    public TCPServer(int port,Context context){
        this.port = port;
        isOpen = true;
        this.context = context;
    }

    public void SetCallBack(TCP_CallBack callBack){
        tcp_callBack = callBack;
    }

    //取得開啟狀態
    public boolean getStatus(){
        return isOpen;
    }
    //關閉伺服器
    public void closeServer(){
        isOpen = false;
        //找出所有正在連線的裝置執行緒，並一一清除、斷線
        for (ServerSocketThread s : SST){
            s.isRun = false;
        }
        SST.clear();
    }
    /**取得Socket許可(握手)*/
    private Socket getSocket(ServerSocket serverSocket){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "更新Server狀態");
            return null;
        }
    }

    @Override
    public void run() {
        try {
            /**在本機的Port上開啟伺服器*/
            ServerSocket serverSocket = new ServerSocket(port);
            //設置Timeout，以便更新裝置連進來的狀況
            serverSocket.setSoTimeout(2000);
            while (isOpen){
                Log.e(TAG, "監測裝置輸入...");
                if (!isOpen) break;
                Socket socket = getSocket(serverSocket);
                if (socket != null){
                    //如果Socket不為null，表示有裝置連入了
                    new ServerSocketThread(socket,context);
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**監聽裝置連入與收發狀態之執行緒*/
    public class ServerSocketThread extends Thread{
        private Socket socket;
        private PrintWriter pw;
        private InputStream is;
        private boolean isRun = true;
        private Context context;

        ServerSocketThread(Socket socket, Context context){
            this.socket = socket;
            this.context = context;
            String ip = socket.getInetAddress().toString();
            Log.d(TAG, "檢測到新的裝置,Ip: " + ip);

            try {
                socket.setSoTimeout(2000);
                OutputStream os = socket.getOutputStream();
                is = socket.getInputStream();
                pw = new PrintWriter(os,true);
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendData(String msg){
            pw.print(msg);
            pw.flush();
        }

        @Override
        public void run() {
            byte[] buff = new byte[100];
            SST.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()){
                try {
                    //監聽訊息是否有送過來
                    int rcvLen;
                    if ((rcvLen = is.read(buff)) != -1 ){
                        String string = new String(buff, 0, rcvLen);
                        Log.d(TAG, "收到訊息: " + string);

                        if(!tcp_callBack.equals(null))
                        {
                            Object o = string;
                            tcp_callBack.OnGetData(o);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //跳出While迴圈即為斷開連線
            try {
                socket.close();
                SST.clear();
                Log.e(TAG, "關閉Server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
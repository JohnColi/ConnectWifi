package com.li.connectlibrary.UDP;

import android.content.Context;
import android.util.Log;

import com.li.connectlibrary.CommendFun;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.li.connectlibrary.CommendFun.getLocalIP;

public class UDPActivity {
    public static final String TAG = "Connect.UDPActivity";

    static ExecutorService exec = Executors.newCachedThreadPool();
    static UDP udpServer;

    private static String mRemoteIP;
    private static int mPort;

    public static void Init()
    {
        Log.d(TAG, "Init");
        SetReceiveSwitch();
    }

    public static String GetLocal_IP() {
        String localIP = CommendFun.getLocalIP(UnityPlayer.currentActivity.getApplicationContext());
        Log.d(TAG, "localIP : " + localIP);
        return localIP;
    }

    public static  void SetIP(String ip)
    {
        Log.d(TAG, "SetIP : " + ip);
        mRemoteIP = ip;
    }

    public static void SetPort(int port)
    {
        Log.d(TAG, "SetPort : " + port);
        if(!"0".equals(port))
            mPort = port;
    }

    //設置監UDP功能
    private static void SetReceiveSwitch() {
        Log.d(TAG, "SetReceiveSwitch");
        //初始化UDP伺服器
        //注意：此處有調用CommendFun.java的內容以取得本機IP
        Context curContext = UnityPlayer.currentActivity.getApplicationContext();
        udpServer = new UDP(getLocalIP(curContext),curContext);
    }

    //開關 UDP
    public static void SwitchUDPState(boolean isOpen)
    {
        Log.d(TAG, "SwitchUDPState, isOpen : " + isOpen);

        if (isOpen) {
            /**開啟UDP伺服器監聽*/
            udpServer.setPort(mPort);
            udpServer.changeServerStatus(isOpen);
            exec.execute(udpServer);
        } else {
            /**關閉UDP伺服器監聽*/
            udpServer.changeServerStatus(isOpen);
        }
    }

    //設置發送資料功能
    public static void SetSendFunction(final String msg) {
        Log.d(TAG, "SetSendFunction, msg : " + msg);
        /**發送UDP訊息至指定的IP*/
        if (msg.length() == 0) return;

        //調用UDP.java中的方法，送出資料
        //註解的為lambda表達式，原貌在下面
        /*
            exec.execute(()->{
                try {
                    udpServer.send(msg, remoteIp, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            */

        exec.execute(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    udpServer.send(msg, mRemoteIP, mPort);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
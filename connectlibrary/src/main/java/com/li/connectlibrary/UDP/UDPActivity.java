package com.li.connectlibrary.UDP;

import android.content.Context;
import android.util.Log;

import com.li.connectlibrary.CommendFun;
import com.li.connectlibrary.UDP_CallBack;
import com.li.connectlibrary.UnityConnectCallBack;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.li.connectlibrary.CommendFun.getLocalIP;

public class UDPActivity {
    public static final String TAG = UDP.TAG;
    static ExecutorService exec = Executors.newCachedThreadPool();
    static UDP udpServer;

    private static String mRemoteIP;
    private static int mPort;
    private static UDP_CallBack udp_callBack;
    private static UnityConnectCallBack unityConnectCallBack;

    public static void Init()
    {
        Log.d(TAG, "Init");
        SetReceiveSwitch();

        udp_callBack = new UDP_CallBack() {
            @Override
            public void OnGetMsg(String msg) {
                Object o = msg;
                unityConnectCallBack.OnGetData(o);
            }
        };

        udpServer.SetCallBack(udp_callBack);
    }

    //region commend
    public static String GetLocal_IP() {
        String localIP = CommendFun.getLocalIP(UnityPlayer.currentActivity.getApplicationContext());
        Log.d(TAG, "localIP : " + localIP);
        return localIP;
    }

    public static  void SetRemoteIP(String ip)
    {
        Log.d(TAG, "SetRemoteIP : " + ip);
        mRemoteIP = ip;
    }

    public static void SetPort(int port)
    {
        Log.d(TAG, "SetPort : " + port);
        if(!"0".equals(port))
            mPort = port;
    }

    public static void SetUnityConnectCallBack(UnityConnectCallBack callBack)
    {
        Log.d(TAG, "SetUnityConnectCallBack");
        unityConnectCallBack = callBack;
    }
    //endregion

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
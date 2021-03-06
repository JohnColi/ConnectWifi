package com.li.connectlibrary.UDP;

import android.content.Context;
import android.util.Log;

import com.li.connectlibrary.CommendFun;
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
                Log.d(TAG,"OnGetMsg: "+ msg);
                unityConnectCallBack.OnGetMsg(msg);
            }

            @Override
            public void OnGetDatas(byte[] datas) {
                Log.d(TAG,"OnGetDatas, data length: "+ datas.length);
                Object o = datas;
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

    public static void SetGetDataSize(int size)
    {
        Log.d(TAG, "SetGetDataSize = " + size);
        UDP.dataSize = size;
    }
    //endregion

    //?????????UDP??????
    private static void SetReceiveSwitch() {
        Log.d(TAG, "SetReceiveSwitch");
        //?????????UDP?????????
        //????????????????????????CommendFun.java????????????????????????IP
        Context curContext = UnityPlayer.currentActivity.getApplicationContext();
        udpServer = new UDP(getLocalIP(curContext),curContext);
    }

    //?????? UDP
    public static void SwitchUDPState(boolean isOpen)
    {
        Log.d(TAG, "SwitchUDPState, isOpen : " + isOpen);

        if (isOpen) {
            /**??????UDP???????????????*/
            udpServer.setPort(mPort);
            udpServer.changeServerStatus(isOpen);
            exec.execute(udpServer);
        } else {
            /**??????UDP???????????????*/
            udpServer.changeServerStatus(isOpen);
        }
    }

    //????????????????????????
    public static void SetSendFunction(final String msg) {
        Log.d(TAG, "SetSendFunction, msg : " + msg);
        /**??????UDP??????????????????IP*/
        if (msg.length() == 0) return;

        /**
        // ??????UDP.java???????????????????????????
        //????????????lambda???????????????????????????
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

    public static void SetSendFunction(final byte[] datas)
    {
        Log.d(TAG, "SetSendFunction by byte[]");
        if(datas == null)
        {
            Log.e(TAG,"Sending data is null");
            return;
        }

        exec.execute(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    udpServer.send(datas, mRemoteIP, mPort);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
package com.li.connectlibrary.TCP;

import android.util.Log;

import com.li.connectlibrary.CommendFun;
import com.li.connectlibrary.UnityConnectCallBack;
import com.unity3d.player.UnityPlayer;

public class TCPActivity {
    public static final String TAG = TCPServer.TAG;
    private static String mRemoteIP;
    private static int mPort;
    private static UnityConnectCallBack unityConnectCallBack;

    public static void Init()
    {
        Log.d(TAG, "Init");
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

}

package com.li.connectlibrary.TCP;

import android.util.Log;

import com.li.connectlibrary.CommendFun;
import com.li.connectlibrary.UnityConnectCallBack;
import com.unity3d.player.UnityPlayer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPActivity {
    public static final String TAG = TCPServer.TAG;
    private static boolean isClientMode;
    private static String mRemoteIP;
    private static int mPort;
    private static UnityConnectCallBack unityConnectCallBack;

    private static ExecutorService server_exec;
    private static ExecutorService client_exec;
    private static TCPServer tcpServer;
    private static TCPClient tcpClient;
    private static TCP_CallBack tcp_callBack;

    public static void Init() {
        Log.d(TAG, "Init");

        tcp_callBack = new TCP_CallBack() {
            @Override
            public void OnGetMsg(String msg) {
                Log.d(TAG, "OnGetMsg : " + msg);
            }

            @Override
            public void OnGetData(Object obj) {
                Log.d(TAG, "OnGetData : " + obj);
            }
        };
    }

    //region commend
    public static String GetLocal_IP() {
        String localIP = CommendFun.getLocalIP(UnityPlayer.currentActivity.getApplicationContext());
        Log.d(TAG, "localIP : " + localIP);
        return localIP;
    }

    public static void SetRemoteIP(String ip) {
        Log.d(TAG, "SetRemoteIP : " + ip);
        mRemoteIP = ip;
    }

    public static void SetPort(int port) {
        Log.d(TAG, "SetPort : " + port);
        if (!"0".equals(port))
            mPort = port;
    }

    public static void SetUnityConnectCallBack(UnityConnectCallBack callBack) {
        Log.d(TAG, "SetUnityConnectCallBack");
        unityConnectCallBack = callBack;
    }
    //endregion

    public static void SetClientMode(boolean isClientMode) {
        Log.d(TAG, "SetClientMode, isClientMode : " + isClientMode);

        SetServerSwitch(!isClientMode);
        SetClientSwitch(isClientMode);

        if (tcp_callBack == null)
            Log.e(TAG, "tcp_callBack is null");
        else {
            if (isClientMode)
                tcpClient.SetCallBack(tcp_callBack);
            else
                tcpServer.SetCallBack(tcp_callBack);
        }
    }

    //設置TCP伺服器功能
    private static void SetServerSwitch(boolean isOpen) {
        Log.d(TAG, "SetServerSwitch, isOpen : " + isOpen);

        if (isOpen) {
            server_exec = Executors.newCachedThreadPool();
            tcpServer = new TCPServer(mPort, UnityPlayer.currentActivity.getApplicationContext());
            server_exec.execute(tcpServer);
        } else {
            if (null != tcpServer)
                tcpServer.closeServer();
        }
    }

    // 設置TCP客戶端功能
    private static void SetClientSwitch(boolean isOpen) {
        Log.d(TAG, "SetClientSwitch, isOpen : " + isOpen);

        if (isOpen) {
            client_exec = Executors.newCachedThreadPool();
            tcpClient = new TCPClient(mRemoteIP, mPort, UnityPlayer.currentActivity.getApplicationContext());
            client_exec.execute(tcpClient);
        } else {
            if (null != tcpClient)
                tcpClient.closeClient();
        }
    }

    //設置發送資料功能(含伺服器/客戶端)
    public static void SetSendFunction(final String msg) {
        Log.d(TAG, "SetSendFunction, msg : " + msg);

        if (isClientMode) {
            //切換開關在Client模式時
            if (tcpClient == null) return;
            Log.d(TAG, "555555");

            if (msg.length() == 0 || !tcpClient.getStatus()) return;
            client_exec.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "666666");
                    tcpClient.send(msg);
                }
            });
        } else {
            //切換開關在Server模式時
            if (tcpServer == null) return;
            Log.d(TAG, "111111");

            if (msg.length() == 0 || !tcpServer.getStatus()) return;
            Log.d(TAG, "222222");

            if (tcpServer.SST.size() == 0) return;
            Log.d(TAG, "333333");

            server_exec.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "444444");
                    tcpServer.SST.get(0).sendData(msg);
                }
            });
        }
    }
}

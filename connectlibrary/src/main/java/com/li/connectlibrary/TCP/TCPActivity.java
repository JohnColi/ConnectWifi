package com.li.connectlibrary.TCP;

import android.util.Log;

import com.li.connectlibrary.CommendFun;
import com.li.connectlibrary.UnityConnectCallBack;
import com.unity3d.player.UnityPlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPActivity {
    public static final String TAG = TCPServer.TAG;
    private static boolean isClientMode;
    private static String mRemoteIP;
    private static int mPort;
    private static UnityConnectCallBack unityConnectCallBack;

    private static ExecutorService exec;
    private static TCPServer tcpServer;
    private static TCPClient tcpClient;

    public static void Init() {
        Log.d(TAG, "Init");
        ExecutorService exec = Executors.newCachedThreadPool();
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
        SetServerSwitch(!isClientMode);
        SetClientSwitch(isClientMode);
    }

    //設置TCP伺服器功能
    private static void SetServerSwitch(boolean isOpen) {

        if (isOpen) {
            tcpServer = new TCPServer(mPort, UnityPlayer.currentActivity.getApplicationContext());
            exec.execute(tcpServer);
        } else {
            tcpServer.closeServer();
        }
    }

    // 設置TCP客戶端功能
    private static void SetClientSwitch(boolean isOpen) {
        if (isOpen) {
            tcpClient = new TCPClient(mRemoteIP, mPort, UnityPlayer.currentActivity.getApplicationContext());
            exec.execute(tcpClient);
        } else {
            tcpClient.closeClient();
        }
    }

    /**
     * 設置發送資料功能(含伺服器/客戶端)
     */
    private void SetSendFunction(final String msg) {
        if (isClientMode) {
            //切換開關在Client模式時
            if (tcpClient == null) return;
            if (msg.length() == 0 || !tcpClient.getStatus()) return;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    tcpClient.send(msg);
                }
            });
        } else {
            //切換開關在Server模式時
            if (tcpServer == null) return;
            if (msg.length() == 0 || !tcpServer.getStatus()) return;
            //此處Lambda表達式相等於下方註解部分
            if (tcpServer.SST.size() == 0) return;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    tcpServer.SST.get(0).sendData(msg);
                }
            });
        }
    }
}

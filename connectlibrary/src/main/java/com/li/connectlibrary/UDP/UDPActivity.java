package com.li.connectlibrary.UDP;

import android.util.Log;

public class UDPActivity {
    public static final String TAG = "Connect.UDPActivity";
    String localIP;
    public static void Init()
    {


    }
    private void setBaseUI() {
        Log.d(TAG, "setBaseUI");
        //注意：此處有調用CommendFun.java的內容以取得本機IP
        //localIP = getLocalIP(this);
        Log.d(TAG, "localIP : " + localIP);

    }
}

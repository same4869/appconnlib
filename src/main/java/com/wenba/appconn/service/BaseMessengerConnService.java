package com.wenba.appconn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wenba.appconn.config.MessengerConfigParm;
import com.wenba.appconn.event.MsgBean;
import com.wenba.appconn.manager.MessengerConnManager;

import de.greenrobot.event.EventBus;

import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_FLOATWIDGET;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_HAVOCINHEAVEN;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_INCLASSTEACHER;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_LITTLEMONKEY;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_MONKEYKING;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_WUKONG;


/**
 * 用于应用间消息接收
 * Created by xunwang on 16/12/21.
 */

public class BaseMessengerConnService extends Service {
    private Messenger mMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        mMessenger = new Messenger(new MessengerHandler());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void sendToManager(String receiveMsg, MessengerConnManager.MsgType type) {
        MsgBean msgBean = new MsgBean();
        msgBean.setMsg(receiveMsg);
        msgBean.setType(type);
        EventBus.getDefault().post(msgBean);
    }

    protected void messageHandle(String msg, int type) {

    }

    /**
     * 信使的持有, 处理返回信息
     */
    private class MessengerHandler extends Handler {

        public MessengerHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.getData() == null) {
                return;
            }
            String receiveMsg = msg.getData().getString(MessengerConfigParm.MSG_ARG);
            switch (msg.what) {
                case MSG_FROM_HAVOCINHEAVEN:
                    Log.d("kkkkkkkk", "消息来自HAVOCINHEAVEN msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.HAVOCINHEAVEN);
                    break;
                case MSG_FROM_LITTLEMONKEY:
                    Log.d("kkkkkkkk", "消息来自LITTLEMONKEY msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.LITTLEMONKEY);
                    break;
                case MSG_FROM_MONKEYKING:
                    Log.d("kkkkkkkk", "消息来自MONKEYKING msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.MOKEYKING);
                    break;
                case MSG_FROM_WUKONG:
                    Log.d("kkkkkkkk", "消息来自WUKONG msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.WUKONG);
                    break;
                case MSG_FROM_INCLASSTEACHER:
                    Log.d("kkkkkkkk", "消息来自INCLASSTEACHER msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.INCLASSTEACHER);
                    break;
                case MSG_FROM_FLOATWIDGET:
                    Log.d("kkkkkkkk", "消息来自FLOATWIDGET msg.arg1 --> " + receiveMsg);
                    sendToManager(receiveMsg, MessengerConnManager.MsgType.FLOATWIDGET);
                    break;
                default:
                    super.handleMessage(msg);
            }
            messageHandle(receiveMsg, msg.what);
        }
    }
}

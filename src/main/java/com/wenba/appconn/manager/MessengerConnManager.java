package com.wenba.appconn.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.wenba.appconn.event.MsgBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static com.wenba.appconn.config.MessengerConfigParm.FLOATWIDGET_SERVICE_ACTION;
import static com.wenba.appconn.config.MessengerConfigParm.HAVOCINHEAVEN_SERVICE_ACTION;
import static com.wenba.appconn.config.MessengerConfigParm.INCLASSTEACHER_SERVICE_ACTION;
import static com.wenba.appconn.config.MessengerConfigParm.LITTLEMONKEY_SERVICE_ACTION;
import static com.wenba.appconn.config.MessengerConfigParm.MONKEYKING_SERVICE_ACTION;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_ARG;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_FLOATWIDGET;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_HAVOCINHEAVEN;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_INCLASSTEACHER;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_LITTLEMONKEY;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_MONKEYKING;
import static com.wenba.appconn.config.MessengerConfigParm.MSG_FROM_WUKONG;
import static com.wenba.appconn.config.MessengerConfigParm.WUKONG_SERVICE_ACTION;


/**
 * .::::.
 * .::::::::.
 * :::::::::::
 * ..:::::::::::'
 * '::::::::::::'
 * .::::::::::
 * '::::::::::::::..
 * ..::::::::::::.
 * ``::::::::::::::::
 * ::::``:::::::::'        .:::.
 * ::::'   ':::::'       .::::::::.
 * .::::'      ::::     .:::::::'::::.
 * .:::'       :::::  .:::::::::' ':::::.
 * .::'        :::::.:::::::::'      ':::::.
 * .::'         ::::::::::::::'         ``::::.
 * ...:::           ::::::::::::'              ``::.
 * ```` ':.          ':::::::::'                  ::::..
 * '.:::::'                    ':'````..               ':'````..
 * <p>
 * Messenger信息管理类
 * Created by xunwang on 16/12/21.
 */

public class MessengerConnManager {

    public static final String TAG = MessengerConnManager.class.getSimpleName();
    private static MessengerConnManager instance;
    private MessengerConnListener messengerConnListener;
    private Messenger mService;
    private boolean isConn;
    //    private WeakReference<Activity> weakReference;
    //更改APP对象发送消息时,先把要发送的东西存在map里,等连接成功后从这个map里面取消息再发送
    private List<SendMsgBean> changeMsgList = new ArrayList<>();
    private MsgType currentTargetMsgType;
    private Activity cactivity;

    private MessengerConnManager() {
        EventBus.getDefault().register(this);
    }

    public static MessengerConnManager getInstance() {
        if (instance == null) {
            synchronized (MessengerConnManager.class) {
                if (instance == null) {
                    instance = new MessengerConnManager();
                }
            }
        }
        Log.e(TAG, "cjjjj getInstance():" + instance.toString());
        return instance;
    }

    public enum MsgType {
        HAVOCINHEAVEN, LITTLEMONKEY, MOKEYKING, WUKONG, INCLASSTEACHER, FLOATWIDGET
    }

    public boolean isConn() {
        return isConn;
    }

    /**
     * @param activity 当前activity
     * @param type     想要发送给哪个APP就填哪个type
     */
    public void init(Activity activity, MsgType type) {
        currentTargetMsgType = type;
//        weakReference = new WeakReference<>(activity);
        Intent intent = new Intent();
        switch (type) {
            case HAVOCINHEAVEN:
                intent.setAction(HAVOCINHEAVEN_SERVICE_ACTION);
                break;
            case LITTLEMONKEY:
                intent.setAction(LITTLEMONKEY_SERVICE_ACTION);
                break;
            case MOKEYKING:
                intent.setAction(MONKEYKING_SERVICE_ACTION);
                break;
            case WUKONG:
                intent.setAction(WUKONG_SERVICE_ACTION);
                break;
            case INCLASSTEACHER:
                intent.setAction(INCLASSTEACHER_SERVICE_ACTION);
                break;
            case FLOATWIDGET:
                intent.setAction(FLOATWIDGET_SERVICE_ACTION);
                break;
            default:
                break;
        }
//        Activity cactivity = weakReference.get();
        this.cactivity = activity;

        if (cactivity != null) {
            Intent cintent = createExplicitFromImplicitIntent(cactivity, intent);
            if (cintent != null) {
                final Intent eintent = new Intent(cintent);
                cactivity.bindService(eintent, mConn, Context.BIND_AUTO_CREATE);
                Log.e(TAG, "cjjjj init activity:" + cactivity.getClass().getSimpleName() + " " + cactivity.toString()
                        + " bindService() mConn:" + mConn.toString());
            }
        }
    }

    /**
     * 如果发送消息的目标APP不是init的那一个,则用这个方法发送
     *
     * @param activity
     * @param targetType
     * @param message
     * @param type
     * @param listener
     */
    public void changeTargetAndSendMessage(Activity activity, MsgType targetType, String message, MsgType type,
                                           SendMsgListener listener) {
        if (isConn) {
            //如果目标不是之前注册的目标,则与之前的解绑,和新的连接
            if (targetType != currentTargetMsgType) {
                currentTargetMsgType = targetType;
//                weakReference = new WeakReference<>(activity);
//                Activity cactivity = weakReference.get();
                cactivity = activity;
                if (cactivity != null) {
                    cactivity.unbindService(mConn);
                    isConn = false;
                    Log.e(TAG, "cjjjj changeTargetAndSendMessage isConn=" + isConn +" cactivity != null unbindService");
                }
                SendMsgBean sendMsgBean = new SendMsgBean();
                sendMsgBean.setMessage(message);
                sendMsgBean.setType(type);
                sendMsgBean.setListener(listener);
                changeMsgList.add(sendMsgBean);
                init(activity, targetType);
            } else {
                sendMsgToApp(message, type, listener);
                Log.e(TAG, "cjjjj changeTargetAndSendMessage isConn=" + isConn + "sendMsgBean:" + message);
            }
        } else {
            SendMsgBean sendMsgBean = new SendMsgBean();
            sendMsgBean.setMessage(message);
            sendMsgBean.setType(type);
            sendMsgBean.setListener(listener);
            changeMsgList.add(sendMsgBean);
            init(activity, targetType);
            Log.e(TAG, "cjjjj changeTargetAndSendMessage isConn=false sendMsgBean:" + sendMsgBean.getMessage());
        }
    }

    public void destroy(Activity activity) {
//        weakReference = new WeakReference<>(activity);
//        Activity cactivity = weakReference.get();
        cactivity = activity;
        if (cactivity != null && isConn) {
            try {
                cactivity.unbindService(mConn);
                Log.e(TAG, "cjjjj destroy activity:" + cactivity.getClass().getSimpleName() + " " + cactivity
                        .toString() + " unbindService() mConn:" + mConn.toString());
                isConn = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().unregister(this);
        messengerConnListener = null;
        instance = null;
        cactivity = null;
    }

    @Subscribe
    public void onEventMainThread(MsgBean event) {
        if (event != null && messengerConnListener != null) {
            messengerConnListener.onMsgRec(event.getMsg(), event.getType());
        }
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Log.e(TAG, "cjjjj mConn onServiceConnected mConn:" + mConn.toString() + " mService:" + mService.toString());
            isConn = true;
            if (messengerConnListener != null) {
                messengerConnListener.onConnected();
            }
            sendToMsgWithHashMap(changeMsgList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "cjjjj mConn onServiceDisconnected mConn:" + mConn.toString());
            mService = null;
            isConn = false;
            if (messengerConnListener != null) {
                messengerConnListener.onDisConnected();
            }
        }
    };

    //连接成功后,如果map里面有数据,则先自动发送这些
    private void sendToMsgWithHashMap(List<SendMsgBean> sendList) {
        Iterator iter = sendList.iterator();
        while (iter.hasNext()) {
            SendMsgBean msgBean = (SendMsgBean) iter.next();
            sendMsgToApp(msgBean.getMessage(), msgBean.getType(), msgBean.getListener());
            iter.remove();
        }
    }

    /**
     * `
     *
     * @param message  要发送的内容
     * @param type     自己是哪个APP就填哪个Type
     * @param listener 发送成功或失败回调
     */
    public void sendMsgToApp(String message, MsgType type, SendMsgListener listener) {
        Message msg;
        switch (type) {
            case HAVOCINHEAVEN:
                msg = Message.obtain(null, MSG_FROM_HAVOCINHEAVEN);
                break;
            case LITTLEMONKEY:
                msg = Message.obtain(null, MSG_FROM_LITTLEMONKEY);
                break;
            case MOKEYKING:
                msg = Message.obtain(null, MSG_FROM_MONKEYKING);
                break;
            case WUKONG:
                msg = Message.obtain(null, MSG_FROM_WUKONG);
                break;
            case INCLASSTEACHER:
                msg = Message.obtain(null, MSG_FROM_INCLASSTEACHER);
                break;
            case FLOATWIDGET:
                msg = Message.obtain(null, MSG_FROM_FLOATWIDGET);
                break;
            default:
                msg = Message.obtain(null, MSG_FROM_HAVOCINHEAVEN);
                break;
        }
        Bundle data = new Bundle();
        data.putString(MSG_ARG, message);
        msg.setData(data);

        if (isConn) {
            try {
                mService.send(msg);
//                Log.e(TAG, "cjjjj sendMsgToApp  mService.send(msg) mService:" + mService.toString());
                if (listener != null) {
                    listener.OnSendMsgSuccess();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.OnSendMsgFailed(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.OnSendMsgFailed(e.getMessage());
                }
            }
        } else {
            if (cactivity != null) {
                init(cactivity, currentTargetMsgType);
                SendMsgBean sendMsgBean = new SendMsgBean();
                sendMsgBean.setMessage(message);
                sendMsgBean.setType(type);
                sendMsgBean.setListener(listener);
                changeMsgList.add(sendMsgBean);
            }
        }
    }

    public void setMessengerConnListener(MessengerConnListener messengerConnListener) {
        this.messengerConnListener = messengerConnListener;
    }

    public interface MessengerConnListener {
        void onConnected();

        void onDisConnected();

        void onMsgRec(String msg, MsgType type);
    }

    public interface SendMsgListener {
        void OnSendMsgSuccess();

        void OnSendMsgFailed(String msg);
    }

    public class SendMsgBean {
        private String message;
        private MsgType type;
        private SendMsgListener listener;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public MsgType getType() {
            return type;
        }

        public void setType(MsgType type) {
            this.type = type;
        }

        public SendMsgListener getListener() {
            return listener;
        }

        public void setListener(SendMsgListener listener) {
            this.listener = listener;
        }
    }

    //5.0后安卓默认不允许隐式启动service,可通过如下处理
    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}

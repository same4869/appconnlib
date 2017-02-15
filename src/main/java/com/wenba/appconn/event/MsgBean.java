package com.wenba.appconn.event;


import com.wenba.appconn.manager.MessengerConnManager;

/**
 * Created by xunwang on 16/12/21.
 */

public class MsgBean {
    private String msg;
    private MessengerConnManager.MsgType type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessengerConnManager.MsgType getType() {
        return type;
    }

    public void setType(MessengerConnManager.MsgType type) {
        this.type = type;
    }
}

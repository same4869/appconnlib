package com.wenba.appconn.config;

/**
 * Messenger的对应参数配置
 * Created by xunwang on 16/12/21.
 */

public class MessengerConfigParm {
    public static final String MSG_ARG = "msg_arg";

    //课堂互动
    public static final String MONKEYKING_SERVICE_ACTION = "com.wenba.monkeyking.appconn";
    //主launcher
    public static final String HAVOCINHEAVEN_SERVICE_ACTION = "com.wenba.havocinheaven.appconn";
    //课前预习
    public static final String LITTLEMONKEY_SERVICE_ACTION = "com.wenba.littlemonkey.appconn";
    //课后练习
    public static final String WUKONG_SERVICE_ACTION = "com.wenba.wukong.appconn";

    //老师端APP
    public static final String INCLASSTEACHER_SERVICE_ACTION = "com.wenba.inclassteacher.appconn";
    //老师端widget
    public static final String FLOATWIDGET_SERVICE_ACTION = "com.wenba.floatwidget.appconn";


    public static final int MSG_FROM_HAVOCINHEAVEN = 31;
    public static final int MSG_FROM_MONKEYKING = 32;
    public static final int MSG_FROM_LITTLEMONKEY = 33;
    public static final int MSG_FROM_WUKONG = 34;
    public static final int MSG_FROM_INCLASSTEACHER = 35;
    public static final int MSG_FROM_FLOATWIDGET = 36;
}

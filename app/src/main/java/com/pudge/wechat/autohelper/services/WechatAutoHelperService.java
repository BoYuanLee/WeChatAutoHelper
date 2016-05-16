package com.pudge.wechat.autohelper.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class WechatAutoHelperService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AccessibilityNodeInfo rootNodeInfo;

    private static int countvalue = 0;

    private static boolean IS_ROOM_MEMBER_UI = false;

    private static boolean IS_CONTACT_INFO_UI = false;

    private static boolean IS_ADD_FRIEND_UI = false;

    private static AccessibilityNodeInfo currentNodeInfo;

    private static int currentListItem = 1;

    private static boolean IS_CLICK_MEMBER_ITEM = false;

    private static boolean IS_HAVE_ADDED_FRIEND = false;

    private static boolean IS_CLICK_ADD_TO_CONTACT = false;

    private static boolean IS_CLICK_CONTACT_INFO_BACK = false;

    private static boolean IS_OVER = false;

    private static boolean IS_SEND_ADD_REQUEST = false;

    private static AccessibilityNodeInfo lastNodeInfo;

    @TargetApi(18)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i("accessibilityEvent", accessibilityEvent.toString());

            this.rootNodeInfo = getRootInActiveWindow();

            if (this.rootNodeInfo == null) {
                return;
            }

            int childCount = rootNodeInfo.getChildCount();

            Log.i("childCount", childCount + "");

            for (int i = 0; i < childCount; i++) {

                AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(i);

                int size = nodeInfo.getChildCount();
                Log.i("size", size + "");
                for (int j = 0; j < size; j++) {
                    AccessibilityNodeInfo subNodeInfo = nodeInfo.getChild(j);

                    Log.i("sub", subNodeInfo.getClassName() + "");
                    if (subNodeInfo.getClassName().equals("android.widget.ListView")) {
                        currentNodeInfo = subNodeInfo;
                        lastNodeInfo = subNodeInfo;
                        int listSize = subNodeInfo.getChildCount();
                        for (; currentListItem < listSize; currentListItem++) {
                            AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);

                            Log.i("listItemInfo Id", listItemInfo.getWindowId() + "");
                            Log.i("listItemInfo", listItemInfo.toString());
                            if (currentListItem == listSize - 1) {
                                subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                currentListItem = 0;
                                return;
                            }

//                    subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
//                    Log.i("sub sub size", subNodeInfo.getChildCount() + "");
                /*    Log.i("itemCount", subNodeInfo.getActionList().get(1).)*/
//                    countvalue += subNodeInfo.getChildCount();
                        Log.i("countvalue", countvalue + "");
                        // subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                }
          /*  AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(i);
            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);*/
            }



     /*  List<AccessibilityNodeInfo> subNodeInfo = rootNodeInfo.findAccessibilityNodeInfosByText("恒玉哥");
        if (subNodeInfo != null && subNodeInfo.size() > 0) {
            Log.i("subNodeInfo", subNodeInfo.get(0).getWindowId() + "");
            subNodeInfo.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

        }*/
        }

     /*   Log.i("IS_ROOM_MEMBER_UI", IS_ROOM_MEMBER_UI + "");
        Log.i("IS_CONTACT_INFO_UI", IS_CONTACT_INFO_UI + "");
        Log.i("IS_ADD_FRIEND_UI", IS_ADD_FRIEND_UI + "");
        Log.i("event class", accessibilityEvent.getClassName() + "");

        if (accessibilityEvent.getSource() != null  && "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())){
            IS_ADD_FRIEND_UI = false;
            IS_CONTACT_INFO_UI = false;
            IS_ROOM_MEMBER_UI = true;
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.ContactInfoUI".equals(accessibilityEvent.getClassName())){
            IS_ROOM_MEMBER_UI = false;
            IS_ADD_FRIEND_UI = false;
            IS_CONTACT_INFO_UI = true;
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI".equals(accessibilityEvent.getClassName())){
            IS_ROOM_MEMBER_UI = false;
            IS_CONTACT_INFO_UI = false;
            IS_ADD_FRIEND_UI = true;
        }

        if (IS_ROOM_MEMBER_UI) {
            // init the default value
            IS_HAVE_ADDED_FRIEND = false;
            IS_CLICK_ADD_TO_CONTACT = false;
            IS_CLICK_CONTACT_INFO_BACK = false;
            IS_OVER = false;
            IS_SEND_ADD_REQUEST = false;
            if (IS_CLICK_MEMBER_ITEM) {
                return;
            }

            this.rootNodeInfo = getRootInActiveWindow();
            if (this.rootNodeInfo == null) {
                return;
            }
            List<AccessibilityNodeInfo> nodeInfos = this.rootNodeInfo.findAccessibilityNodeInfosByText("测试员9527");
            if (nodeInfos != null && nodeInfos.size() > 0) {
                nodeInfos.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                IS_CLICK_MEMBER_ITEM = true;
            }
        }

        if (IS_CONTACT_INFO_UI) {
            if (IS_OVER && !IS_CLICK_CONTACT_INFO_BACK) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                IS_CLICK_CONTACT_INFO_BACK = true;
            } else {
                if (IS_CLICK_MEMBER_ITEM && !IS_CLICK_ADD_TO_CONTACT) {
                    findAndPerformAction("添加到通讯录", false);
                    IS_CLICK_ADD_TO_CONTACT = true;
                } else {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        }

        if (IS_ADD_FRIEND_UI) {
           if (!IS_OVER) {
                Log.i("isHaveSendButton", isHaveButton("发送") + "");
                if (isHaveButton("发送")) {
//                    findAndPerformAction("发送", false);
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    IS_HAVE_ADDED_FRIEND = true;
                    IS_OVER = true;

                    IS_CLICK_MEMBER_ITEM = false;
                }
            }
        }*/
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    private boolean isHaveButton(String text) {
        if (getRootInActiveWindow() == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        if (nodes == null) {
            return false;
        }

        return true;
    }

    @TargetApi(18)
    private void findAndPerformAction(String text, boolean isAppendParent) {

        if (getRootInActiveWindow() == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        if (nodes == null) {
            return;
        }

        Log.i("nodes size", nodes.size() + "");
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            Log.i("node info", node.toString());
            Log.i("text out", text);
            if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                Log.i("text in", text);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                Log.i("text in", text);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }
}

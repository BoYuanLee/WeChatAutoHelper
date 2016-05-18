package com.pudge.wechat.autohelper.services;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;

import java.util.List;


public class WechatAutoHelperService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AccessibilityNodeInfo rootNodeInfo;

    private static int currentListItem = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        this.rootNodeInfo = getRootInActiveWindow();

        if (this.rootNodeInfo == null) {
            return;
        }

        Log.i("accessibilityEvent", accessibilityEvent.toString());

        if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())) {

            int childCount = this.rootNodeInfo.getChildCount();

            Log.i("childCount", childCount + "");

            for (int i = 0; i < childCount; i++) {

                AccessibilityNodeInfo nodeInfo = this.rootNodeInfo.getChild(i);
                int size = nodeInfo.getChildCount();
                Log.i("size", size + "");
                for (int j = 0; j < size; j++) {
                    AccessibilityNodeInfo subNodeInfo = nodeInfo.getChild(j);
                    Log.i("sub", subNodeInfo.getClassName() + "");
                    if (subNodeInfo.getClassName().equals("android.widget.ListView")) {
                        int listSize = subNodeInfo.getChildCount();
                        for (; currentListItem < listSize; ) {
                            final AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    currentListItem++;
                                    listItemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }, 1000);
                        }
                        if (currentListItem == listSize) {
                            subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            currentListItem = 0;
                        }
                    }
                }
            }

        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.ContactInfoUI".equals(accessibilityEvent.getClassName())){

            if (isHaveButton("发消息")) {
                performGlobalAction(GLOBAL_ACTION_BACK);
             } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findAndPerformAction("添加到通讯录");
                        if (isHaveButton("发消息")) {
                            performGlobalAction(GLOBAL_ACTION_BACK);
                        }
                    }
                }, 1000);
            }
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.ui.base.p".equals(accessibilityEvent.getClassName())){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findAndPerformAction("发送");
                }
            }, 1000);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }


    private void findAndPerformAction(String text) {

        if (getRootInActiveWindow() == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            if ((node.getClassName().equals("android.widget.Button") || node.getClassName().equals("android.widget.TextView")) && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    private boolean isHaveButton(String text) {
        if (getRootInActiveWindow() == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodeInfos = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        if (nodeInfos == null || nodeInfos.size() < 1) {
            return  false;
        }

        for (int i = 0; i < nodeInfos.size(); i++) {
            AccessibilityNodeInfo nodeInfo = nodeInfos.get(i);
            if (nodeInfo.getClassName().equals("android.widget.Button") && nodeInfo.isEnabled()) {
                return true;
            }
        }
        return false;
    }
}

package com.pudge.wechat.autohelper.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;

import java.util.List;


public class WechatAutoHelperService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AccessibilityNodeInfo rootNodeInfo;

    private static int currentListItem = 1;

    private static boolean IS_SENDED = false;

    @TargetApi(18)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.i("accessibilityEvent", accessibilityEvent.toString());

        Log.i("Event ClassName", accessibilityEvent.getClassName().toString());

        if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())) {
            IS_SENDED = false;
            this.rootNodeInfo = getRootInActiveWindow();
            if (this.rootNodeInfo == null) {
                return;
            }

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

                        Log.i("listSize", listSize + "");

                        if (currentListItem == listSize) {
                            currentListItem = 0;
                            subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        } else {
                            for (; currentListItem < listSize; ) {
                                Log.i("currentListItem", currentListItem + "");
                                Log.i("subNodeInfo", subNodeInfo.toString());

                                final AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);
                                Log.i("listItemInfo", listItemInfo.toString());
                                currentListItem++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        listItemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    }
                                }, 2000);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.ContactInfoUI".equals(accessibilityEvent.getClassName())) {
            Log.i("添加到通讯录", "添加到通讯录");
            if (!IS_SENDED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("添加到通讯录 r", "添加到通讯录 r");
                        findAndPerformAction("添加到通讯录", false);
                        if (isHaveButton("发消息")) {
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                    }
                }, 2000);
            } else {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }


        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI".equals(accessibilityEvent.getClassName())) {
            Log.i("发送", "发送");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("发送 r", "发送 r");
                    IS_SENDED = true;
                    findAndPerformAction("发送", false);
                }
            }, 2000);
        }
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

        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            if ((node.getClassName().equals("android.widget.Button") || node.getClassName().equals("android.widget.TextView") ) && node.isEnabled()) {
              return true;
            }
        }
        return false;
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

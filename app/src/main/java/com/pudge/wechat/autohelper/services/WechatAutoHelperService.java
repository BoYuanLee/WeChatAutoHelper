package com.pudge.wechat.autohelper.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.pudge.wechat.autohelper.utils.HongbaoSignature;
import com.pudge.wechat.autohelper.utils.PowerUtil;

import java.util.List;


public class WechatAutoHelperService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AccessibilityNodeInfo rootNodeInfo;

    private static int countvalue = 0;

    private static boolean ISROOMMEMBER = false;

    private static AccessibilityNodeInfo currentNodeInfo;

    private static int currentListItem = 1;

    private static boolean HASADDFRIEND = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.i("event111", accessibilityEvent.toString());
        if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())) {
            ISROOMMEMBER = true;
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.ContactInfoUI".equals(accessibilityEvent.getClassName())){
            ISROOMMEMBER = false;
            if (HASADDFRIEND) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                return;
             }
            findAndPerformAction("添加到通讯录");
            return;
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.ui.base.p".equals(accessibilityEvent.getClassName())){
            ISROOMMEMBER = false;
            HASADDFRIEND = true;
            performGlobalAction(GLOBAL_ACTION_BACK);
            return;
        }

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
                    int listSize = subNodeInfo.getChildCount();
                    for (; currentListItem < listSize; currentListItem++) {
                        AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);
                        listItemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        if (currentListItem == listSize - 1) {
                            subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            currentListItem = 0;
                        }

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
            if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }
}

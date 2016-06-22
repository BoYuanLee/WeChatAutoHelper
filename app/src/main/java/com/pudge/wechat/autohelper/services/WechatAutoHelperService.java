package com.pudge.wechat.autohelper.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;


public class WechatAutoHelperService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AccessibilityNodeInfo rootNodeInfo;

    private static int currentListItem = 0;

    private static boolean IS_SENDED = false;

    private static boolean IS_GREETED = false;

    private static int CURRENT_ROOM_ITEM_COUNT = 0;

    private static boolean IS_ROOM_MEMBER_UI = false;

    private static boolean IS_NEARBY_FRIENDS_UI = false;

    private static boolean IS_HAVE_SEARCHED_NEARBY_FRIENDS = false;

    private static int TOTAL_COUNT = 0;

    private static boolean END = false;

    private static long DELAY_TIME = 2000;

    @TargetApi(18)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.i("event", accessibilityEvent.toString());

        if (accessibilityEvent.getSource() != null && ("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI".equals(accessibilityEvent.getClassName()) || (isHaveButton("附近的人") && isHaveButton("摇一摇")))) {
            currentListItem = 0;
            IS_SENDED = false;
            IS_GREETED = false;
            CURRENT_ROOM_ITEM_COUNT = 0;
            TOTAL_COUNT = 0;
            END = false;
            IS_NEARBY_FRIENDS_UI = false;
            IS_HAVE_SEARCHED_NEARBY_FRIENDS = false;
            return;
        }

        if (accessibilityEvent.getSource() != null && accessibilityEvent.getClassName().toString().contains("com.tencent.mm.plugin.") && !"com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())) {
            IS_ROOM_MEMBER_UI = false;
        }

        if (accessibilityEvent.getSource() != null && accessibilityEvent.getClassName().toString().contains("com.tencent.mm.plugin.") && !"com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI".equals(accessibilityEvent.getClassName())) {
            IS_NEARBY_FRIENDS_UI = false;
        }

        if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI".equals(accessibilityEvent.getClassName())) {
            if (END) {
                return;
            }
            IS_ROOM_MEMBER_UI = true;
            IS_SENDED = false;
            this.rootNodeInfo = getRootInActiveWindow();

            if (this.rootNodeInfo == null) {
                return;
            }

            int childCount = this.rootNodeInfo.getChildCount();

            for (int i = 0; i < childCount; i++) {

                AccessibilityNodeInfo nodeInfo = this.rootNodeInfo.getChild(i);

                int size = nodeInfo.getChildCount();

                for (int j = 0; j < size; j++) {

                    AccessibilityNodeInfo subNodeInfo = nodeInfo.getChild(j);

                    if (subNodeInfo.getClassName().equals("android.widget.ListView")) {

                        int listSize = subNodeInfo.getChildCount();

                        for (; currentListItem < listSize; ) {
                            CURRENT_ROOM_ITEM_COUNT++;

                            final AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);

                            currentListItem++;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listItemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }, DELAY_TIME);
                            break;
                        }

                        if (CURRENT_ROOM_ITEM_COUNT >= TOTAL_COUNT  + 10 && TOTAL_COUNT != 0) {
                            END = true;
                            return;
                        }

                        if (currentListItem == listSize) {
                            currentListItem = 0;
                            subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        }
                    }
                }
            }
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI".equals(accessibilityEvent.getClassName())) {
                if (END) {
                    return;
                }
                IS_NEARBY_FRIENDS_UI = true;
                IS_GREETED = false;

                rootNodeInfo = getRootInActiveWindow();
                if (rootNodeInfo == null) {
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

                        if (!IS_HAVE_SEARCHED_NEARBY_FRIENDS) {
                            Log.i("searched", "searched");
                            if (subNodeInfo.getClassName().equals("android.view.ViewGroup")) {
                                int viewCount = subNodeInfo.getChildCount();
                                for (int vi = 0; vi < viewCount; vi++) {
                                    AccessibilityNodeInfo viewSubNode = subNodeInfo.getChild(vi);
                                    Log.i("viewSubNode", viewSubNode.toString());
                                    if ("更多".equals(viewSubNode.getContentDescription()) && "android.widget.TextView".equals(viewSubNode.getClassName())) {
                                        Log.i("更多 content", "更多 content");
                                        viewSubNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                            }
                                        }, DELAY_TIME);
                                        IS_HAVE_SEARCHED_NEARBY_FRIENDS = true;
                                        continue;
                                    }
                                }
                            }
                        }



                        Log.i("sub", subNodeInfo.getClassName() + "");
                        if (subNodeInfo.getClassName().equals("android.widget.ListView")) {
                            int listSize = subNodeInfo.getChildCount();

                            Log.i("listSize", listSize + "");

                            for (; currentListItem < listSize; ) {
                                CURRENT_ROOM_ITEM_COUNT++;
                                Log.i("currentListItem", currentListItem + "");
                                Log.i("subNodeInfo", subNodeInfo.toString());


                                final AccessibilityNodeInfo listItemInfo = subNodeInfo.getChild(currentListItem);

                                Log.i("listItemInfo node text", listItemInfo.getText() + "");

                                Log.i("listItemInfo", listItemInfo.toString());

                                Log.i("listItemInfo", listItemInfo.toString());
                                currentListItem++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("shit", "shit");
                                        listItemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    }
                                }, DELAY_TIME);
                                break;
                            }

                            if (CURRENT_ROOM_ITEM_COUNT >= TOTAL_COUNT  + 10 && TOTAL_COUNT != 0) {
                                END = true;
                                return;
                            }

                            if (currentListItem == listSize) {
                                currentListItem = 0;
                                subNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }

                        }
                    }
                }
        } else if (accessibilityEvent.getSource() != null && IS_NEARBY_FRIENDS_UI && "android.widget.ListView".equals(accessibilityEvent.getClassName()) ) {
            TOTAL_COUNT = accessibilityEvent.getItemCount();
            Log.i("total count", accessibilityEvent.getItemCount() + "");
        } else if (IS_NEARBY_FRIENDS_UI && !IS_HAVE_SEARCHED_NEARBY_FRIENDS && "android.widget.TextView".equals(accessibilityEvent.getClassName())) {
            if (accessibilityEvent.getSource() != null) {
                String contentDescription = accessibilityEvent.getSource().getContentDescription() + "";
                if ("更多".equals(contentDescription)) {
                    Log.i("更多 text button","更多 text button");
                    accessibilityEvent.getSource().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IS_HAVE_SEARCHED_NEARBY_FRIENDS = true;
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                    }, DELAY_TIME);
                }
            }

        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.ContactInfoUI".equals(accessibilityEvent.getClassName())) {
            Log.i("添加到通讯录", "添加到通讯录");
            if (isHaveButton("发消息")) {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } else if (isHaveButton("打招呼")) {
                if (IS_GREETED) {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findAndPerformAction("打招呼", false);
                        }
                    }, DELAY_TIME);
                }
            } else {
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
                    }, DELAY_TIME);
                } else {
                    IS_SENDED = false;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            }
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI".equals(accessibilityEvent.getClassName())) {
            Log.i("发送", "发送");
            IS_SENDED = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("发送 r", "发送 r");
                    findAndPerformAction("发送", false);
                }
            }, DELAY_TIME);
        } else if (accessibilityEvent.getSource() != null && IS_ROOM_MEMBER_UI && "android.widget.ListView".equals(accessibilityEvent.getClassName()) ) {
            TOTAL_COUNT = accessibilityEvent.getItemCount();
            Log.i("total count", accessibilityEvent.getItemCount() + "");
        } else if (accessibilityEvent.getSource() != null && "com.tencent.mm.ui.chatting.ChattingUI".equals(accessibilityEvent.getClassName()) && isHaveButton("加为朋友")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findAndPerformAction("加为朋友", false);
                    IS_GREETED = true;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            }, DELAY_TIME);
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
            if ((node.getClassName().equals("android.widget.Button") || node.getClassName().equals("android.widget.TextView")) && node.isEnabled()) {
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

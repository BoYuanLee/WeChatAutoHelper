package com.pudge.wechat.autohelper.activities;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pudge.wechat.autohelper.R;
import com.pudge.wechat.autohelper.fragments.CommentSettingsFragment;
import com.pudge.wechat.autohelper.fragments.GeneralSettingsFragment;

/**
 * Created by Zhongyi on 1/19/16.
 * Settings page.
 */
public class SettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        loadUI();
        prepareSettings();
    }

    private void prepareSettings() {
        String title, fragId;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            fragId = bundle.getString("frag_id");
        } else {
            title = "偏好设置";
            fragId = "GeneralSettingsFragment";
        }

        TextView textView = (TextView) findViewById(R.id.settings_bar);
        textView.setText(title);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if ("GeneralSettingsFragment".equals(fragId)) {
            fragmentTransaction.replace(R.id.preferences_fragment, new GeneralSettingsFragment());
        } else if ("CommentSettingsFragment".equals(fragId)) {
            fragmentTransaction.replace(R.id.preferences_fragment, new CommentSettingsFragment());
        }
        fragmentTransaction.commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadUI() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffd84e43);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void performBack(View view) {
        super.onBackPressed();
    }

    public void enterAccessibilityPage(View view) {
        Intent mAccessibleIntent =
                new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(mAccessibleIntent);
    }
}

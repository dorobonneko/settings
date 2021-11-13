package com.moe.settings;

import android.app.Activity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class AutoBootActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getPackageManager().(new ComponentName("com.coloros.screenrecorder","com.coloros.screenrecorder.services.QuickPanelService"),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }
    
}

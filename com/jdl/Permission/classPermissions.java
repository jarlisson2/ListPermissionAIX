package com.jdl.Permission;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

public class classPermissions extends Activity {
    private int PERMISSION_REQUEST_CODE = 200;
    private List<String> permissionListGrated = new ArrayList<>();
    private List<String> permissionListDanied = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        List<String> permissionList = new ArrayList<>();
        String[] permissionListString = getIntent().getExtras().getStringArray("listReceiver");
        for (String permission : permissionListString) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            } else {
                permissionListGrated.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) classPermissions.this, permissionList.toArray(new String[permissionList.size()]),
                    PERMISSION_REQUEST_CODE);
        }
        else {
            finish();
        } 
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionListGrated.add(permissions[i]);
                } else {
                    permissionListDanied.add(permissions[i]);
                }
            }
            // YailList listPermissionsGrated = YailList.makeList(permissionListGrated);
            // YailList listPermissionsDanied = YailList.makeList(permissionListDanied);
            finish();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("Granted", permissionListGrated.toArray(new String[permissionListGrated.size()]));
        intent.putExtra("Danied", permissionListDanied.toArray(new String[permissionListDanied.size()]));
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
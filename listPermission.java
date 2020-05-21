package com.jdl.Permission;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.YailList;

@DesignerComponent(version = 1, description = "Request list permission <br> Developed by Jarlisson", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "https://image.flaticon.com/icons/png/512/1468/1468166.png",helpUrl="https://github.com/jarlisson2/ListPermissionAIX") // //
@SimpleObject(external = true)
public class listPermission extends AndroidNonvisibleComponent
            implements ActivityCompat.OnRequestPermissionsResultCallback {

      public Activity activity;
      public Context context;

      public void ReturnRequestPermission(YailList listPermissionsGrated, YailList listPermissionsDanied) {
            this.OnPermissionRequest(listPermissionsGrated, listPermissionsDanied);
      }

      final int PERMISSION_REQUEST_CODE = 200;

      public listPermission(ComponentContainer container) {
            super(container.$form());
            context = container.$context();
            activity = (Activity) context;

      }
      @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                  @NonNull int[] grantResults) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                  List<String> permissionListGrated = new ArrayList<>();
                  List<String> permissionListDanied = new ArrayList<>();
                  for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                              permissionListGrated.add(permissions[i]);
                        } else {
                              permissionListDanied.add(permissions[i]);
                        }
                  }
                  final YailList listPermissionsGrated = YailList.makeList(permissionListGrated);
                  final YailList listPermissionsDanied = YailList.makeList(permissionListDanied);
                  activity.runOnUiThread(new Runnable() {
                        public void run() {
                              OnPermissionRequest(listPermissionsGrated, listPermissionsDanied);
                        }
                  });
            }

      }

      @SimpleFunction(description = "In a list of permissions, a window will open for the user, whether or not to allow permissions that have not been granted.")
      public void RequestPermission(YailList listPermissions) {
            String[] permissionListString = listPermissions.toStringArray();
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissionListString) {
                  if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permission);
                  }
            }
            if (!permissionList.isEmpty()) {
                  ActivityCompat.requestPermissions((Activity) context,
                              permissionList.toArray(new String[permissionList.size()]), PERMISSION_REQUEST_CODE);
            }
      }

      @SimpleFunction(description = "Checks whether the mentioned permission has been granted, it will return true or false.")
      public boolean CheckSinglePermission(String permission) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                  return true;
            else
                  return false;
      }

      @SimpleFunction(description = "When Verifying that all the permissions mentioned in the list have been allowed, it will return true or false.")
      public boolean CheckGarantedAll(YailList listPermissions) {
            String[] permissionListString = listPermissions.toStringArray();
            Boolean garantedAll = true;
            for (int i = 0; i < permissionListString.length; i++) {
                  if (ContextCompat.checkSelfPermission(context,
                              permissionListString[i]) != PackageManager.PERMISSION_GRANTED)
                        garantedAll = false;
            }
            return garantedAll;
      }

      @SimpleFunction(description = "Parses a whitelist and returns an event (ResultCheckMultiplePermission) with a denied and allowed whitelist.")
      public void CheckMultiplePermissions(YailList listPermissions) {
            String[] permissionListString = listPermissions.toStringArray();
            List<String> permissionListGrated = new ArrayList<>();
            List<String> permissionListDanied = new ArrayList<>();
            for (int i = 0; i < permissionListString.length; i++) {
                  if (ContextCompat.checkSelfPermission(context,
                              permissionListString[i]) == PackageManager.PERMISSION_GRANTED) {
                        permissionListGrated.add(permissionListString[i]);
                  } else {
                        permissionListDanied.add(permissionListString[i]);
                  }
            }
            final YailList listPermissionsGrated = YailList.makeList(permissionListGrated);
            final YailList listPermissionsDanied = YailList.makeList(permissionListDanied);
            ResultCheckMultiplePermissions(listPermissionsGrated, listPermissionsDanied);
      }

      @SimpleEvent(description = "CheckMultiplePermission result.")
      public void ResultCheckMultiplePermissions(YailList listPermissionsGrated, YailList listPermissionsDanied) {
            EventDispatcher.dispatchEvent(this, "ResultCheckMultiplePermissions", listPermissionsGrated,
                        listPermissionsDanied);
      }

      @SimpleEvent(description = "RequestListPermission result.")
      public void OnPermissionRequest(YailList listPermissionsGrated, YailList listPermissionsDanied) {
            EventDispatcher.dispatchEvent(this, "OnPermissionRequest", listPermissionsGrated, listPermissionsDanied);
      }

}
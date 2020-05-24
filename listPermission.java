package com.jdl.Permission;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.ActivityResultListener;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.ReplForm;
import com.google.appinventor.components.runtime.util.YailList;

@DesignerComponent(version = 2, description = "Request list permission <br> Developed by Jarlisson", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "https://image.flaticon.com/icons/png/512/1468/1468166.png", helpUrl = "https://github.com/jarlisson2/ListPermissionAIX") // //
@UsesActivities(activities = {
            @ActivityElement(name = "com.jdl.Permission.classPermissions", theme = "@android:style/Theme.Translucent.NoTitleBar") })
@SimpleObject(external = true)

public class listPermission extends AndroidNonvisibleComponent implements Component, ActivityResultListener {
      public Activity activity;
      public Context context;
      public boolean isRepl = false;
      private int PERMISSION_REQUEST_CODE = 200;
      private ComponentContainer container;
      private int requestCode = 0;

      public listPermission(ComponentContainer container) {
            super(container.$form());
            this.container = container;
            context = container.$context();
            activity = (Activity) context;
            if (form instanceof ReplForm) {
                  isRepl = true;
            }
      }

      @SimpleFunction(description = "In a list of permissions, a window will open for the user, whether or not to allow permissions that have not been granted.")
      public void RequestPermission(YailList listPermissions) {
            String[] permissionListString = listPermissions.toStringArray();
            if (isRepl) {
                  List<String> permissionList = new ArrayList<>();
                  for (String permission : permissionListString) {
                        if (ContextCompat.checkSelfPermission(context,
                                    permission) != PackageManager.PERMISSION_GRANTED) {
                              permissionList.add(permission);
                        }
                  }
                  if (!permissionList.isEmpty()) {
                        ActivityCompat.requestPermissions((Activity) context,
                                    permissionList.toArray(new String[permissionList.size()]), PERMISSION_REQUEST_CODE);
                  }
            } else {
                  Intent intent = new Intent((Context) form, classPermissions.class);
                  intent.putExtra("listReceiver", permissionListString);
                  intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  if (requestCode == 0)
                        requestCode = form.registerForActivityResult(this);
                  container.$context().startActivityForResult(intent, requestCode);
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
      public void OnPermissionRequest(YailList listPermissionsGrated, YailList listPermissionsDanied,
                  boolean grantedAll) {
            EventDispatcher.dispatchEvent(this, "OnPermissionRequest", listPermissionsGrated, listPermissionsDanied,
                        grantedAll);

      }

      @Override
      public void resultReturned(int requestCode, int resultCode, Intent data) {
            if (resultCode == -1) {
                  String[] listGranted = data.getExtras().getStringArray("Granted");
                  String[] listDanied = data.getExtras().getStringArray("Danied");
                  final YailList listPermissionsGrated = YailList.makeList(listGranted);
                  final YailList listPermissionsDanied = YailList.makeList(listDanied);
                  final boolean grantedAll = listGranted.length > 0 && listDanied.length == 0 ? true : false;
                  OnPermissionRequest(listPermissionsGrated, listPermissionsDanied, grantedAll);
            }
      }

}
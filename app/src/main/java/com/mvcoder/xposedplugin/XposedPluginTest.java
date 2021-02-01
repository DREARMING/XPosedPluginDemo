package com.mvcoder.xposedplugin;

import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedPluginTest implements IXposedHookLoadPackage {

    private static final String TARGET_PKT_NAME = "com.mvcoder.xposetestdemo";
    private static final String TARGET_CLASS = "com.mvcoder.xposetestdemo.MainActivity";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        XposedBridge.log("Loaded app Test: " + param.packageName);
        if(!TextUtils.equals(param.packageName, TARGET_PKT_NAME)){
            return;
        }
        XposedBridge.log("find target class : " + TARGET_PKT_NAME + " , Version:" + BuildConfig.VERSION_NAME);
        XposedHelpers.findAndHookMethod(TARGET_CLASS, param.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("hook create method before..");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("hook create method after...");
                Class clazz = param.thisObject.getClass();
                Field field = clazz.getDeclaredField("tvTitle");
                if(field == null){
                    XposedBridge.log("can't find tvTitle field");
                    return;
                }
                field.setAccessible(true);
                Object textView = field.get(param.thisObject);
                Method method = field.getType().getDeclaredMethod("setText", CharSequence.class);
                method.invoke(textView, "XPosed hook");
                XposedBridge.log("xposed hook operation success!!");
            }
        });
    }
}

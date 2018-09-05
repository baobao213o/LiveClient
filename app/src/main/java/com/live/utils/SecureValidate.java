package com.live.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.live.LiveApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SecureValidate {

    public static String getPkgName() {
        String pkName = null;
        try {

            pkName = LiveApp.getInstance().getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkName;
    }

    public static int getVerCode() {
        int verCode = 0;
        try {
            String pkName = LiveApp.getInstance().getPackageName();
            verCode = LiveApp.getInstance().getPackageManager().getPackageInfo(pkName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String printSignatureMD5() {
        try {
            PackageInfo packageInfo = LiveApp.getInstance().getPackageManager().getPackageInfo(LiveApp.getInstance().getPackageName(), PackageManager.GET_SIGNATURES);

            return getMD5MessageDigest(packageInfo.signatures[0].toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMD5MessageDigest(byte[] bytes) {
        StringBuilder md5StringBuffer = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(bytes);
            byte[] digest = messageDigest.digest();
            for (byte aDigest : digest) {
                String hexString = Integer.toHexString(aDigest & 0xff);

                if (hexString.length() == 1)
                    md5StringBuffer.append("0");

                md5StringBuffer.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StringBuffer.toString();
    }


}

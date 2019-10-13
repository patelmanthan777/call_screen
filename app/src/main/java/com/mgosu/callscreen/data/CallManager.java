package com.mgosu.callscreen.data;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.mgosu.callscreen.service.ITelephonyService;
import com.mgosu.callscreen.service.NotificationCall;
import com.mgosu.callscreen.service.NotificationService;
import com.mgosu.callscreen.util.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CallManager {
    private static final String TAG = CallManager.class.getSimpleName();
    private final KeyguardManager keyguardManager;
    private AudioManager audioManager;
    private Context context;
    private static final String MANUFACTURER_HTC = "HTC";
    private CallStateReceiver callStateReceiver;

    public CallManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    private class CallStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: ");
        }
    }

    public void acceptCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm == null) {
                    throw new NullPointerException("tm == null");
                }
                String[] per = {Manifest.permission.ANSWER_PHONE_CALLS};
                if (!Utils.hasPermissions(context, per)) {
                    Utils.permission(context, per, 1123);
                } else {
                    tm.acceptRingingCall();
                    Log.e(TAG, "acceptCall: ");
                }
//            }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                deno();
//            updateWindowFlags();
            acceptCallAndroidM1();
                Log.e(TAG, "acceptCallAndroidM1 ");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                throughMediaController(context);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                throughAudioManager();
            }
        } catch (Exception e) {
            throughReceiver(context);
        }

    }

    private void deno() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            throw new NullPointerException("tm == null");
        }else {
            try {
                tm.getClass().getMethod("answerRingingCall").invoke(tm);
            } catch (IllegalAccessException e) {
                Log.e(TAG,"IllegalAccessException:"+ e.getMessage() );
            } catch (InvocationTargetException e) {
                Log.e(TAG,"InvocationTargetException:"+ e.getMessage() );
//                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException: "+e.getMessage() );
                e.printStackTrace();
            }
        }
    }
//        try {
//            ITelephony telephony;
//
//            TelephonyManager telephonyManager = (TelephonyManager) context.
//                    getSystemService(Context.TELEPHONY_SERVICE);
//            Class aClass = Class.forName(telephonyManager.getClass().getName());
//            Method method = aClass.getDeclaredMethod("getITelephony");
////
////
////            Method getITelephony = telephonyManager
////                    .getClass()
////                    .getDeclaredMethod("getITelephony");
//
//            method.setAccessible(true);
//
//            telephony = (ITelephony) method.invoke(telephonyManager);
//
//            telephony.answerRingingCall();
//
//        } catch (Exception ignored) {
//        }
//    }

    private ITelephonyService getTelephonyService(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            return (ITelephonyService) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void throughTelephonyService(Context context) {
        ITelephonyService telephonyService = getTelephonyService(context);
        if (telephonyService != null) {
            telephonyService.silenceRinger();
            telephonyService.answerRingingCall();
        }
    }

    private void throughAudioManager() {
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        audioManager.dispatchMediaKeyEvent(downEvent);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    private void throughReceiver(Context context) {
        try {
            throughTelephonyService(context);
        } catch (Exception exception) {
            boolean broadcastConnected = "HTC".equalsIgnoreCase(Build.MANUFACTURER)
                    && !audioManager.isWiredHeadsetOn();

            if (broadcastConnected) {
                broadcastHeadsetConnected(false, context);
            }
            try {
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK);
            } catch (IOException ioe) {
                throughPhoneHeadsetHook(context);
            } finally {
                if (broadcastConnected) {
                    broadcastHeadsetConnected(false, context);
                }
            }
        }
    }

    private void broadcastHeadsetConnected(boolean connected, Context context) {
        Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra("state", connected ? 1 : 0);
        intent.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(intent, null);
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void throughMediaController(Context context) {
        MediaSessionManager mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        try {
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(new ComponentName(context, NotificationService.class));
            for (MediaController controller : controllers) {
                if ("com.android.server.telecom".equals(controller.getPackageName())) {
                    controller.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    break;
                }
            }
        } catch (Exception e) {
            throughAudioManager();
        }
    }

    private void throughPhoneHeadsetHook(Context context) {
        Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
    }

    private void acceptCallAndroidM() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (MediaController mediaController : ((MediaSessionManager) context.getApplicationContext().getSystemService("media_session"))
                        .getActiveSessions(new ComponentName(context.getApplicationContext(), NotificationCall.class))) {
                    if ("com.android.server.telecom".equals(mediaController.getPackageName())) {
                        mediaController.dispatchMediaButtonEvent(new KeyEvent(1, 79));
                        return;
                    }
                }
            }
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
    }
    private void acceptCallAndroidM1() {
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(false,context);
        }

        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

            } catch (IOException e) {

                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                context.sendOrderedBroadcast(btnDown, enforcedPerm);
                context.sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false,context);
            }
        }
    }
}

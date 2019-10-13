package com.mgosu.callscreen.util;

import android.Manifest;
import android.os.Environment;

import com.mgosu.callscreen.R;

import java.io.File;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;

public class Constants {
    public static final int ITEM_PER_PAGE = 8;
    public static final int[] imageArray = {R.drawable.image_people1, R.drawable.image_people2,
            R.drawable.image_people3, R.drawable.image_people4, R.drawable.image_people5};
    public static final String KEY_PHONE = "phone";
    public static final String KEY_SEND_OBJ = "send_obj";
    public static final String ACTION_DISCONNECT = "action_disconnect";
    public static final String KEY_CALL = "call";
    public static final String[] permission_contact = {Manifest.permission.READ_CONTACTS};
    public static final String[] permission = {CALL_PHONE, ANSWER_PHONE_CALLS, CAMERA, READ_CONTACTS};
    public static final String[] permission1 = {CALL_PHONE, CAMERA, READ_CONTACTS};

    public static final String FOLDER_NAME = "Call Splash";

    public static final int KEY_REQUEST_CONTACT = 1130;
    public static final int KEY_DEFAUL = 201;
    public static final int KEY_REQUEST_CAMERA = 5243;
    public static final int KEY_REQUEST_PERMISSION = 1233;
    public static final int ID_ALL_CONTACT = 12834721;
    public static final int REQUEST_CHANGE = 1998;
    public static final String KEY_BOOLEAN = "intent_boolean";
    public static final String KEY_PATH_VIDEO = "path_video";
    public static final String KEY_NAME_CONTACT = "name_contact";
    public static final String KEY_PHONE_CONTACT = "phone_contact";
    public static final String KEY_URI_PATH = "path_image_uri";
    public static final String DISSMISS_CALL = "call_dissmis";

    public static final String NAME_REQUEST = "name_request";
    public static final String KEY_FRAGMENT = "key_fragment";

    public static final String KEY_DATE_CONTACT = "date_contact";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String ZIP = "zip";

    public static final String PATH_VIDEO_FOLDER = Environment.getExternalStorageDirectory() +
            File.separator + FOLDER_NAME;

    public static final int STATE_LOADING = 0;
    public static final int STATE_FAILED = -1;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_LOADING_MORE = 2;
}

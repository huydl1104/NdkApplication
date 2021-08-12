package com.example.ndk.util

import android.Manifest

/**
 * @author yudongliang
 * create time 2021-08-12
 * describe :
 */


val REQUEST_PERMISSIONS: Array<String> = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
)
package com.example.ndk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ndk.jniUtils.Jni;

public class NativeTestActivity extends AppCompatActivity {

    public Jni jni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_test);
        jni = new Jni();
        jni.testStruct();
    }


}
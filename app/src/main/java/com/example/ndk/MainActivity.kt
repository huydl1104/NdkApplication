package com.example.ndk

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ndk.adapter.MainAdapter
import com.example.ndk.adapter.OnItemCLickListener
import com.example.ndk.util.CommonUtils
import com.example.ndk.util.REQUEST_PERMISSIONS
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_CODE = 1

    private val dataList = arrayListOf(
        "ffmpeg_version_info"
    )

    private val listener = object: OnItemCLickListener {
        override fun onItemClick(case: String) {
            handlerClickEvent(case)
        }
    }

    private fun handlerClickEvent(case: String) {
        when(case){
            dataList[0]->{
                showDialog()
            }
        }
    }

    private val mainAdapter = MainAdapter(this, dataList).apply {
        onItemClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.run {
            layoutManager = GridLayoutManager(this@MainActivity,3)
            adapter = mainAdapter
        }

    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ffmpeg的版本信息")
        builder.setMessage(FFJni.getFFVersion())
        builder.show()
    }

    private fun startActivity(){

    }


    private fun hasPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        CommonUtils.copyAssetsDirToSDCard(this, "byteflow", "/sdcard")
        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, PERMISSIONS_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(this, "We need the permission: WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
}
package com.ehi.screencapturemonitor;

import android.Manifest.permission;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ehi.capture.CaptureCallback;
import com.ehi.capture.CaptureFileObserver;
import com.ehi.capture.MediaImageObserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean has = EasyPermissions
                .hasPermissions(this, permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE);
        if (!has) {
            EasyPermissions.requestPermissions(
                    this,
                    ".....",
                    10000,
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MediaImageObserver.getInstance(getApplication()).registerCaptureListener();
        MediaImageObserver.getInstance(getApplication()).setCaptureCallbackListener(imagePath ->
        {
            Log.d("Media_File",imagePath);
            bitmap = BitmapFactory.decodeFile(imagePath);

            ImageView iv = findViewById(R.id.iv_image);
            iv.setImageBitmap( compressBitmap(bitmap,200));
        });
    }

    private Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // 循环判断压缩后图片是否超过限制大小
        while(baos.toByteArray().length / 1024 > sizeLimit) {
            // 清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }

        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);

        return newBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
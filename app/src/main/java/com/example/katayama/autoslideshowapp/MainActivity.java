package com.example.katayama.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private Button prev;
    private Button next;
    private Button slide;
    ImageView imageView;
    //private int mPosition;
    //int id[] = {23,24,25,26};
    boolean mSlideshow = false;
    Cursor cursor;

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            if(mSlideshow){
                mHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        Log.d("ANDROID", "URI : " + imageUri);
                        imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    }
                });
            }
        }
    }
    Timer timer = new Timer();
    TimerTask mTimerTask = new MainTimerTask();
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
            } else {
            getContentsInfo();
        }
        prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        slide = (Button) findViewById(R.id.slide);
        slide.setOnClickListener(this);
        timer.schedule(mTimerTask, 0, 2000);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getCusor(){
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
    }

    private void getContentsInfo() {
        getCusor();
        if (cursor.moveToFirst()) {
            do {
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                imageVIew.setImageURI(imageUri);
                Log.d("android", imageUri.toString());
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.prev:
                prev();
                if(mSlideshow == false) {
                } else {
                    Toast.makeText(MainActivity.this,"再生中",Toast.LENGTH_SHORT).show();
                }
               break;
            case R.id.next:
                next();
                if(mSlideshow == false) {

                } else {
                    Toast.makeText(MainActivity.this,"再生中",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.slide:
                onSlide();
                break;
        }
    }

//    public void movePosition(int move) {
//        mPosition = mPosition + move;
//        if(mPosition >= id.length) {
//            mPosition = 0;
//        } else if(mPosition < 0){
//            mPosition = id.length - 1;
//        }
//    }

    public void onSlide() {
        mSlideshow = !mSlideshow;
    }
    public void next(){
        // 画像の情報を取得する
        getCusor();
        if(cursor.moveToFirst()) {
            cursor.moveToNext();
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                imageVIew.setImageURI(imageUri);
                Log.d("Next", imageUri.toString());
        }
    }
    public void prev(){
        // 画像の情報を取得する
        getCusor();
        if(cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
        cursor.moveToPrevious();
    }
}

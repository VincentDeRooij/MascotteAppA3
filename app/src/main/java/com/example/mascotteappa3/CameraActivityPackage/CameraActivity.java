package com.example.mascotteappa3.CameraActivityPackage;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.photogallery.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CameraActivity extends AppCompatActivity {

    Button btnTakePic;
    ImageView imageView;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    String pathToFile;
    Integer count;
    boolean started = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btnTakePic = findViewById(R.id.btnTakePic);
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakerAction();
            }
        });
        if(!SaveImages.getInstance().isHasImage()){
            count = 0;

            imageView = findViewById(R.id.image);
            imageView1 = findViewById(R.id.image1);
            imageView2 = findViewById(R.id.image2);
            imageView3 = findViewById(R.id.image3);
            started = false;

            System.out.println("start");
        }
        else {
            count = SaveImages.getInstance().getCount();
            //imageView = SaveImages.getInstance().getImageView();
            imageView = findViewById(R.id.image);
            imageView.setImageBitmap(SaveImages.getInstance().getBitmap());
            imageView1 = findViewById(R.id.image1);
            imageView1.setImageBitmap(SaveImages.getInstance().getBitmap1());
            imageView2 = findViewById(R.id.image2);
            imageView2.setImageBitmap(SaveImages.getInstance().getBitmap2());
            imageView3 = findViewById(R.id.image3);
            imageView3.setImageBitmap(SaveImages.getInstance().getBitmap3());
        }



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                setImageView(bitmap);

            }
        }
    }

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            photoFile = creatPhotoFile();
            if(photoFile != null){
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(CameraActivity.this, "com.a3.cameraandroid.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }

        }
    }

    private File creatPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());

        }
        return image;
    }

    private void setImageView(Bitmap bitmap){
        if(count == 0){
            SaveImages.getInstance().setBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
            count++;
        }else if(count == 1){
            SaveImages.getInstance().setBitmap1(bitmap);
            imageView1.setImageBitmap(bitmap);
            count++;
        }else if(count == 2){
            SaveImages.getInstance().setBitmap2(bitmap);
            imageView2.setImageBitmap(bitmap);
            count++;
        }else if(count ==3){
            SaveImages.getInstance().setBitmap3(bitmap);
            imageView3.setImageBitmap(bitmap);
            count = 0;
        }
        SaveImages.getInstance().setCount(count);

        SaveImages.getInstance().setHasImage(true);
    }
}
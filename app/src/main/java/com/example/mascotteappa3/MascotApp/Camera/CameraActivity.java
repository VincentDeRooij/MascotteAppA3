package com.example.mascotteappa3.MascotApp.Camera;

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

import com.example.mascotteappa3.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CameraActivity extends AppCompatActivity {

    Button btnTakePic; // button that takes the user to the cameraApp
    ImageView imageView; // imageView
    ImageView imageView1; // imageView
    ImageView imageView2; // imageView
    ImageView imageView3;// imageView
    String pathToFile; // pathname to the picture file
    Integer count; // amount of pictures taken
    boolean started = true;
    Bitmap bitmap; // picture bitmap

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
                //takes a picture
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

    // saves the picture taken to the gallery location
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathToFile);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    // sets the imageView with the picture taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                bitmap = BitmapFactory.decodeFile(pathToFile);
                setImageView(bitmap);
            }
        }
    }

    // handles the picture conversion and saving
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

    // creates the photo file name
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

    // fills the imageViews
    private void setImageView(Bitmap bitmap){
        if(count == 0){
            SaveImages.getInstance().setBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
            galleryAddPic();
            count++;
        }else if(count == 1){
            SaveImages.getInstance().setBitmap1(bitmap);
            imageView1.setImageBitmap(bitmap);
            galleryAddPic();
            count++;
        }else if(count == 2){
            SaveImages.getInstance().setBitmap2(bitmap);
            imageView2.setImageBitmap(bitmap);
            galleryAddPic();
            count++;
        }else if(count ==3){
            SaveImages.getInstance().setBitmap3(bitmap);
            imageView3.setImageBitmap(bitmap);
            galleryAddPic();
            count = 0;
        }
        SaveImages.getInstance().setCount(count);

        SaveImages.getInstance().setHasImage(true);
    }
}
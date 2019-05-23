package com.example.mascotteappa3.MascotApp.Camera;

import android.graphics.Bitmap;

public class SaveImages {

    private static SaveImages instance;

    private boolean hasImage;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    public Bitmap getBitmap3() {
        return bitmap3;
    }

    public void setBitmap3(Bitmap bitmap3) {
        this.bitmap3 = bitmap3;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public static SaveImages getInstance(){
        if(instance == null){
            instance = new SaveImages();
        }
        return instance;
    }

    private SaveImages(){
        hasImage = false;
    }
}

package com.example.mascotteappa3.CameraActivityPackage.MapView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.mascotteappa3.R;

public class NavigationHelper {
    private static NavigationHelper instance;

    private NavigationHelper() {

    }

    public Intent onNavigationItemSelected(@NonNull MenuItem menuItem, AppCompatActivity activity) {
        Intent intent = null;
        if(menuItem.getItemId() == R.id.nav_item_map) {
            intent = new Intent(activity, MapActivity.class);
        }
        if(menuItem.getItemId() == R.id.nav_item_pictures) {
            intent = new Intent(activity, MapActivity.class);
        }

        return intent;
    }

    public static NavigationHelper getInstance() {
        if(instance == null) {
            instance = new NavigationHelper();
        }
        return instance;
    }
}

package com.example.mascotteappa3.MascotApp.MapView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.mascotteappa3.MascotApp.Informatie.Information;
import com.example.mascotteappa3.MascotApp.Sensors.GPSTracker;
import com.example.mascotteappa3.MascotApp.Camera.CameraActivity;
import com.example.mascotteappa3.R;

public class NavigationHelper { // handler class for the sideBar in the app
    private static NavigationHelper instance;

    private NavigationHelper() {

    }

    // menu selector, this method handles the menu selecting and starts up a new intent
    public Intent onNavigationItemSelected(@NonNull MenuItem menuItem, AppCompatActivity activity) {
        Intent intent = null;
        if(menuItem.getItemId() == R.id.nav_item_map) {
            intent = new Intent(activity, MapActivity.class);
        }
        if(menuItem.getItemId() == R.id.nav_item_pictures) {
            intent = new Intent(activity, CameraActivity.class);
        }
        if (menuItem.getItemId() == R.id.nav_item_info){
            intent = new Intent(activity, Information.class);
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

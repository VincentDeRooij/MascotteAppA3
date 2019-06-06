package com.example.mascotteappa3.MascotApp.MapView;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.mascotteappa3.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MapView mapView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private ImageView img1 = null;
    private ImageView img2 = null;
    private ImageView img3 = null;
    private ImageView img4 = null;
    private boolean rood = true;
    private boolean blauw = true;
    private boolean groen = true;
    private boolean geel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.acces_token));

        setContentView(R.layout.activity_map);

        img1 = findViewById(R.id.mascotte1);
        img1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(rood) {
                    img1.setImageResource(R.drawable.mascotterood);
                }
                else{
                    img1.setImageResource(R.drawable.mascotteg);
                }
                rood = !rood;
            }
        });
        img2 = findViewById(R.id.mascotte2);
        img2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(blauw) {
                    img2.setImageResource(R.drawable.mascotteblauw);
                }
                else{
                    img2.setImageResource(R.drawable.mascotteg);
                }
                blauw = !blauw;
            }
        });
        img3 = findViewById(R.id.mascotte3);
        img3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(groen) {
                    img3.setImageResource(R.drawable.mascottegroen);
                }
                else{
                    img3.setImageResource(R.drawable.mascotteg);
                }
                groen = !groen;
            }
        });
        img4 = findViewById(R.id.mascotte4);
        img4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(geel) {
                    img4.setImageResource(R.drawable.mascottegeel);
                }
                else{
                    img4.setImageResource(R.drawable.mascotteg);
                }
                geel = !geel;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/vwjapderooij/cjvqha9wx0t8w1co93osiftuh"), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        //Add the marker image to map
//                        style.addImage("marker-icon-id",
//                                BitmapFactory.decodeResource(
//                                        MapActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
//
//                        SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
//                        SymbolLayer symbolLayer2 = new SymbolLayer("layer-id2", "source-id2");
//                        symbolLayer.withProperties(
//                                PropertyFactory.iconImage("marker-icon-id")
//                        );
//                        symbolLayer2.withProperties(
//                                PropertyFactory.iconImage("marker-icon-id")
//                        );
//                        style.addLayer(symbolLayer);
//                        style.addLayer(symbolLayer2);
                    }
                });
            }
        });
    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //All methods for menu.
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent = NavigationHelper.getInstance().onNavigationItemSelected(menuItem, this);

        if(intent == null) {
            return false;
        }

        startActivity(intent);
        return true;
    }
}
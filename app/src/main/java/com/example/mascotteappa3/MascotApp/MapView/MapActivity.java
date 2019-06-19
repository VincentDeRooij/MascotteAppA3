package com.example.mascotteappa3.MascotApp.MapView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;

import android.widget.Button;

import android.widget.Toast;

import com.example.mascotteappa3.MascotApp.MQTT.IMQTT;
import com.example.mascotteappa3.MascotApp.MQTT.MascotMQTT;
import com.example.mascotteappa3.MascotApp.Sensors.GPSTracker;
import com.example.mascotteappa3.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class MapActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        PermissionsListener,
        IMQTT {

    private MapView mapView;
    private MapboxMap mapboxMap;

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

    private SymbolLayer symbolBlue;
    private SymbolLayer symbolRed;
    private SymbolLayer symbolYellow;
    private SymbolLayer symbolGreen;

    private GeoJsonSource sourceBlue;
    private GeoJsonSource sourceRed;
    private GeoJsonSource sourceYellow;
    private GeoJsonSource sourceGreen;

    private PermissionsManager permissionsManager;
    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    // Variables needed to listen to location updates
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);

    private GPSTracker gps = new GPSTracker();
    private Context mContext; // necessary for the GPS tracker to function


    public Map<String,GPSCoordinate> lastCoordinates = new HashMap<>();
    public Map<String,GeoJsonSource> mapMarkers = new HashMap<>();


    private Button reconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clientId = MqttClient.generateClientId();
        MascotMQTT mascotMQTT = new MascotMQTT(this, this, clientId, this);
        mascotMQTT.connect();

        // This till
        mContext = this;
        Log.d("Main", "Main started");
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, MapActivity.this);


            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line, this is for the system logs
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }


        // This is necessary for the built-in GPS to function properly

        Mapbox.getInstance(this, getString(R.string.acces_token));

        setContentView(R.layout.activity_map);

        img1 = findViewById(R.id.mascotte1);
        img1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                rood = !rood;
                if(rood) {
                    img1.setImageResource(R.drawable.mascotterood);
                }
                else{

                    img1.setImageResource(R.drawable.mascotteg);
                }
            }
        });
        img2 = findViewById(R.id.mascotte2);
        img2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                blauw = !blauw;
                if(blauw) {
                    img2.setImageResource(R.drawable.mascotteblauw);
                }
                else{
                    img2.setImageResource(R.drawable.mascotteg);
                }
            }
        });
        img3 = findViewById(R.id.mascotte3);
        img3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                groen = !groen;
                if(groen) {
                    img3.setImageResource(R.drawable.mascottegroen);
                }
                else{
                    img3.setImageResource(R.drawable.mascotteg);
                }
            }
        });
        img4 = findViewById(R.id.mascotte4);
        img4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                geel = !geel;
                if(geel) {
                    img4.setImageResource(R.drawable.mascottegeel);
                }
                else{
                    img4.setImageResource(R.drawable.mascotteg);
                }
            }
        });

        img1.setImageResource(R.drawable.mascotterood);
        img2.setImageResource(R.drawable.mascotteblauw);
        img3.setImageResource(R.drawable.mascottegroen);
        img4.setImageResource(R.drawable.mascottegeel);

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
        mapView.getMapAsync(this);

        // Start services
        try {
            Intent intent = new Intent(MapActivity.this, MascotMQTT.class);
            startService(intent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        ArrayList<Feature> symbolLayerIconFeatureList = new ArrayList<>();

        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/vwjapderooij/cjvqha9wx0t8w1co93osiftuh?optimize=true")
                        .withImage("MascotteBlue", BitmapFactory.decodeResource(MapActivity.this.getResources(), R.drawable.mascotteblauw)) // init of the blue mascot tracker
                        .withSource(sourceBlue = new GeoJsonSource("MascotteBlueSource", FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                        .withImage("MascotteRed", BitmapFactory.decodeResource(MapActivity.this.getResources(), R.drawable.mascotterood)) // init of the red mascot tracker
                        .withSource(sourceRed = new GeoJsonSource("MascotteRedSource", FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                        .withImage("MascotteYellow", BitmapFactory.decodeResource(MapActivity.this.getResources(), R.drawable.mascottegeel)) // init of the yellow mascot tracker
                        .withSource(sourceYellow = new GeoJsonSource("MascotteYellowSource", FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                        .withImage("MascotteGreen", BitmapFactory.decodeResource(MapActivity.this.getResources(), R.drawable.mascottegroen)) // init of the green mascot tracker
                        .withSource(sourceGreen = new GeoJsonSource("MascotteGreenSource", FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                        .withLayer(symbolBlue = new SymbolLayer("Mascotte-BlueLayer", "MascotteBlueSource") // loading of the Blue Mascot TrackerIcon
                                .withProperties(PropertyFactory.iconImage("MascotteBlue"),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, 0f}),
                                        iconSize(0.09f))
                        )
                        .withLayer(symbolRed = new SymbolLayer("Mascotte-RedLayer", "MascotteRedSource") // loading of the Red Mascot TrackerIcon
                                .withProperties(PropertyFactory.iconImage("MascotteRed"),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, 0f}),
                                        iconSize(0.09f))
                        )
                        .withLayer(symbolYellow = new SymbolLayer("Mascotte-YellowLayer", "MascotteYellowSource") // loading of the Yellow Mascot TrackerIcon
                                .withProperties(PropertyFactory.iconImage("MascotteYellow"),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, 0f}),
                                        iconSize(0.09f))
                        )
                        .withLayer(symbolGreen = new SymbolLayer("Mascotte-GreenLayer", "MascotteGreenSource") // // loading of the Green Mascot TrackerIcon
                                .withProperties(PropertyFactory.iconImage("MascotteGreen"),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, 0f}),
                                        iconSize(0.09f))
                        )
                , new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                        //sourceGreen.setGeoJson(Point.fromLngLat(4.791761, 51.585800));
                        updateMarkerPosition(4.791961, 51.585800, sourceBlue);
                        mapMarkers.put("1",sourceBlue);
                        updateMarkerPosition(4.791761, 51.586800, sourceRed);
                        mapMarkers.put("2",sourceRed);
                        updateMarkerPosition(4.791661, 51.586600, sourceYellow);
                        mapMarkers.put("3",sourceYellow);
                        updateMarkerPosition(4.791861, 51.586800, sourceGreen);
                        mapMarkers.put("4",sourceGreen);
                        //updateMarkerPosition(4.791761, 51.587800, sourceBlue);


                    }
                });
    }

    private void updateMarkerPosition(double longitude, double latitude, GeoJsonSource source) {
        Log.d("Marker update", "New marker location");
        source.setGeoJson(Point.fromLngLat(longitude, latitude));
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.location_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, R.string.location_explanation, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMessageArrived(String message) {
        Log.d("MQTT", "Message received by MapActivity");
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has("Coordinaat"))
            {
                Log.d("MQTT", "Type received: Coordinaat");
                String id = jsonObject.getJSONObject("Coordinaat").getString("id");
                double latitude = jsonObject.getJSONObject("Coordinaat").getDouble("latitude");
                double longitude = jsonObject.getJSONObject("Coordinaat").getDouble("longitude");
                lastCoordinates.put(id,new GPSCoordinate(latitude,longitude));
                /*GPSCoordinate gpsCoordinate = new GPSCoordinate(latitude, longitude);
                Log.d("Latitude", gpsCoordinate.getLatitude() + "");
                Log.d("Longitude", gpsCoordinate.getLongitude() + "");*/
                //Use the GPSCoordinate class because the conversion of lat and long happens inside the constructor.
                Log.d("MQTT", "Handled Coordinaat for mascot " + id);

            }
            else if (jsonObject.has("Mascotte")) {
                Log.d("MQTT", "Type received: Mascotteknop");
                String id = jsonObject.getJSONObject("Mascotte").getString("id");
                if (lastCoordinates.containsKey(id)) {
                    updateMarkerPosition(lastCoordinates.get(id).getLongitude(), lastCoordinates.get(id).getLatitude(), mapMarkers.get(id));
                    Log.d("MQTT", "Handled Mascotteknop for mascot  " + id);
                    lastCoordinates.remove(id);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class MainActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapActivity> activityWeakReference;

        MainActivityLocationCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Create a Toast which displays the new location's coordinates
                String textToShow = String.format(activity.getString(R.string.new_location) +
                        (result.getLastLocation().getLatitude()) +
                        (result.getLastLocation().getLongitude()));

                Toast.makeText(activity, textToShow, Toast.LENGTH_SHORT).show();

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
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

        if (intent == null) {
            return false;
        }

        startActivity(intent);
        return true;
    }
}
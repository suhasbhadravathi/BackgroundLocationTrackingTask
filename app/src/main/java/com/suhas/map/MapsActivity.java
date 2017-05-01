package com.suhas.map;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //Google Map declaration
    GoogleMap mMap;

    Boolean isInitLocation = false;
    private static final long INTERVAL = 1000 * 30 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 30 * 1;

    private String  startedTime,stoppedTime;
    private int hours,min;
    DataDictionary dataDictionary;
    //Google ApiClient
    private GoogleApiClient googleApiClient;

    Location mCurrentLocation;

    //GCM Location
    LocationRequest mLocationRequest;

    private   Double  distance = 0.00;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        createLocationRequest();

         dataDictionary = new DataDictionary();

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();


        mMap = mapFragment.getMap();
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }


    //Getting started location
    private void getStartedLocation() {

        //Getting started time
          startedTime = DateFormat.getTimeInstance().format(new Date());
        // Set Distace to 0.00
        distance  = 0.00;
        dataDictionary.setTotalDistance(0.00);
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude

            dataDictionary.setStartedLatitude(location.getLatitude());
            dataDictionary.setStartedLongitude(location.getLongitude());

            //moving the map to location
            moveMapToStartingLocation();
        }
    }

    //Getting current location
    private void initLocation() {
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        //Initate location for first time and move map
        if(!isInitLocation) {

            if (location != null) {
                //Getting longitude and latitude
                dataDictionary.setStartedLatitude(location.getLatitude());
                dataDictionary.setStartedLongitude(location.getLongitude());

                dataDictionary.setCurrentLatitude(location.getLatitude());
                dataDictionary.setCurrentLongitude(location.getLongitude());

                dataDictionary.setTotalDistance(0.0);
                //moving the map to location
                //Creating a LatLng Object to store Coordinates
                LatLng latLng = new LatLng(dataDictionary.getStartedLatitude(), dataDictionary.getStartedLongitude());

                //Adding marker to map
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                //Moving the camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //Animating the camera
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                isInitLocation = true;
            }
        }
    }

    //Getting stopped location
    private void getStoppedLocation() {

        //Getting stopped time
          stoppedTime = DateFormat.getTimeInstance().format(new Date());
        //Getting actual time taken
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date startedDate = simpleDateFormat.parse(startedTime);
            Date stoppedDate = simpleDateFormat.parse(stoppedTime);
            long difference = stoppedDate.getTime() - startedDate.getTime();

            int days = (int) (difference / (1000*60*60*24));
             hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
             min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            dataDictionary.setStoppedLatitude(location.getLatitude());
            dataDictionary.setStoppedLongitude(location.getLongitude());
            //moving the map to location
            moveMapToStoppedLocation();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    // To get continuous location updates
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        // Get first current location
        initLocation();

        //Get location on location change
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            //77.5713 -- 12.9767
            dataDictionary.setLatitudeOnLocationChange(location.getLatitude());
            dataDictionary.setLongitudeOnLocationChange(location.getLongitude());

            LatLng from = new LatLng(dataDictionary.getLatitudeOnLocationChange(), dataDictionary.getLongitudeOnLocationChange());
            LatLng to = new LatLng(dataDictionary.getCurrentLatitude(), dataDictionary.getCurrentLongitude());

            //Calculating the distance in meters
             distance = SphericalUtil.computeDistanceBetween(from, to);

            dataDictionary.setTotalDistance( dataDictionary.getTotalDistance() + distance );
            dataDictionary.setLatitudeOnLocationChange(dataDictionary.getCurrentLatitude());
            dataDictionary.setLongitudeOnLocationChange(dataDictionary.getCurrentLongitude());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }
    //To map to started location
    private void moveMapToStartingLocation() {

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(dataDictionary.getStartedLatitude(), dataDictionary.getStartedLongitude());

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //Setting position
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //Function to move the map
    private void moveMapToStoppedLocation() {
        //Creating a LatLng Object to store Coordinates

        LatLng latLng = new LatLng(dataDictionary.getStoppedLatitude(), dataDictionary.getStoppedLongitude());

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //Setting position
                .draggable(true)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public String makeURL (double startedLatitude, double startedLongitude, double stoppedLatitude, double stoppedLongitude ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(startedLatitude));
        urlString.append(",");
        urlString
                .append(Double.toString( startedLongitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( stoppedLatitude));
        urlString.append(",");
        urlString.append(Double.toString(stoppedLongitude));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&AIzaSyASgbZ01joLP_RYvH2RXETFNgoIlE_QYoQ");
        return urlString.toString();
    }

    private void getDirection(){
        //Getting the URL using latitude and longituted
        String url = makeURL(dataDictionary.getStartedLatitude(),dataDictionary.getStartedLongitude(), dataDictionary.getStoppedLatitude(), dataDictionary.getStoppedLongitude());
        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //The parameter is the server response
    public void drawPath(String  result) {

        Toast.makeText(this, "Total shift time  " + hours+ "." + min  + "h  " + String.valueOf(dataDictionary.getTotalDistance().shortValue()) +"m" , Toast.LENGTH_LONG).show();

        int color = Color.rgb(135,206,250);
        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            // JSONArray durationArray = json.getJSONArray("legs");
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(color)
                    .geodesic(true)
            );
        }
        catch (JSONException e) {
                e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            mMap.clear();
            getStartedLocation();
        } else {
            startLocationUpdates();
            getStoppedLocation();
            getDirection();
        }
    }
}
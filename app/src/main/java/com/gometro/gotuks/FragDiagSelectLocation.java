package com.gometro.gotuks;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by wprenison on 2015/11/27.
 */
public class FragDiagSelectLocation extends DialogFragment implements View.OnClickListener, SearchView.OnQueryTextListener, OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    //Views
    private SearchView svSearch;
    private Button btnHelp;
    private MapView mvMapContent;
    private GoogleMap gmMap;
    private Button btnDone;
    private GifImageView loadingCircle;

    //Vars
    private ActivityDisplayStream activity;
    private String locationToBeSelected;
    private GeocoderResultReceiver geocoderResultReceiver;
    private Marker selectedLocationMarker;
    private Address selectedAddress;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Set style
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Check android version
        int layourResource = R.layout.diag_frag_select_location;

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            layourResource = R.layout.diag_frag_select_location_pre_loli; //Use perloli layout

        View constructedView = inflater.inflate(layourResource, container, false);

        //Get handel on views
        svSearch = (SearchView) constructedView.findViewById(R.id.svFSLSearchBar);
        btnHelp = (Button) constructedView.findViewById(R.id.btnFSLHelp);
        mvMapContent = (MapView) constructedView.findViewById(R.id.mVFSLMapContent);
        btnDone = (Button) constructedView.findViewById(R.id.btnFSLDone);
        loadingCircle = (GifImageView) constructedView.findViewById(R.id.imgvFSLLoadingCircle);

        return constructedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        activity = (ActivityDisplayStream) getActivity();

        //Set animations
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnimations;
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        initMap(savedInstanceState);
        init();
    }

    private void init()
    {
        //Check screen size if small adjust map view height
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        /*Log.d("fragSelLoc", "Density = " + metrics.densityDpi);
        Log.d("fragSelLoc", "LDensity = " + DisplayMetrics.DENSITY_LOW);
        Log.d("fragSelLoc", "MDensity = " + DisplayMetrics.DENSITY_MEDIUM);
        Log.d("fragSelLoc", "HDensity = " + DisplayMetrics.DENSITY_HIGH);
        Log.d("fragSelLoc", "XHDensity = " + DisplayMetrics.DENSITY_XHIGH);
        Log.d("fragSelLoc", "XXHDensity = " + DisplayMetrics.DENSITY_XXHIGH);
        Log.d("fragSelLoc", "XXXHDensity = " + DisplayMetrics.DENSITY_XXXHIGH);*/
        if(metrics.densityDpi <= DisplayMetrics.DENSITY_MEDIUM)
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mvMapContent.getLayoutParams();
            params.height = 250;
            mvMapContent.setLayoutParams(params);
        }

        //Set click listeners
        btnDone.setOnClickListener(this);
        btnHelp.setOnClickListener(this);

        //Expand search view
//        svSearch.setIconified(false);
        svSearch.setOnQueryTextListener(this);

        geocoderResultReceiver = new GeocoderResultReceiver(new Handler());

        //Check for init data
    }

    private void initMap(Bundle savedInstanceState)
    {
        mvMapContent.onCreate(savedInstanceState);
        mvMapContent.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gmMap = googleMap;
        googleMap.setOnMapClickListener(this);

        //Animate map to UP campus
        //Animate camera to center SA -29.366486, 24.959918 zoom: 4
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(-25.753383, 28.237867), 15.5f);
        gmMap.animateCamera(camUpdate);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mvMapContent.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mvMapContent.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mvMapContent.onDestroy();
    }

    private boolean validate()
    {
        boolean valid = true;

        if(selectedLocationMarker == null)
        {
            valid = false;
            Toast.makeText(activity, "No Location Selected", Toast.LENGTH_LONG).show();
        }

        return valid;
    }

    @Override
    public void onClick(View v)
    {
        if(v == btnDone)
        {
            if(validate())
            {
                //Save to trip and dismiss dialog
                activity.setReportAddress(formatAddress(selectedAddress), selectedAddress.getLatitude(), selectedAddress.getLongitude());
                this.dismiss();
            }
        }
        else if(v == btnHelp)
        {
//            activity.showSelectedLocationHelp(tripIndex, locationToBeSelected, false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        //Display loading circle
        loadingCircle.setVisibility(View.VISIBLE);

        //Search address provided
        Intent geocoderIntent = new Intent(activity, IntentServiceGeocoder.class);
        geocoderIntent.putExtra("resultReceiver", geocoderResultReceiver);
        geocoderIntent.putExtra("fetchType", 0);
        geocoderIntent.putExtra("addressName", query);

        activity.startService(geocoderIntent);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        //Display loading circle
        loadingCircle.setVisibility(View.VISIBLE);

        //Remove previous address marker if on exists
        if(selectedLocationMarker != null)
            selectedLocationMarker.remove();

        //Geocode from location
        Intent geocoderIntent = new Intent(activity, IntentServiceGeocoder.class);
        geocoderIntent.putExtra("resultReceiver", geocoderResultReceiver);
        geocoderIntent.putExtra("fetchType", 1);

        //Convert LatLng to Location
        Location location = new Location("MapView");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        geocoderIntent.putExtra("location", location);

        activity.startService(geocoderIntent);
    }

    /*@Override
    public void onMapClick(LatLng latLng)
    {
        //Display loading circle
        loadingCircle.setVisibility(View.VISIBLE);

        //Remove previous address marker if on exists
        if(selectedLocationMarker != null)
            mvMapContent.removeMarker(selectedLocationMarker);

        //Geocode from location
        Intent geocoderIntent = new Intent(activity, IntentServiceGeocoder.class);
        geocoderIntent.putExtra("resultReceiver", geocoderResultReceiver);
        geocoderIntent.putExtra("fetchType", 1);

        //Convert ILatLng to Location
        Location location = new Location("MapView");
        location.setLatitude(latLng.getLatitude());
        location.setLongitude(latLng.getLongitude());
        geocoderIntent.putExtra("location", location);

        activity.startService(geocoderIntent);
    }*/

    private String formatAddress(Address address)
    {
        StringBuilder fromattedAddress = new StringBuilder();

        int addressLineCount = address.getMaxAddressLineIndex();    //Leave out last field which is country, users know what country they are in
        for(int i = 0; i < addressLineCount; i++)
            if((addressLineCount - i) > 1)
                fromattedAddress.append(address.getAddressLine(i) + ", ");
            else
                fromattedAddress.append(address.getAddressLine(i));

        return fromattedAddress.toString();
    }

    //Inner class to receive geocoder results
    class GeocoderResultReceiver extends ResultReceiver
    {
        private final String TAG = "GeocoderResultReceiver";

        public GeocoderResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            if(resultCode == 1)
            {
                final Address address = resultData.getParcelable("addressResult");

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        selectedAddress = address;

                        //Remove possible previous existing marker
                        if(selectedLocationMarker != null)
                            selectedLocationMarker.remove();

                        String title = "Facility";

                        //Create marker
                        MarkerOptions selectedLocationMarkerOptions = new MarkerOptions().title(title).snippet(formatAddress(address)).position(new LatLng(address.getLatitude(), address.getLongitude()));

                        //Add & Create icon
                        selectedLocationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_black));

                        //Add marker to map and selected marker for future removal
                        selectedLocationMarker = gmMap.addMarker(selectedLocationMarkerOptions);

                        //animate to selected location
                        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 14);
                        gmMap.animateCamera(camUpdate);

                        /*mapbox 0.72 selectedLocationMarker = new Marker(title, formatAddress(address), new LatLng(address.getLatitude(), address.getLongitude()));
                        selectedLocationMarker.setIcon(new Icon(getResources().getDrawable(R.drawable.pin_unkown_pin)));
                        mvMapContent.addMarker(selectedLocationMarker);
//                        mvMapContent.selectMarker(selectedLocationMarker, true);
                        mvMapContent.getController().animateTo(new ILatLng()
                        {
                            @Override
                            public double getLatitude()
                            {
                                return address.getLatitude();
                            }

                            @Override
                            public double getLongitude()
                            {
                                return address.getLongitude();
                            }

                            @Override
                            public double getAltitude()
                            {
                                return 14;
                            }
                        });*/

                        //Hide loading circle
                        loadingCircle.setVisibility(View.GONE);
                    }
                });

            }
            else
                Log.e(TAG, "Geocoder failed: " + resultData.getString("errorMsg"));
        }
    }
}

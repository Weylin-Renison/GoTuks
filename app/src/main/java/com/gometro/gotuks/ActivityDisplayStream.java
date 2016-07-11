package com.gometro.gotuks;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.protobuf.InvalidProtocolBufferException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wprenison on 2015/09/25.
 */
public class ActivityDisplayStream extends AppCompatActivity
{
    private final int MAX_RETRY = 5;
    private final int UPDATE_INTERVAL = 5;
    private final double START_LOC_LAT = -25.753383;
    private final double START_LOC_LON = 28.237867;
    private final double START_LOC_ZOOM = 15.5;


    private String TAG = "DisplayStream";
    private MapView mvDisplayStream;
    PublisherAdView mPublisherAdView;
    private RelativeLayout relLayBusKey;

    private List<Marker> lstVehicleMarkers;
    private List<Marker> lstPoiMarkers;
    public List<GeoJsonPOI> lstPoiGeoJsonMarkers;
    private int retryCount = 0;
    private boolean reqCompleted = true;
    private Timer timer;

    private FragmentManager fragMang;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stream);

        //Hide status notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mvDisplayStream = (MapView) findViewById(R.id.mvADSmap);
        mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);

        fragMang = getSupportFragmentManager();

        lstVehicleMarkers = new ArrayList<>();
        lstPoiMarkers = new ArrayList<>();
        lstPoiGeoJsonMarkers = new ArrayList<>();

        initMap();
        initBusKey();
        initAdBanner();

    }

    private void initMap()
    {
        mvDisplayStream.setCenter(new ILatLng()
        {
            @Override
            public double getLatitude()
            {
                return START_LOC_LAT;
            }

            @Override
            public double getLongitude()
            {
                return START_LOC_LON;
            }

            @Override
            public double getAltitude()
            {
                return START_LOC_ZOOM;
            }
        });
        mvDisplayStream.setZoom((float) START_LOC_ZOOM);

        JSONObject featCollection = readGeoJsonAsset();

        if(featCollection != null)
        {
            //Add points of interests and route lines
            try
            {
                JSONArray features = featCollection.getJSONArray("features");


                for(int i = 0; i < features.length(); i++)
                {
                    JSONObject featObj = features.getJSONObject(i);
                    JSONObject featProperties = featObj.getJSONObject("properties");
                    JSONObject featGeometry = featObj.getJSONObject("geometry");

                    String featType = featGeometry.getString("type");

                    if(featType.equalsIgnoreCase("Point"))
                    {
                        JSONArray featSchedule = featObj.getJSONArray("schedules");

                        //Add poi marker
                        GeoJsonPOI poi = new GeoJsonPOI(featProperties, featGeometry, featSchedule);
                        Marker poiMarker = poi.getPOIMarker();

                        //Set marker pin
                        poiMarker.setIcon(new Icon(getResources().getDrawable(R.drawable.pin_bus_stop)));
                        mvDisplayStream.addMarker(poiMarker);
                        lstPoiMarkers.add(poiMarker);
                        poi.setMarker(poiMarker);
                        lstPoiGeoJsonMarkers.add(poi);
                    }
                    else if(featType.equalsIgnoreCase("LineString"))
                    {
                        //Draw line overlay
                        GeoJsonPath geoJsonPath = new GeoJsonPath(featProperties, featGeometry);
                        geoJsonPath.paintPath(mvDisplayStream);
                    }
                    else
                        Log.e(TAG, "Unknown feature type");
                }
            }
            catch (JSONException je)
            {
                je.printStackTrace();
            }
        }

        //Add on click events for poi markers
        ItemizedIconOverlay itemizedOverlay = new ItemizedIconOverlay(this, lstPoiMarkers, new ItemizedIconOverlay.OnItemGestureListener<Marker>()
        {
            @Override
            public boolean onItemSingleTapUp(int i, Marker marker)
            {
                //Handle click here
                //Find geo json marker associated with this map marker
                int geoJsonMarkerIndex = -1;
                for(int j = 0; j < lstPoiGeoJsonMarkers.size(); j++)
                {
                    Marker comapreMarker = lstPoiGeoJsonMarkers.get(j).getMarker();
                    if(comapreMarker == marker)
                        geoJsonMarkerIndex = j;
                }

                if(geoJsonMarkerIndex != -1)
                {
                    //Create and display schedule dialog
                    FragDiagStopSchedule fragStopDetails = new FragDiagStopSchedule();
                    Bundle args = new Bundle();
                    args.putInt("stopIndex", geoJsonMarkerIndex);
                    fragStopDetails.setArguments(args);

                    fragStopDetails.show(fragMang, "FragDiagStopSchedule");
                }
                return true;
            }

            @Override
            public boolean onItemLongPress(int i, Marker marker)
            {
                return false;
            }
        });

        mvDisplayStream.addItemizedOverlay(itemizedOverlay);

        //Add points of interest (only here because there is only one for now)
//        Marker poiMarker = new Marker("University of Pretoria", "\"Make Today Matter\" - Prof. Cheryl de la Rey", new LatLng(START_LOC_LAT, START_LOC_LON));
//        poiMarker.setIcon(new Icon(getResources().getDrawable(R.drawable.ic_poi_up)));
//
//        lstPoiMarkers.add(poiMarker);
//        mvDisplayStream.addMarker(poiMarker);
    }

    private JSONObject readGeoJsonAsset()
    {
        BufferedReader reader = null;
        JSONObject featureCollection = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("featuresJSON.json")));

            String tempReadString;
            String stringJSON = "";
            while((tempReadString = reader.readLine()) != null){ stringJSON += tempReadString + "\n";}

            featureCollection = new JSONObject(stringJSON);

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }

        return featureCollection;
    }

    private void initBusKey()
    {
        relLayBusKey = (RelativeLayout) findViewById(R.id.relLayADSBusKey);

        Animation animPopUp = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top);
        animPopUp.setDuration(1000);
        relLayBusKey.startAnimation(animPopUp);
    }

    private void initAdBanner()
    {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                checkStream();
            }
        }, 0, UPDATE_INTERVAL * 1000);
    }

    public void checkStream()
    {
        //Send for upload to server
        RequestParams params = new RequestParams();
        //params.put("imei", "001Abc");
        params.put("streamId", "1");

        //Get url
        SharedPreferences prefMang = PreferenceManager.getDefaultSharedPreferences(this);
        String SERVER_ADDRESS = prefMang.getString("SERVER_ADDRESS", null);
        String SERVER_API_DOWNSTREAM = prefMang.getString("SERVER_API_DOWNSTREAM", null);

        Log.i(TAG, "URL:" + SERVER_ADDRESS + SERVER_API_DOWNSTREAM);

        final AsyncHttpClient client = new SyncHttpClient();
        client.setUserAgent("android");
        client.post(SERVER_ADDRESS + SERVER_API_DOWNSTREAM, params,
                    new AsyncHttpResponseHandler()
                    {

                        @Override
                        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes)
                        {
//                            txtvTestStream.append("\n" + i);
                            try
                            {
//                                Log.i(TAG, "No of bytes received: " + bytes.length);

                                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                                StreamPacketProtos.StreamPacket streamPacket = StreamPacketProtos.StreamPacket.parseDelimitedFrom(bais);

                                List<StreamPacketProtos.StreamPacket.StreamData> streamDataList = streamPacket.getStreamDataList();

                                //Remove all markers
                                for (int k = 0; k < lstVehicleMarkers.size(); k++)
                                {
                                    final Marker removeVehicleMarker = lstVehicleMarkers.get(k);

                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            mvDisplayStream.removeMarker(removeVehicleMarker);
                                        }
                                    });
                                }


                                lstVehicleMarkers.clear();  //Clear array to not keep old data resulting in out of memmory

                                for (int j = 0; j < streamDataList.size(); j++)
                                {
                                    StreamPacketProtos.StreamPacket.StreamData streamData = streamDataList.get(j);
                                    StreamPacketProtos.StreamPacket.StreamData.LocData locData = streamData.getLocData();
                                    StreamPacketProtos.StreamPacket.StreamData.StatusData statusData = streamData.getStatusData();


                                    /*Log.i(TAG, "VehicleId: " + streamData.getVehicleId()
                                            + " DriverId: " + streamData.getDriverId()
                                            + "\nLat: " + locData.getLat() + ", Lon: " + locData.getLon()
                                            + " Bearing: " + locData.getBearing() + " Accuracy: " + locData.getAccuracy()
                                            + " Speed: " + locData.getSpeed()
                                            + "\nBus Full: " + statusData.getBusFull() + " Heavy Traffic: " + statusData.getHeavyTraffic()
                                            + " Bus Empty: " + statusData.getBusEmpty());*/

                                    Marker vehicleMarker = new Marker("" + streamData.getVehicleId(), "UP bus:\nSpeed: " + locData.getSpeed()
                                            + "\nAccuracy: " + locData.getAccuracy() + "\nStatus: BusFull - " + statusData.getBusFull()
                                            + " Heavy Traffic - " + statusData.getHeavyTraffic() + " BusEmpty - " + statusData.getBusEmpty()
                                            , new LatLng(locData.getLat(), locData.getLon()));

                                    //Get a dynamically selected drawable dep on vehicle status
                                    int ic_dynamic_drawable = getStatusDrawableResource(statusData);

                                    Drawable ic_vehicle = rotateToBearing(locData.getBearing(), ic_dynamic_drawable);
                                    vehicleMarker.setIcon(new Icon(ic_vehicle));

                                    vehicleMarker.setHotspot(Marker.HotspotPlace.CENTER);

                                    final Marker vehicleMarkerFinal = vehicleMarker;

                                    lstVehicleMarkers.add(vehicleMarkerFinal);

                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            mvDisplayStream.addMarker(vehicleMarkerFinal);
                                        }
                                    });

                                    /*if(lstVehicleMarkers.size() > 0)
                                    {
                                        for (int k = 0; k < lstVehicleMarkers.size(); k++)
                                        {
                                            //TODO: Change this to dynamic detection
                                            if (Integer.parseInt(lstVehicleMarkers.get(k).getTitle()) == streamData.getVehicleId())
                                            {
                                                Marker updateMarker = lstVehicleMarkers.get(k);
                                                mvDisplayStream.removeMarker(lstVehicleMarkers.get(k));
                                                //Update location and values instead of adding new
                                                updateMarker.setPoint(new LatLng(locData.getLat(), locData.getLon()));
                                                updateMarker.setDescription("UP bus:\nSpeed: " + locData.getSpeed()
                                                                                    + "\nAccuracy: " + locData.getAccuracy() + "\nStatus: BusFull - " + statusData.getBusFull()
                                                                                    + " Heavy Traffic - " + statusData.getHeavyTraffic() + " BusEmpty - " + statusData.getBusEmpty());

                                                Drawable ic_vehicle = rotateToBearing(locData.getBearing(), R.drawable.ic_bus);
                                                updateMarker.setIcon(new Icon(ic_vehicle));

                                                mvDisplayStream.addMarker(lstVehicleMarkers.get(k));

                                            } else
                                            {
                                                //TODO: CHange to methods
                                                Marker vehicleMarker = new Marker("" + streamData.getVehicleId(), "UP bus:\nSpeed: " + locData.getSpeed()
                                                        + "\nAccuracy: " + locData.getAccuracy() + "\nStatus: BusFull - " + statusData.getBusFull()
                                                        + " Heavy Traffic - " + statusData.getHeavyTraffic() + " BusEmpty - " + statusData.getBusEmpty()
                                                        , new LatLng(locData.getLat(), locData.getLon()));

                                                Drawable ic_vehicle = rotateToBearing(locData.getBearing(), R.drawable.ic_bus);
                                                vehicleMarker.setIcon(new Icon(ic_vehicle));

                                                lstVehicleMarkers.add(vehicleMarker);
                                                mvDisplayStream.addMarker(vehicleMarker);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Marker vehicleMarker = new Marker("" + streamData.getVehicleId(), "UP bus:\nSpeed: " + locData.getSpeed()
                                                + "\nAccuracy: " + locData.getAccuracy() + "\nStatus: BusFull - " + statusData.getBusFull()
                                                + " Heavy Traffic - " + statusData.getHeavyTraffic() + " BusEmpty - " + statusData.getBusEmpty()
                                                , new LatLng(locData.getLat(), locData.getLon()));

                                        Drawable ic_vehicle = rotateToBearing(locData.getBearing(), R.drawable.ic_bus);
                                        vehicleMarker.setIcon(new Icon(ic_vehicle));

                                        lstVehicleMarkers.add(vehicleMarker);
                                        mvDisplayStream.addMarker(vehicleMarker);
                                    }*/

                                }


                            } catch (InvalidProtocolBufferException ipbe)
                            {
                                ipbe.printStackTrace();
                                Log.e(TAG, "An Invalid protocol buffer exception occured: " + ipbe.getMessage());
                            } catch (IOException ioe)
                            {
                                ioe.printStackTrace();
                                Log.e(TAG, "An IOException occured: " + ioe.getMessage());
                            }

                        }

                        @Override
                        public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable)
                        {
                            Log.e(TAG, "Down Stream failed: " + i);
                        }

                        @Override
                        public void onFinish()
                        {
                            super.onFinish();
                        }
                    });

    }

    private int getStatusDrawableResource(StreamPacketProtos.StreamPacket.StreamData.StatusData statusData)
    {
        int drawableRes = R.drawable.ic_bus_big;

        if(statusData.getBusEmpty() && !statusData.getHeavyTraffic())
            drawableRes = R.drawable.ic_bus_g;
        else if(statusData.getBusFull() && !statusData.getHeavyTraffic())
            drawableRes = R.drawable.ic_bus_r;
        else if(statusData.getHeavyTraffic() && !statusData.getBusEmpty() && !statusData.getBusFull())
            drawableRes = R.drawable.ic_bus_y;
        else if(statusData.getBusEmpty() && statusData.getHeavyTraffic())
            drawableRes = R.drawable.ic_bus_g_y;
        else if(statusData.getBusFull() && statusData.getHeavyTraffic())
            drawableRes = R.drawable.ic_bus_r_y;

        return drawableRes;
    }

    private Drawable rotateToBearing(float bearing, int drawableResource)
    {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableResource);
        // Getting width & height of the given image.
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(bearing);
        // Rotating Bitmap
        Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
        BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);

        //Print bearing
//        Toast.makeText(this, "Bearing: " + bearing, Toast.LENGTH_LONG).show();

        return bmd;
    }

    @Override
    protected void onPause()
    {
        timer.cancel();
        super.onPause();
    }
}
package com.gometro.gotuks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.gometro.gotuks.StreamPacketProtos.StreamPacket;
import com.gometro.gotuks.StreamPacketProtos.StreamPacket.StreamData;
import com.gometro.gotuks.StreamPacketProtos.StreamPacket.StreamData.LocData;
import com.gometro.gotuks.StreamPacketProtos.StreamPacket.StreamData.StatusData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private final String TAG = "MainActivity";

    private TextView txtvTestStream;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtvTestStream = (TextView) findViewById(R.id.txtvTestStream);
    }

    public void onClickTestStream(View view)
    {
       checkStream();
    }

    public void onClickDisplayMap(View view)
    {
        Intent intentDisplayMap = new Intent(MainActivity.this, ActivityDisplayStream.class);
        startActivity(intentDisplayMap);
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

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(240 * 1000);
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
                                StreamPacket streamPacket = StreamPacket.parseDelimitedFrom(bais);

                                List<StreamData> streamDataList = streamPacket.getStreamDataList();

                                txtvTestStream.setText("");
                                for (int j = 0; j < streamDataList.size(); j++)
                                {
                                    StreamData streamData = streamDataList.get(j);
                                    LocData locData = streamData.getLocData();
                                    StatusData statusData = streamData.getStatusData();


                                    txtvTestStream.append("VehicleId: " + streamData.getVehicleId()
                                                                  + " DriverId: " + streamData.getDriverId()
                                                                  + "\nLat: " + locData.getLat() + ", Lon: " + locData.getLon()
                                                                  + " Bearing: " + locData.getBearing() + " Accuracy: " + locData.getAccuracy()
                                                                  + " Speed: " + locData.getSpeed()
                                                                  + "\nBus Full: " + statusData.getBusFull() + " Heavy Traffic: " + statusData.getHeavyTraffic()
                                                                  + " Bus Empty: " + statusData.getBusEmpty());

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
                    });
    }
}

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sirius.seoulapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsFragment extends Fragment
        implements OnMapReadyCallback, OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, Serializable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = getClass().getName();
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Row> rowList;
    private ArrayList<LatLng> latlngs;
    private LatLng currentPosition;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button addBtn;
    private Button calBtn;
    private boolean isSearchedFirstCurrentPosition;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopService();
        }
    };

    private void stopService(){
        Intent stopIntent = new Intent(getContext(),
                CalculatePositionService.class);
        getContext().stopService(stopIntent);
    }

    private void setLatLngs() {
        Log.d(TAG, "setLatLngs");
        latlngs = new ArrayList<LatLng>();
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            LatLng latlng = new LatLng(Double.valueOf(row.getWGS84_Y()), Double.valueOf(row.getWGS84_X()));
            latlngs.add(latlng);
        }
    }

    private void setMarkerforSeouldata() {
        Log.d(TAG, "setMarker");
        MarkerOptions marker = new MarkerOptions();
        for (int i = 0; i < latlngs.size(); i++) {
            marker.position(latlngs.get(i));
            marker.title(rowList.get(i).getNAME_KOR());
            googleMap.addMarker(marker);
        }
    }

    private void setMarker(LatLng tempLatLng, String title, String snippet) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(tempLatLng);
        marker.title(title);
        marker.snippet(snippet);
        googleMap.addMarker(marker);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markeradd, null);
        final EditText editTitle = (EditText) linear.findViewById(R.id.title);
        final EditText editSnippet = (EditText) linear.findViewById(R.id.snippet);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
                .setTitle("Marker 추가하기")
                .setIcon(R.drawable.ic_place_black_24dp)
                .setView(linear)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = editTitle.getText().toString();
                        String snippet = editSnippet.getText().toString();
                        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(snippet)) {
                            Toast.makeText(getContext(), "Cannot be Empty!", Toast.LENGTH_LONG).show();
                            editTitle.setText("");
                            editSnippet.setText("");
                            dialogInterface.dismiss();
                            return;
                        }
                        setMarker(currentPosition, editTitle.getText().toString(), editSnippet.getText().toString());
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editTitle.setText("");
                        editSnippet.setText("");
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog alertDialog = alert.create();
        addBtn = (Button) v.findViewById(R.id.addbtn);
        addBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        calBtn = (Button) v.findViewById(R.id.calbtn);
        calBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSearchedFirstCurrentPosition) {
                    Toast.makeText(getContext(), "현재 위치가 아직 조회되지 않았습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, String.valueOf(currentPosition.latitude) + "," + String.valueOf(currentPosition.longitude));
                Intent intent = new Intent(getContext(), CalculatePositionService.class);
                intent.putExtra("rowList", rowList);
                intent.putExtra("currentPosition", currentPosition);
                getContext().startService(intent);
            }
        });


        Bundle bundle = getArguments();
        rowList = (ArrayList<Row>) bundle.getSerializable("rowList");

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        googleMap = map;
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMinZoomPreference(5);
        googleMap.setMaxZoomPreference(15);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown())
                    marker.hideInfoWindow();
                else marker.showInfoWindow();
                return false;
            }
        });

        setLatLngs();
        setMarkerforSeouldata();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void setCurrentPosition(double latitude, double longitude) {
        Log.d(TAG, "setCurrentPosition");
        currentPosition = new LatLng(latitude, longitude);
        if (!isSearchedFirstCurrentPosition) {
            isSearchedFirstCurrentPosition = true;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
        }
    }

    @Override
    public void onPause() {
        isSearchedFirstCurrentPosition = false;
        super.onPause();
        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        isSearchedFirstCurrentPosition = false;
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "onMyLocationButtonClick");
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        setCurrentPosition(location.getLatitude(), location.getLongitude());
        //Toast.makeText(getContext(), "onLocationChanged"+location.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

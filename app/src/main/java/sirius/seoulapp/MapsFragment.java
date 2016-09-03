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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
        implements OnMapReadyCallback, OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener, Serializable {

    private final String TAG = getClass().getName();
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Row> rowList;
    private ArrayList<LatLng> latlngs;

    private void setLatLngs(){
        Log.d(TAG, "setLatLngs");
        for(int i=0; i<rowList.size(); i++){
            Row row = rowList.get(i);
            latlngs.add(new LatLng(new Double(row.getWGS84_X()), new Double(row.getWGS84_Y())));
        }
    }

    private void setMarker(){
        Log.d(TAG, "setMarker");
        for(int i=0; i<latlngs.size(); i++){
            googleMap.addMarker(new MarkerOptions().position(latlngs.get(i)).title(rowList.get(i).getNM_DP()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        Bundle bundle = getArguments();
        rowList = (ArrayList<Row>) bundle.getSerializable("rowList");

        setLatLngs();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        googleMap = map;
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);
        setMarker();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

//    @Override
//    public void onBackPressed() {
//        CameraPosition cameraPosition = mMap.getCameraPosition();
//        Log.d("asd", new Double(cameraPosition.target.latitude).toString());
//        Log.d("asd", new Double(cameraPosition.target.longitude).toString());
//        return;
//    }


    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "onMyLocationButtonClick");
        return false;
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "onCameraIdle");
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        Log.d("latitude", String.valueOf(cameraPosition.target.latitude));
        Log.d("longitude", String.valueOf(cameraPosition.target.longitude));
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.d(TAG, "onCameraMoveCanceled");
    }

    @Override
    public void onCameraMove() {
        Log.d(TAG, "onCameraMove");
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.d(TAG, "onCameraMoveStarted");
    }
}


package sirius.seoulapp.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import sirius.seoulapp.R;
import sirius.seoulapp.seouldata.Row;

public class MapsFragment extends Fragment
        implements OnMapReadyCallback, OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = getClass().getName();
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Row> rowList;
    private ArrayList<LatLng> latlngs;
    private LatLng currentPosition;
    private boolean isSearchedFirstCurrentPosition;
    private LocationReceiver locationReceiver;
    private IntentFilter intentFilter;
    private Intent AutoSearchingintent;
    private LatLng defaultPosition;
    private transient Context mContext;
    private MaterialDialog materialDialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentPosition", currentPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContext = getContext();
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        final LinearLayout linear = (LinearLayout) View.inflate(mContext, R.layout.markeradd, null);
        final EditText editTitle = (EditText) linear.findViewById(R.id.title);
        final EditText editSnippet = (EditText) linear.findViewById(R.id.snippet);
        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext)
                .setTitle("Marker 추가하기")
                .setIcon(R.drawable.ic_place_black_24dp)
                .setView(linear)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = editTitle.getText().toString();
                        String snippet = editSnippet.getText().toString();
                        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(snippet)) {
                            Toast.makeText(mContext, "Cannot be Empty!", Toast.LENGTH_LONG).show();
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
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(int index, MaterialSimpleListItem item) {
                // TODO
                switch(index){
                    case 0:
                        alertDialog.show();
                        break;
                    case 1:
                        clearAllMarker();
                        break;
                    case 2:
                        if(!isRunningAutoSearch)
                            startAutoSearch();
                        else
                            stopAutoSearch();
                        break;
                    default:
                        break;
                }
                materialDialog.dismiss();
            }
        });

        adapter.add(new MaterialSimpleListItem.Builder(mContext)
                .content("Add")
                .icon(R.drawable.ic_add_circle_white_24dp)
                .backgroundColor(Color.MAGENTA)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(mContext)
                .content("Clear")
                .icon(R.drawable.ic_all_out_white_24dp)
                .backgroundColor(Color.MAGENTA)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(mContext)
                .content("Auto Search")
                .backgroundColor(Color.MAGENTA)
                .icon(R.drawable.ic_autorenew_white_36dp)
                .build());

        materialDialog = new MaterialDialog.Builder(mContext)
                .title("설정")
                .adapter(adapter, null)
                .dividerColorRes(R.color.colorAccent)
                .build();

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.show();

            }
        });

        Bundle bundle = getArguments();
        rowList = (ArrayList<Row>) bundle.getSerializable("rowList");
        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getParcelable("currentPosition");
        }
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

        locationReceiver = new LocationReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(LocationReceiver.mBroadcastStringAction);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mContext.registerReceiver(locationReceiver, intentFilter);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        defaultPosition = new LatLng(37.552527,126.990762);
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPosition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition,12.5f));
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

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

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        startLocationService();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        stopLocationService();
        super.onPause();
        isSearchedFirstCurrentPosition = false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopAutoSearch();
    }

    public GoogleMap getGoogleMap(){ return googleMap; }

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

    private void setCurrentPosition(LatLng newPosition) {
        Log.d(TAG, "setCurrentPosition");
        currentPosition = newPosition;
        if (!isSearchedFirstCurrentPosition) {
            isSearchedFirstCurrentPosition = true;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
        }
        Log.d(TAG, String.valueOf(currentPosition));
    }

    public LatLng getCurrentPosition(){
        return currentPosition;
    }

    public void clearAllMarker(){
        googleMap.clear();
    }

    public void startLocationService(){
        final Intent intent = new Intent(mContext, LocationService.class);
        mContext.startService(intent);
    }
    public void stopLocationService(){
        final Intent intent = new Intent(mContext, LocationService.class);
        mContext.stopService(intent);
    }

    private boolean isRunningAutoSearch;
    public boolean getisRunningAutoSearch(){ return isRunningAutoSearch; }

    public void startAutoSearch(){
        if (!isSearchedFirstCurrentPosition) {
            Toast.makeText(mContext, "현재 위치가 아직 조회되지 않았습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        isRunningAutoSearch = true;
        AutoSearchingintent = new Intent(mContext, CalculatePositionService.class);
        AutoSearchingintent.putExtra("currentPosition", currentPosition);
        AutoSearchingintent.putExtra("rowList", rowList);
        mContext.startService(AutoSearchingintent);
    }

    public void stopAutoSearch(){
        if(isRunningAutoSearch) {
            isRunningAutoSearch = false;
            mContext.stopService(AutoSearchingintent);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "onMyLocationButtonClick");
        return false;
    }

    public class LocationReceiver extends BroadcastReceiver {
        public static final String mBroadcastStringAction = "sirius.seoulapp.broadcast.Location";
        private LatLng newPosition;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastStringAction)){
                Log.d(TAG, "onReceive");
                newPosition = (LatLng) intent.getParcelableExtra("currentPosition");
                setCurrentPosition(newPosition);
                if(isRunningAutoSearch) {
                    startAutoSearch();
                }
            }
        }
    }
}

package sirius.seoulapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import sirius.seoulapp.map.MapsFragment;
import sirius.seoulapp.seouldata.Row;

public class MustVisitListFragment extends Fragment implements Serializable, AdapterView.OnItemSelectedListener {

    private final String TAG = getClass().getName();
    private MapsFragment mapsFragment;
    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private ArrayList<Row> rowList;
    private ArrayList<Row> selectedrowList;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> guList;
    private ArrayList<String> title;
    private ArrayList<String> address;
    private int contentLength;
    private GoogleMap googleMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        googleMap = mapsFragment.getGoogleMap();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.mustvisitlist_recyclerview, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        adapter = new ContentAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
        setSpinner();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setRowList(ArrayList<Row> rowList){ this.rowList = rowList; }

    public void setMapsFragment(MapsFragment mapsFragment){ this.mapsFragment = mapsFragment; }

    private void setSpinner(){
        spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, guList);
        spinner.setAdapter(spinnerAdapter);
    }

    private void initRecyclerViewData(){
        Log.d(TAG, "initRecyclerViewData");
        title.clear();
        address.clear();
        guList.clear();
        selectedrowList = rowList;

        contentLength = rowList.size();
        for(int i=0; i<contentLength; i++){
            Row row = rowList.get(i);
            title.add(row.getNAME_KOR());
            address.add(row.getH_KOR_GU() + " " + row.getH_KOR_DONG());
            if(!guList.contains(row.getH_KOR_GU())){
                guList.add(row.getH_KOR_GU());
            }
        }
        Collections.sort(guList);
        guList.add(0, "전체");
        sortDatas();
    }

    private void setRecyclerViewData(String selectedGu){
        Log.d(TAG, "setRecyclerViewData");
        Log.d(TAG, selectedGu);

        title = new ArrayList<String>();
        address = new ArrayList<String>();
        selectedrowList = new ArrayList<Row>();

        for(int idx=0; idx<rowList.size(); idx++){
            Row row = rowList.get(idx);
            String rowGu = row.getH_KOR_GU();
            if(rowGu.contains(selectedGu)){
                String rowAddress = rowGu + " " + row.getH_KOR_DONG();
                String rowTitle = row.getNAME_KOR();
                title.add(rowTitle);
                address.add(rowAddress);
                selectedrowList.add(row);
            }
        }
        contentLength = title.size();
        sortDatas();
    }

    private void sortDatas(){
        Collections.sort(title);
        Collections.sort(address);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getContext(),String.valueOf(i + "," + l),Toast.LENGTH_LONG).show();
        // parent.getItemAtPosition(pos)
        Log.d(TAG, "onItemSelected");

        if(i == 0){
            initRecyclerViewData();
        }
        else{
            String selectedGu = guList.get(i);
            setRecyclerViewData(selectedGu);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView address;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.mustvisitlist, parent, false));
            Log.d(TAG, "ViewHolder");

            title = (TextView) itemView.findViewById(R.id.title);
            address = (TextView) itemView.findViewById(R.id.address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getAdapterPosition();
//                    Log.d("position", new Integer(curPosition).toString());
                    final LatLng currentPosition = mapsFragment.getCurrentPosition();

                    if(currentPosition == null){
                        Toast.makeText(getContext(), "아직 현재 위치가 조회되지 않았습니다.",Toast.LENGTH_LONG).show();
                        return;
                    }

                    final LatLng destPosition = new LatLng(Double.valueOf(selectedrowList.get(itemPosition).getWGS84_Y()),Double.valueOf(selectedrowList.get(itemPosition).getWGS84_X()));

                    // Getting URL to the Google Directions API

                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://daummaps://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=PUBLICTRANSIT"));
                    startActivity(intent);

//                    String serverKey = "AIzaSyBBSjp3OiJDhTYmumjDZGFcRb-Lg0Lv30k";
//                    GoogleDirection.withServerKey(serverKey)
//                            .from(currentPosition)
//                            .to(destPosition)
//                            .language(Language.KOREAN)
//                            .unit(Unit.METRIC)
//                            .avoid(AvoidType.FERRIES)
//                            .avoid(AvoidType.HIGHWAYS)
//                            .transitMode(TransitMode.BUS)
//                            .transportMode(TransportMode.TRANSIT)
//                            .alternativeRoute(true)
//                            .execute(new DirectionCallback() {
//                                @Override
//                                public void onDirectionSuccess(Direction direction, String rawBody) {
//                                    // Do something here
//                                    String status = direction.getStatus();
//
//                                    Log.d(TAG, status);
//                                    Log.d(TAG, rawBody);
//                                    if (direction.isOK()) {
//                                        List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
//                                        ArrayList<LatLng> sectionPositionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
//                                        for(int i = 0; i<sectionPositionList.size(); i++){
//                                            LatLng position = sectionPositionList.get(i);
//                                            Step step;
//                                            if(i < stepList.size()) {
//                                                step = stepList.get(i);
//                                                System.out.println(i + " : " + step.getTravelMode());
//                                                if (step.getTravelMode().equals("WALKING")) {
//                                                    MarkerOptions marker = new MarkerOptions();
//                                                    marker.position(position);
//                                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp));
//                                                    marker.visible(true);
//                                                    marker.alpha(0.7f);
//                                                    marker.zIndex(999);
//                                                    googleMap.addMarker(marker);
//                                                } else if (step.getTravelMode().equals("TRANSIT")) {
//                                                    MarkerOptions marker = new MarkerOptions().position(position);
//                                                    marker.position(position);
//                                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp));
//                                                    marker.visible(true);
//                                                    marker.alpha(0.7f);
//                                                    marker.zIndex(999);
//                                                    googleMap.addMarker(marker);
//                                                }
//                                            }
//                                        }
//
//                                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getContext(), stepList, 3, Color.RED, 3, Color.BLUE);
//                                        for (PolylineOptions polylineOption : polylineOptionList) {
//                                            googleMap.addPolyline(polylineOption);
//                                        }
//
//                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                                        fragmentTransaction.hide(MustVisitListFragment.this);
//                                        fragmentTransaction.show(mapsFragment);
//                                        fragmentTransaction.commit();
//                                    }
//                                }
//
//                                @Override
//                                public void onDirectionFailure(Throwable t) {
//                                    // Do something here
//                                    Log.d(TAG, t.getMessage());
//                                }
//                            });
//
                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        public ContentAdapter() {
            Log.d(TAG, "ContentAdapter");
            title = new ArrayList<String>();
            address = new ArrayList<String>();
            guList = new ArrayList<String>();
            initRecyclerViewData();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder");
            holder.title.setText(title.get(position % title.size()));
            holder.address.setText(address.get(position % address.size()));
        }

        @Override
        public int getItemCount() {
            return contentLength;
        }
    }
}
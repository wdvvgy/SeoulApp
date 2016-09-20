package sirius.seoulapp.map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sirius.seoulapp.seouldata.Results;
import sirius.seoulapp.seouldata.Row;

/**
 * Created by SIRIUS on 2016-09-06.
 */
public class CalculatePositionService extends Service {

    private final String TAG = getClass().getName();
    private ArrayList<Row> rowList;
    private ArrayList<Row> nearByMarker;
    private LatLng currentPosition;
    private String currentAddress;
    private String currentGu = new String();
    private String currentDong = new String();
    private boolean isSearchedGu;
    private boolean isSearchedDong;
    private CalculateDistance calculateDistance;
    private MapsFragment mapsFragment;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            mapsFragment = (MapsFragment) intent.getSerializableExtra("mapsFragment");
            rowList = (ArrayList<Row>) intent.getSerializableExtra("rowList");
            currentPosition = (LatLng) intent.getParcelableExtra("currentPosition");
        }
        if (rowList == null || currentPosition == null) {
            Log.d(TAG, "rowList or currentPosition is null");
            return START_STICKY;
        }
        calculateDistance = CalculateDistance.getInstance();
        calculateDistance.setCurrentPosition(currentPosition);

        String latlng = String.valueOf(currentPosition.latitude) + "," + String.valueOf(currentPosition.longitude);

        final Call<Results> call = request.getJSON(latlng, "ko");
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                currentAddress = response.body().getResults().get(0).getFormatted_address();
                Log.d(TAG, currentAddress);
                isSearchedGu = false;
                isSearchedDong = false;
                Message message = mHandler.obtainMessage();
                String[] TokenAddress = currentAddress.split(" ");
                for (int i = 0; i < TokenAddress.length; i++) {
                    if (TokenAddress[i].charAt(TokenAddress[i].length() - 1) == '구') {
                        currentGu = TokenAddress[i];
                        isSearchedGu = true;
                        sendMessageToHandler(message, 1, currentGu);
                        return;
                    }
                }
                for (int i = 0; i < TokenAddress.length; i++) {
                    if (TokenAddress[i].charAt(TokenAddress[i].length() - 1) == '동') {
                        currentDong = TokenAddress[i];
                        isSearchedDong = true;
                        sendMessageToHandler(message, 2, currentDong);
                        return;
                    }
                }
                sendMessageToHandler(message, 0, null);
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
        return START_STICKY;
    }

    private void getNearbyMarkerbyGu() {
        nearByMarker = new ArrayList<Row>();
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            if (row.getH_KOR_GU().equals(currentGu)) {
                nearByMarker.add(row);
            }
        }
        calculateDistance.setRowList(nearByMarker);
    }

    private void getNearbyMarkerbyDong() {
        nearByMarker = new ArrayList<Row>();
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            if (row.getH_KOR_DONG().equals(currentDong)) {
                nearByMarker.add(row);
            }
        }
        calculateDistance.setRowList(nearByMarker);
    }

    private void sendMessageToHandler(Message message, int what, Object obj) {
        message.what = what;
        message.obj = (String) obj;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "주소를 찾을 수 없습니다. GPS 에러", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Log.d(TAG, String.valueOf(msg.obj));
                    getNearbyMarkerbyGu();
                    calculateDistance.calculate();
                    break;
                case 2:
                    Log.d(TAG, String.valueOf(msg.obj));
                    getNearbyMarkerbyDong();
                    calculateDistance.calculate();
                    break;
                default:
                    Log.d(TAG, "handleMessage -> (msg.what) default value");
                    break;
            }

            if (calculateDistance.getisCalculatedDistance()) {
                ArrayList<Row> insideMarker = new ArrayList<Row>();
                for (int i = 0; i < calculateDistance.getCalculatedDistances().size(); i++) {
                    double meter = calculateDistance.getCalculatedDistances().get(i);
                    Log.d("distances", String.valueOf(meter));
                    if (meter <= 5000)
                        insideMarker.add(nearByMarker.get(i));

                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(AutoSearchingReceiver.mBroadcastStringAction);
                broadcastIntent.putExtra("detect", calculateDistance.getCalculatedDistances());
                broadcastIntent.putExtra("insideMarker", insideMarker);
                sendBroadcast(broadcastIntent);
            }
        }
    };

    RequestInterfaceGeocode request = RequestInterfaceGeocode.retrofit.create(RequestInterfaceGeocode.class);
    interface  RequestInterfaceGeocode{
        @GET("json")
        Call<Results> getJSON(
                @Query("latlng") String latlng,
                @Query("language") String language
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

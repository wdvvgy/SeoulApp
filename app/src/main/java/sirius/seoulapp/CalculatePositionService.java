package sirius.seoulapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SIRIUS on 2016-09-06.
 */
public class CalculatePositionService extends IntentService {

    private final String TAG = getClass().getName();
    private ArrayList<Row> rowList;
    private LatLng currentPosition;
    private String currentAddress;

    public CalculatePositionService(){
        super("CalculatePositionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null){
            rowList = (ArrayList<Row>) intent.getSerializableExtra("rowList");
            currentPosition = intent.getParcelableExtra("currentPosition");
        }
        if(rowList == null || currentPosition == null){
            Log.d(TAG, "rowList or currentPosition is null");
            return;
        }

        String latlng = String.valueOf(currentPosition.latitude) + "," + String.valueOf(currentPosition.longitude);
        Log.d(TAG, latlng);
        RequestInterface request = RequestInterface.retrofit.create(RequestInterface.class);
        final Call<Results> call = request.getJSON(latlng, "ko");
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
//                rowList = response.body().getSebcTourStreetKor().getRowList();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("rowList", rowList);
//                mapsFragment.setArguments(bundle);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();

                currentAddress = response.body().getResults().get(0).getFormatted_address();
                Log.d("currentAddress",currentAddress);
                String currentGu = new String();
                String[] TokenAddress = currentAddress.split(" ");
                for(int i=0; i<TokenAddress.length; i++){
                    if(TokenAddress[i].charAt(TokenAddress[i].length()-1) == '구'){
                        currentGu = TokenAddress[i];
                    }
                }

                if(currentGu != null){
                    Log.d("currentGu", currentGu);
                }
            }
            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    interface RequestInterface {
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

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopSelf();
    }
}

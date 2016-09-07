package sirius.seoulapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by SIRIUS on 2016-09-02.
 */
public class LoadingPlacesFragment extends Fragment {
    private final String TAG = "LoadingPlacesFragment";
    private MapsFragment mapsFragment;

    private ArrayList<Row> rowList;

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);

        RequestInterface request = RequestInterface.retrofit.create(RequestInterface.class);
        final Call<SebcTourStreetKor> call =
                request.getJSON("6641526f797764763334694f746c71", "json", "SebcTourStreetKor", 1, 134);

        call.enqueue(new Callback<SebcTourStreetKor>() {
            @Override
            public void onResponse(Call<SebcTourStreetKor> call, Response<SebcTourStreetKor> response) {
                rowList = response.body().getSebcTourStreetKor().getRowList();
                Bundle bundle = new Bundle();
                bundle.putSerializable("rowList", rowList);
                mapsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
            }

            @Override
            public void onFailure(Call<SebcTourStreetKor> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle = getArguments();
        mapsFragment = (MapsFragment) bundle.getSerializable("mapsFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.loadingplaces_fragment, container, false);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    interface RequestInterface {
        @GET("{key}/{format}/{name}/{startNum}/{endNum}")
        Call<SebcTourStreetKor> getJSON(
                @Path("key") String key,
                @Path("format") String format,
                @Path("name") String name,
                @Path("startNum") int startNum,
                @Path("endNum") int endNum
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.seoul.go.kr:8088/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

package sirius.seoulapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ParsingdataFragment extends Fragment {

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.textView)
    TextView textView;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parsingdata_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestInterface request = RequestInterface.retrofit.create(RequestInterface.class);
                final Call<SebcTourStreetKor> call =
                        request.getJSON("6641526f797764763334694f746c71", "json", "SebcTourStreetKor", 1, 5);

                call.enqueue(new Callback<SebcTourStreetKor>() {
                    @Override
                    public void onResponse(Call<SebcTourStreetKor> call, Response<SebcTourStreetKor> response) {
                        ArrayList<Row> rowList = response.body().getSebcTourStreetKor().getRowList();
                        String str = "";
                        for(int i=0; i<rowList.size(); i++){
                            Row row = rowList.get(i);
                            str += row;
                        }
                        textView.setText(str);
                    }
                    @Override
                    public void onFailure(Call<SebcTourStreetKor> call, Throwable t) {
                        textView.setText("Something went wrong: " + t.getMessage());
                    }
                });
            }
        });
        return view;
    }

    public class SebcTourStreetKor {

        @SerializedName("SebcTourStreetKor")
        @Expose
        private Sebc SebcTourStreetKor;

        public Sebc getSebcTourStreetKor(){ return SebcTourStreetKor; }

    }

    public class Sebc{
        @SerializedName("list_total_count")
        @Expose
        private int list_total_count;

        @SerializedName("RESULT")
        @Expose
        private Result RESULT;

        @SerializedName("row")
        @Expose
        private ArrayList<Row> row;
        public ArrayList<Row> getRowList(){ return row; }

        public String toString(){
            return new Integer(list_total_count).toString();
        }
    }

    public class Result{

        @SerializedName("CODE")
        @Expose
        private String CODE;

        @SerializedName("MESSAGE")
        @Expose
        private String MESSAGE;
    }

    public class Row {
        @SerializedName("MAIN_KEY")
        @Expose
        private String MAIN_KEY;

        @SerializedName("NM_DP")
        @Expose
        private String NM_DP;

        @SerializedName("WGS84_X")
        @Expose
        private String WGS84_X;

        @SerializedName("WGS84_Y")
        @Expose
        private String WGS84_Y;

        public String toString(){ return MAIN_KEY + "," + NM_DP + "," + WGS84_X + "," + WGS84_Y; }

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

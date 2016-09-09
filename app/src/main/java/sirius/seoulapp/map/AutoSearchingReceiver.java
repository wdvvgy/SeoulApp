package sirius.seoulapp.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import sirius.seoulapp.seouldata.Row;

/**
 * Created by SIRIUS on 2016-09-08.
 */
public class AutoSearchingReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getName();
    public static final String mBroadcastStringAction = "sirius.seoulapp.broadcast.AutoSearching";
    private ArrayList<Row> insideMarker;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(mBroadcastStringAction)){
            insideMarker = (ArrayList<Row>) intent.getSerializableExtra("insideMarker");
            for(int i=0; i<insideMarker.size(); i++){
                Log.d(TAG, insideMarker.get(i).toString());
            }
        }
    }
}
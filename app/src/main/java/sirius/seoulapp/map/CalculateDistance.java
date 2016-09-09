package sirius.seoulapp.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import sirius.seoulapp.seouldata.Row;

/**
 * Created by SIRIUS on 2016-09-07.
 */
public class CalculateDistance {
    private final String TAG = getClass().getName();
    private static CalculateDistance instance = null;
    public synchronized static CalculateDistance getInstance(){
        if(instance == null){
            instance = new CalculateDistance();
        }
        return instance;
    }
    private CalculateDistance(){ }

    private ArrayList<Row> rowList;
    public void setRowList(ArrayList<Row> rowList){ this.rowList = rowList; }

    private LatLng currentPosition;
    public void setCurrentPosition(LatLng currentPosition){ this.currentPosition = currentPosition; }

    private ArrayList<Double> calculatedDistances;
    public ArrayList<Double> getCalculatedDistances(){ return calculatedDistances; }

    private boolean isCalculatedDistances;
    public boolean getisCalculatedDistance(){ return isCalculatedDistances; }

    public void calculate(){
        calculatedDistances = new ArrayList<Double>();
        isCalculatedDistances = false;

        double theta = 0, dist = 0;
        for(int i=0; i<rowList.size(); i++) {
            Row row = rowList.get(i);
            double rowX = Double.valueOf(row.getWGS84_X());
            double rowY = Double.valueOf(row.getWGS84_Y());

            theta = currentPosition.longitude - rowX;

            dist = Math.sin(deg2rad(currentPosition.latitude)) * Math.sin(deg2rad(rowY)) + Math.cos(deg2rad(currentPosition.latitude)) * Math.cos(deg2rad(rowY)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1609.344;
            calculatedDistances.add(dist);
        }

        isCalculatedDistances = true;
    }

    private double deg2rad(double deg){
        return (double) (deg * Math.PI / (double)180d);
    }

    private double rad2deg(double rad){
        return (double) (rad * (double)180d / Math.PI);
    }

}

package sirius.seoulapp.seouldata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SIRIUS on 2016-09-03.
 */
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
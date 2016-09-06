package sirius.seoulapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SIRIUS on 2016-09-06.
 */
public class Results {
    @SerializedName("results")
    @Expose
    private ArrayList<res> results;

    public ArrayList<res> getResults() {
        return results;
    }
}
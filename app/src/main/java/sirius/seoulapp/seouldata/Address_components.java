package sirius.seoulapp.seouldata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by SIRIUS on 2016-09-06.
 */
public class Address_components {
    @SerializedName("long_name")
    @Expose
    private String long_name;

    public String getLong_name() {
        return long_name;
    }

    @SerializedName("short_name")
    @Expose
    private String short_name;

    public String getShort_name() {
        return short_name;
    }

}

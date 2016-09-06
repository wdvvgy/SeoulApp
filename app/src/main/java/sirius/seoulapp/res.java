package sirius.seoulapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SIRIUS on 2016-09-06.
 */
public class res {

    @SerializedName("address_components")
    @Expose
    private ArrayList<Address_components> address_components;

    public ArrayList<Address_components> getAddress_components() {
        return address_components;
    }

    @SerializedName("formatted_address")
    @Expose
    private String formatted_address;

    public String getFormatted_address() {
        return formatted_address;
    }


//    @SerializedName("geometry")
//    @Expose
//    private Geometry geometry;
//
//    public Geometry getGeometry() {
//        return geometry;
//    }
//
//    @SerializedName("place_id")
//    @Expose
//    private String place_id;
//
//    public String getPlace_id() {
//        return place_id;
//    }
//
//    @SerializedName("types")
//    @Expose
//    private Types types;
//
//    public Types getTypes() {
//        return types;
//    }
}
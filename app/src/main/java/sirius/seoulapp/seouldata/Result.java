package sirius.seoulapp.seouldata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by SIRIUS on 2016-09-03.
 */
public class Result{

    @SerializedName("CODE")
    @Expose
    private String CODE;

    @SerializedName("MESSAGE")
    @Expose
    private String MESSAGE;
}
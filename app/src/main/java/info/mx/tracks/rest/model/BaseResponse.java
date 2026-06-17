package info.mx.tracks.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("id")
    @Expose
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
package ir.ncompany.lastnews.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rub-naghibi on 11/9/2016.
 */

public class RetroGetBBCNews {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public ArrayList<RetroGetBBCNewsData> getRetroGetBBCNewsDatas() {
        return retroGetBBCNewsDatas;
    }

    public void setRetroGetBBCNewsDatas(ArrayList<RetroGetBBCNewsData> retroGetBBCNewsDatas) {
        this.retroGetBBCNewsDatas = retroGetBBCNewsDatas;
    }

    @SerializedName("status")
    private String status;
    @SerializedName("source")
    private String source;
    @SerializedName("sortBy")
    private String sortBy;
    @SerializedName("articles")
    private ArrayList<RetroGetBBCNewsData> retroGetBBCNewsDatas;

}

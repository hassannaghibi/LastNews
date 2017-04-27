package ir.ncompany.lastnews.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by rub-naghibi on 11/9/2016.
 */

public interface RetroBaseApi {
    @GET("v1/articles?source=bbc-news&sortBy=top&apiKey=d2f218bf8c4e4e4892c1a6feb2102ac1")
    Call<RetroGetBBCNews> bbcNews();
    @GET("v1/articles?source=bbc-sport&sortBy=top&apiKey=d2f218bf8c4e4e4892c1a6feb2102ac1")
    Call<RetroGetBBCNews> bbcSport();

}
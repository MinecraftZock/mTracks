package info.mx.tracks.rest;

import java.util.List;

import info.mx.tracks.room.entity.Comment;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IAppTrackService {

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/ratings/track/{trackId}")
    Single<List<Comment>> getRatingsForTrack(
            @Path(value = "trackId") long trackId,
            @Header("Authorization") String authorization);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("/backend/MXServer/rest/ratings/createComment")
    Single<Comment> postRating(
            @Body Comment comment,
            @Header("Authorization") String authorization);

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/weather/forTrack/{trackid}/{unit}/{lang}")
    Single<String> getWeather4TrackSync(
            @Path(value = "trackid") long trackid,
            @Path(value = "unit") String unit,
            @Path(value = "lang") String lang,
            @Header("Authorization") String authorization);
}

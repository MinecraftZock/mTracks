package info.mx.tracks.tracklist;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ViewHolderTracksDist {

    private TextView tr_list_event;
    private TextView tr_name;
    private TextView tr_distance;
    private RatingBar tr_ratingBar;
    private TextView tr_mo;
    private TextView tr_tu;
    private TextView tr_we;
    private TextView tr_th;
    private TextView tr_fr;
    private TextView tr_sa;
    private TextView tr_so;
    private ImageView tr_country;
    private ImageView tr_track_access;
    private ImageView tr_camera;
    private ImageView tr_calendar;

    public ViewHolderTracksDist(TextView tr_list_event, TextView tr_name, TextView tr_distance, RatingBar tr_ratingBar, TextView tr_mo,
                                TextView tr_tu, TextView tr_we, TextView tr_th, TextView tr_fr, TextView tr_sa, TextView tr_so, ImageView tr_country,
                                ImageView tr_track_access, ImageView tr_camera, ImageView tr_calendar) {
        this.setTr_list_event(tr_list_event);
        this.setTr_name(tr_name);
        this.setTr_distance(tr_distance);
        this.setTr_ratingBar(tr_ratingBar);
        this.setTr_mo(tr_mo);
        this.setTr_tu(tr_tu);
        this.setTr_we(tr_we);
        this.setTr_th(tr_th);
        this.setTr_fr(tr_fr);
        this.setTr_sa(tr_sa);
        this.setTr_so(tr_so);
        this.setTr_country(tr_country);
        this.setTr_track_access(tr_track_access);
        this.setTr_camera(tr_camera);
        this.setTr_calendar(tr_calendar);
    }

    public TextView getTr_list_event() {
        return tr_list_event;
    }

    public void setTr_list_event(TextView tr_list_event) {
        this.tr_list_event = tr_list_event;
    }

    public TextView getTr_name() {
        return tr_name;
    }

    public void setTr_name(TextView tr_name) {
        this.tr_name = tr_name;
    }

    public TextView getTr_distance() {
        return tr_distance;
    }

    public void setTr_distance(TextView tr_distance) {
        this.tr_distance = tr_distance;
    }

    public RatingBar getTr_ratingBar() {
        return tr_ratingBar;
    }

    public void setTr_ratingBar(RatingBar tr_ratingBar) {
        this.tr_ratingBar = tr_ratingBar;
    }

    public TextView getTr_mo() {
        return tr_mo;
    }

    public void setTr_mo(TextView tr_mo) {
        this.tr_mo = tr_mo;
    }

    public TextView getTr_tu() {
        return tr_tu;
    }

    public void setTr_tu(TextView tr_tu) {
        this.tr_tu = tr_tu;
    }

    public TextView getTr_we() {
        return tr_we;
    }

    public void setTr_we(TextView tr_we) {
        this.tr_we = tr_we;
    }

    public TextView getTr_th() {
        return tr_th;
    }

    public void setTr_th(TextView tr_th) {
        this.tr_th = tr_th;
    }

    public TextView getTr_fr() {
        return tr_fr;
    }

    public void setTr_fr(TextView tr_fr) {
        this.tr_fr = tr_fr;
    }

    public TextView getTr_sa() {
        return tr_sa;
    }

    public void setTr_sa(TextView tr_sa) {
        this.tr_sa = tr_sa;
    }

    public TextView getTr_so() {
        return tr_so;
    }

    public void setTr_so(TextView tr_so) {
        this.tr_so = tr_so;
    }

    public ImageView getTr_country() {
        return tr_country;
    }

    public void setTr_country(ImageView tr_country) {
        this.tr_country = tr_country;
    }

    public ImageView getTr_track_access() {
        return tr_track_access;
    }

    public void setTr_track_access(ImageView tr_track_access) {
        this.tr_track_access = tr_track_access;
    }

    public ImageView getTr_camera() {
        return tr_camera;
    }

    public void setTr_camera(ImageView tr_camera) {
        this.tr_camera = tr_camera;
    }

    public ImageView getTr_calendar() {
        return tr_calendar;
    }

    public void setTr_calendar(ImageView tr_calendar) {
        this.tr_calendar = tr_calendar;
    }

}

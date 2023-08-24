
package info.mx.tracks.rest.google;

import com.google.gson.annotations.Expose;

public class OverviewPolyline {

    @Expose
    private String points;

    /**
     * @return The points
     */
    public String getPoints() {
        return points;
    }

    /**
     * @param points The points
     */
    public void setPoints(String points) {
        this.points = points;
    }

}

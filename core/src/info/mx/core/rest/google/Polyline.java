
package info.mx.core.rest.google;

import com.google.gson.annotations.Expose;

public class Polyline {

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


package info.mx.core.rest.google;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @Expose
    private Bounds bounds;
    @Expose
    private String copyrights;
    @Expose
    private List<Leg> legs = new ArrayList<Leg>();
    @SerializedName("overview_polyline")
    @Expose
    private OverviewPolyline overviewPolyline;
    @Expose
    private String summary;
    @Expose
    private List<Object> warnings = new ArrayList<Object>();
    @SerializedName("waypoint_order")
    @Expose
    private List<Object> waypointOrder = new ArrayList<Object>();

    /**
     * @return The bounds
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * @param bounds The bounds
     */
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    /**
     * @return The copyrights
     */
    public String getCopyrights() {
        return copyrights;
    }

    /**
     * @param copyrights The copyrights
     */
    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    /**
     * @return The legs
     */
    public List<Leg> getLegs() {
        return legs;
    }

    /**
     * @param legs The legs
     */
    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    /**
     * @return The overviewPolyline
     */
    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    /**
     * @param overviewPolyline The overview_polyline
     */
    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    /**
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return The warnings
     */
    public List<Object> getWarnings() {
        return warnings;
    }

    /**
     * @param warnings The warnings
     */
    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    /**
     * @return The waypointOrder
     */
    public List<Object> getWaypointOrder() {
        return waypointOrder;
    }

    /**
     * @param waypointOrder The waypoint_order
     */
    public void setWaypointOrder(List<Object> waypointOrder) {
        this.waypointOrder = waypointOrder;
    }

}

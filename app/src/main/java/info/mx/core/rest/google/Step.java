
package info.mx.core.rest.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @Expose
    private Distance_ distance;
    @Expose
    private Duration_ duration;
    @SerializedName("end_location")
    @Expose
    private EndLocation_ endLocation;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @Expose
    private Polyline polyline;
    @SerializedName("start_location")
    @Expose
    private StartLocation_ startLocation;
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @Expose
    private String maneuver;

    /**
     * @return The distance
     */
    public Distance_ getDistance() {
        return distance;
    }

    /**
     * @param distance The distance
     */
    public void setDistance(Distance_ distance) {
        this.distance = distance;
    }

    /**
     * @return The duration
     */
    public Duration_ getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(Duration_ duration) {
        this.duration = duration;
    }

    /**
     * @return The endLocation
     */
    public EndLocation_ getEndLocation() {
        return endLocation;
    }

    /**
     * @param endLocation The end_location
     */
    public void setEndLocation(EndLocation_ endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * @return The htmlInstructions
     */
    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    /**
     * @param htmlInstructions The html_instructions
     */
    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    /**
     * @return The polyline
     */
    public Polyline getPolyline() {
        return polyline;
    }

    /**
     * @param polyline The polyline
     */
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    /**
     * @return The startLocation
     */
    public StartLocation_ getStartLocation() {
        return startLocation;
    }

    /**
     * @param startLocation The start_location
     */
    public void setStartLocation(StartLocation_ startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * @return The travelMode
     */
    public String getTravelMode() {
        return travelMode;
    }

    /**
     * @param travelMode The travel_mode
     */
    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    /**
     * @return The maneuver
     */
    public String getManeuver() {
        return maneuver;
    }

    /**
     * @param maneuver The maneuver
     */
    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

}

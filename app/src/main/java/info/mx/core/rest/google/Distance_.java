
package info.mx.core.rest.google;

import com.google.gson.annotations.Expose;

public class Distance_ {

    @Expose
    private String text;
    @Expose
    private Integer value;

    /**
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(Integer value) {
        this.value = value;
    }

}

package obj.quickblox.sample.chat.java.ui.Model;

public class Location_Venues
{
    private String name;
    private String lat;
    private String lng;
    private String distance;


    public Location_Venues(String name, String lat, String lng, String distance) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}

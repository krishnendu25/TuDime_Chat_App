package obj.quickblox.sample.chat.java.util;


import java.util.ArrayList;

public class FourSquareNearbyPlaceResponseModel {

    private Meta meta;
    private Response response;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Meta {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
    public class Response {
        private ArrayList<Venues> venues;

        public ArrayList<Venues> getVenues() {
            return venues;
        }

        public void setVenues(ArrayList<Venues> venues) {
            this.venues = venues;
        }

    }

    public class Venues {
        private String name;
        private LocationModel location;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public LocationModel getLocation() {
            return location;
        }
        public void setLocation(LocationModel location) {
            this.location = location;
        }

    }

    public class LocationModel {
        private double lat;
        private double lng;
        private String distance;
        public double getLat() {
            return lat;
        }
        public void setLat(double lat) {
            this.lat = lat;
        }
        public double getLng() {
            return lng;
        }
        public void setLng(double lng) {
            this.lng = lng;
        }
        public String getDistance() {
            return distance;
        }
        public void setDistance(String distance) {
            this.distance = distance;
        }
    }
}

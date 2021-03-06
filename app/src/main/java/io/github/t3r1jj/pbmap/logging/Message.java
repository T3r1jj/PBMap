package io.github.t3r1jj.pbmap.logging;

import org.json.JSONObject;

import java.io.Serializable;

import io.github.t3r1jj.pbmap.model.map.Coordinate;
import io.github.t3r1jj.pbmap.model.map.Place;

public class Message implements Serializable {
    private String id;
    private String closestPlace;
    private Coordinate closestPlaceCoordinate;
    private String description;
    private long epochMs;
    private double ddPlace;
    private double ddRoute;
    private final String map;
    private final Coordinate coordinate;
    private final Coordinate closestRouteCoordinate;

    public Message(String map, Coordinate coordinate, Coordinate closestRouteCoordinate, Place closestPlace) {
        this.map = map;
        this.coordinate = coordinate;
        this.closestRouteCoordinate = closestRouteCoordinate;
        if (closestPlace != null) {
            this.closestPlace = closestPlace.getId();
            this.closestPlaceCoordinate = closestPlace.getCoordinates().get(0);
        }
        if (coordinate != null) {
            if (closestPlaceCoordinate != null) {
                ddPlace = closestPlaceCoordinate.distanceTo(coordinate);
            }
            if (closestRouteCoordinate != null) {
                ddRoute = closestRouteCoordinate.distanceTo(coordinate);
            }
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void setId(String id) {
        this.id = id;
    }

    void setEpochMs(long epochMs) {
        this.epochMs = epochMs;
    }

    String toJson() {
        return "{" +
                "\"id\":\"" + id + "\"" +
                ",\"epochMs\":" + epochMs +
                ",\"map\":\"" + map + "\"" +
                ",\"coordinate\":" + jsonStringify(coordinate) +
                ",\"closestRouteCoordinate\":" + jsonStringify(closestRouteCoordinate) +
                ",\"closestPlace\":" + "{" +
                "\"id\":\" " + closestPlace + "\"" +
                ", \"coordinate\": " + jsonStringify(closestPlaceCoordinate) +
                "}" +
                ",\"description\":" + encodeText(description) +
                ",\"ddRoute\":" + ddRoute +
                ",\"ddPlace\":" + ddPlace +
                ",\"code\":\"" + (coordinate == null ? "null" : coordinate.toString().replace("\"", "\\\"")) + "\"" +
                '}';
    }

    private String encodeText(String text) {
        if (text == null) {
            return "\"null\"";
        }
        return JSONObject.quote(text);
    }

    private String jsonStringify(Coordinate coordinate) {
        return "{" +
                "\"lat\": " + (coordinate == null ? 0 : coordinate.lat) +
                ", \"lng\": " + (coordinate == null ? 0 : coordinate.lng) +
                ", \"alt\": " + (coordinate == null ? 0 : coordinate.alt) +
                "}";
    }

}

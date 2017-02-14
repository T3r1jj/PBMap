package io.github.t3r1jj.pbmap;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import io.github.t3r1jj.pbmap.model.Coordinate;
import io.github.t3r1jj.pbmap.model.PBMap;
import io.github.t3r1jj.pbmap.model.Place;
import io.github.t3r1jj.pbmap.model.Space;
import io.github.t3r1jj.pbmap.search.SearchSuggestion;
import io.github.t3r1jj.pbmap.view.MapView;

public class Controller {
    private PBMap map;
    private MapView mapView;
    private final MapActivity mapActivity;
    private LinkedList<String> road = new LinkedList<>();

    Controller(MapActivity base) {
        this.mapActivity = base;
    }

    void loadMap(SearchSuggestion suggestion) throws Exception {
        loadNewMap(suggestion.mapPath);
        pinpointPlace(suggestion.place);
    }

    void loadMap(String assetsMapPath) throws Exception {
        loadNewMap(assetsMapPath);
        mapView.loadPreviousPosition();
    }

    void loadPreviousMap() throws Exception {
        Log.d(getClass().getSimpleName(), road.toString());
        road.removeLast();
        String mapPath = road.getLast();
        loadNewMap(mapPath);
    }

    private void loadNewMap(String assetsMapPath) throws Exception {
        Serializer serializer = new Persister();
        addPathToRoad(assetsMapPath);
        map = serializer.read(PBMap.class, mapActivity.getAssets().open(assetsMapPath));
        MapView nextMapView = map.createView(mapActivity);
        nextMapView.setController(this);
        mapActivity.setMapView(nextMapView);
        if (isInitialized()) {
            mapView.addToMap(nextMapView);
        }
        mapView = nextMapView;
        mapActivity.setBackButtonVisible(road.size() > 1);
    }

    private void addPathToRoad(String newPath) {
        if (road.isEmpty() || !newPath.equals(road.getLast())) {
            road.add(newPath);
        }
    }

    private void pinpointPlace(final String placeName) {
        for (Place place : map.getPlaces()) {
            if (place.getName().equals(placeName)) {
                final Coordinate center = place.getCenter();
                mapView.post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.setScale(1f);
                        mapView.scrollToAndCenter(center.lng, center.lat);
                        mapView.setScaleFromCenter(getPinpointScale());
                    }
                });
                return;
            }
        }
    }

    private float getPinpointScale() {
        return 1f;
    }

    public void onNavigationPerformed(Space space) {
        try {
            loadMap(space.getMapReference());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isInitialized() {
        return mapView != null;
    }

    public void loadLogo(Place map) {
        ImageView logo = null;
        try {
            InputStream inputStream = mapActivity.getAssets().open(map.getLogoPath());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            logo = new ImageView(mapActivity);
            logo.setImageDrawable(drawable);
        } catch (IllegalArgumentException | IOException e) {
            if (map.getLogoPath() != null) {
                e.printStackTrace();
            }
        }
        mapActivity.setLogo(logo);
    }

}

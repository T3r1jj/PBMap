package io.github.t3r1jj.pbmap.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.paths.CompositePathView;

import java.io.IOException;
import java.io.InputStream;

import io.github.t3r1jj.pbmap.model.Coordinate;
import io.github.t3r1jj.pbmap.model.Space;

public class SpaceView extends CompositePathView.DrawablePath implements PlaceView {

    private HotSpot hotSpot;
    private final Space space;
    private final SpotView spotView;
    private final Context context;

    public SpaceView(Context context, Space space) {
        this.space = space;
        this.context = context;
        path = new Path();
        paint = new Paint();
        Coordinate[] coordinates = space.getCoordinates();
        path.moveTo((float) coordinates[coordinates.length - 1].lng, (float) coordinates[coordinates.length - 1].lat);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.GRAY);
        double[] clip = new double[4];
        for (Coordinate coordinate : coordinates) {
            path.lineTo((float) coordinate.lng, (float) coordinate.lat);
            clip[0] = Math.min(coordinate.lng, clip[0]);
            clip[1] = Math.min(coordinate.lat, clip[1]);
            clip[2] = Math.max(coordinate.lng, clip[2]);
            clip[3] = Math.max(coordinate.lat, clip[3]);
        }
        path.lineTo((float) coordinates[0].lng, (float) coordinates[0].lat);
        spotView = new SpotView(context, space);

        if (space.getReferenceMapPath() != null) {
            prepareHotspot(clip);
        }
    }

    private void prepareHotspot(double[] clip) {
        Region region = new Region();
        region.setPath(path, new Region((int) (clip[0] + 0.5d), (int) (clip[1] + 0.5d), (int) (clip[2] + 0.5d), (int) (clip[3] + 0.5d)));
        hotSpot = new HotSpot();
        hotSpot.set(region);
    }

    @Override
    public void addToMap(final MapView pbMapView) {
        pbMapView.getCompositePathView().addPath(this);
        pbMapView.addPlaceView(spotView);
        if (space.getReferenceMapPath() != null) {

            hotSpot.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
                @Override
                public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                    pbMapView.fireNavigationPerformed(space);
                }
            });
            pbMapView.addHotSpot(hotSpot);
        }
        if (space.getLogoPath() != null) {
            try {
                InputStream inputStream = context.getAssets().open(space.getLogoPath());
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                ImageView logo = new ImageView(context);
                logo.setImageDrawable(drawable);
                Coordinate center = space.getCenter();
                pbMapView.addMarker(logo, center.lng, center.lat,  -0.5f, -1.0f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

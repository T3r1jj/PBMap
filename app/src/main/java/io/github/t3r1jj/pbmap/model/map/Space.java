package io.github.t3r1jj.pbmap.model.map;

import android.content.Context;

import org.simpleframework.xml.Attribute;

import io.github.t3r1jj.pbmap.view.PlaceView;
import io.github.t3r1jj.pbmap.view.SpaceView;

public class Space extends Place {
    @Attribute(name = "reference_map_path", required = false)
    protected String referenceMapPath;
    @Attribute(required = false)
    private String url;

    @Override
    public PlaceView createView(Context context) {
        return new SpaceView(context, this);
    }

    public String getReferenceMapPath() {
        return referenceMapPath;
    }

    public void setReferenceMapPath(String referenceMapPath) {
        this.referenceMapPath = referenceMapPath;
    }

    public String getUrl() {
        return url;
    }
}

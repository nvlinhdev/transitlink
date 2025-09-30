package vn.edu.fpt.transitlink.mapbox_integration;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;

import java.util.ArrayList;
import java.util.List;

public class PolylineMerger {
    private static final int PRECISION = 5;

    public static String mergePolylines(List<String> polylines) {
        List<Point> mergedPoints = new ArrayList<>();

        for (String polyline : polylines) {
            if (polyline == null || polyline.isEmpty()) continue;

            List<Point> points = PolylineUtils.decode(polyline, PRECISION);

            if (!mergedPoints.isEmpty() && !points.isEmpty()) {
                // Tránh trùng điểm đầu
                if (mergedPoints.get(mergedPoints.size() - 1).equals(points.get(0))) {
                    points = points.subList(1, points.size());
                }
            }
            mergedPoints.addAll(points);
        }

        if (mergedPoints.isEmpty()) {
            return "";
        }

        return PolylineUtils.encode(mergedPoints, PRECISION);
    }
}

package vn.edu.fpt.transitlink.mapbox_integration;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import java.util.ArrayList;
import java.util.List;

public class PolylineCutterByPoint {
    private static final int PRECISION = 5; // chỉnh nếu cần
    private static final double R = 6371000.0; // bán kính Trái Đất (m)

    private static List<Point> decode(String encoded) {
        return PolylineUtils.decode(encoded, PRECISION);
    }

    private static String encode(List<Point> points) {
        return PolylineUtils.encode(points, PRECISION);
    }

    // Haversine distance (m)
    private static double haversineDistance(Point a, Point b) {
        double lat1 = Math.toRadians(a.latitude());
        double lat2 = Math.toRadians(b.latitude());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.longitude() - a.longitude());
        double s1 = Math.sin(dLat / 2);
        double s2 = Math.sin(dLon / 2);
        double aa = s1 * s1 + Math.cos(lat1) * Math.cos(lat2) * s2 * s2;
        double c = 2 * Math.atan2(Math.sqrt(aa), Math.sqrt(1 - aa));
        return R * c;
    }

    // Chuyển Point -> (x,y) meters dùng equirectangular approx, centerLatRad lấy từ target
    private static double[] toXYMeters(Point p, double centerLatRad) {
        double x = R * Math.toRadians(p.longitude()) * Math.cos(centerLatRad);
        double y = R * Math.toRadians(p.latitude());
        return new double[]{x, y};
    }

    private static class Projection {
        int segIndex;
        double t; // 0..1
        Point projected;
        double distMeters;
    }

    // Tìm projection (segIndex, t, projectedPoint) nếu khoảng cách <= toleranceMeters
    private static Projection findProjection(List<Point> pts, Point target, double toleranceMeters) {
        double centerLatRad = Math.toRadians(target.latitude());
        double[] targetXY = toXYMeters(target, centerLatRad);

        for (int i = 0; i < pts.size() - 1; i++) {
            Point p1 = pts.get(i);
            Point p2 = pts.get(i + 1);
            double[] p1xy = toXYMeters(p1, centerLatRad);
            double[] p2xy = toXYMeters(p2, centerLatRad);

            double vx = p2xy[0] - p1xy[0];
            double vy = p2xy[1] - p1xy[1];
            double wx = targetXY[0] - p1xy[0];
            double wy = targetXY[1] - p1xy[1];

            double vlen2 = vx * vx + vy * vy;
            if (vlen2 == 0) continue; // đoạn dài bằng 0

            double t = (vx * wx + vy * wy) / vlen2;
            double tClamped = Math.max(0.0, Math.min(1.0, t));
            double projx = p1xy[0] + tClamped * vx;
            double projy = p1xy[1] + tClamped * vy;

            double dx = projx - targetXY[0];
            double dy = projy - targetXY[1];
            double dist = Math.sqrt(dx * dx + dy * dy); // meters

            if (dist <= toleranceMeters) {
                // tạo point nội suy lat/lon theo tClamped
                double lat = p1.latitude() + (p2.latitude() - p1.latitude()) * tClamped;
                double lon = p1.longitude() + (p2.longitude() - p1.longitude()) * tClamped;
                Projection proj = new Projection();
                proj.segIndex = i;
                proj.t = tClamped;
                proj.projected = Point.fromLngLat(lon, lat);
                proj.distMeters = dist;
                return proj;
            }
        }
        return null;
    }

    // Helper: small equality threshold (m) để tránh duplicate khi thêm điểm
    private static final double DUP_EPS_METERS = 0.01; // 1 cm

    // Public API với tolerance mặc định 5m
    public static String cutFromStartToPoint(String encoded, Point target) {
        return cutFromStartToPoint(encoded, target, 10.0);
    }

    public static String cutFromPointToEnd(String encoded, Point target) {
        return cutFromPointToEnd(encoded, target, 10.0);
    }

    public static String cutFromStartToPoint(String encoded, Point target, double toleranceMeters) {
        List<Point> pts = decode(encoded);
        if (pts.isEmpty()) throw new IllegalArgumentException("Polyline empty");

        Projection proj = findProjection(pts, target, toleranceMeters);
        if (proj == null) {
            throw new IllegalArgumentException("Target point not on polyline within tolerance " + toleranceMeters + " m");
        }

        List<Point> out = new ArrayList<>();
        out.add(pts.get(0));
        // thêm các vertex up to segIndex (sẽ thêm p1..p_segIndex)
        for (int i = 0; i < proj.segIndex; i++) {
            out.add(pts.get(i + 1));
        }
        // thêm điểm projection (nếu không duplicate)
        Point last = out.get(out.size() - 1);
        if (haversineDistance(last, proj.projected) > DUP_EPS_METERS) {
            out.add(proj.projected);
        }
        return encode(out);
    }

    public static String cutFromPointToEnd(String encoded, Point target, double toleranceMeters) {
        List<Point> pts = decode(encoded);
        if (pts.isEmpty()) throw new IllegalArgumentException("Polyline empty");

        Projection proj = findProjection(pts, target, toleranceMeters);
        if (proj == null) {
            throw new IllegalArgumentException("Target point not on polyline within tolerance " + toleranceMeters + " m");
        }

        List<Point> out = new ArrayList<>();
        out.add(proj.projected);
        // thêm phần còn lại từ segIndex+1 tới cuối
        for (int i = proj.segIndex + 1; i < pts.size(); i++) {
            Point p = pts.get(i);
            if (haversineDistance(out.get(out.size() - 1), p) > DUP_EPS_METERS) {
                out.add(p);
            }
        }
        return encode(out);
    }

    // 3) Cắt từ điểm A -> điểm B (cả 2 nằm trên polyline)
    public static String cutBetweenPoints(String encoded, Point start, Point end, double toleranceMeters) {
        List<Point> pts = decode(encoded);
        if (pts.isEmpty()) throw new IllegalArgumentException("Polyline empty");

        Projection projA = findProjection(pts, start, toleranceMeters);
        Projection projB = findProjection(pts, end, toleranceMeters);
        if (projA == null || projB == null) {
            throw new IllegalArgumentException("Start or End point not on polyline within tolerance " + toleranceMeters + " m");
        }

        // xác định thứ tự: nếu A nằm sau B thì đảo ngược
        int startSeg = projA.segIndex;
        int endSeg = projB.segIndex;
        boolean reversed = false;
        if (startSeg > endSeg || (startSeg == endSeg && projA.t > projB.t)) {
            Projection tmp = projA;
            projA = projB;
            projB = tmp;
            reversed = true;
        }

        List<Point> out = new ArrayList<>();
        out.add(projA.projected);

        // thêm các điểm trung gian giữa A và B
        for (int i = projA.segIndex + 1; i <= projB.segIndex; i++) {
            Point p = pts.get(i);
            if (haversineDistance(out.get(out.size() - 1), p) > DUP_EPS_METERS) {
                out.add(p);
            }
        }

        // thêm B
        Point last = out.get(out.size() - 1);
        if (haversineDistance(last, projB.projected) > DUP_EPS_METERS) {
            out.add(projB.projected);
        }

        // nếu thứ tự bị đảo thì reverse list
        if (reversed) {
            List<Point> reversedList = new ArrayList<>();
            for (int i = out.size() - 1; i >= 0; i--) {
                reversedList.add(out.get(i));
            }
            out = reversedList;
        }

        return encode(out);
    }

    // Overload mặc định tolerance 10m
    public static String cutBetweenPoints(String encoded, Point start, Point end) {
        return cutBetweenPoints(encoded, start, end, 10.0);
    }
}


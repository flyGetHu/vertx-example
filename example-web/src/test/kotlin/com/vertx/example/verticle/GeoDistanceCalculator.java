package com.vertx.example.verticle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoDistanceCalculator {


    private static final double EARTH_RADIUS_KM = 6371.0; // 地球半径，单位：千米
    private static final double EARTH_RADIUS = 6371000; // 地球平均半径，单位：米

    private static final double EARTH_RADIUS_MILES = 3958.8; // 地球半径，单位：英里

    // 将经纬度距离转换为米
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // 计算一公里在纬度上的差值（单位：度）
    private static double getLatitudeDegreeDifference(double radiusMeters) {
        // 一公里对应的地球表面弧长所跨的纬度（在赤道附近）
        return (180.0 / Math.PI) * (radiusMeters / EARTH_RADIUS);
    }

    // 计算一公里在经度上的差值（单位：度），这取决于纬度
    private static double getLongitudeDegreeDifference(double latitude, double radiusMeters) {
        // 一公里对应的地球表面弧长所跨的经度（考虑到纬度影响）
        double radLat = deg2rad(latitude);
        return (180.0 / Math.PI) * (radiusMeters / (EARTH_RADIUS * Math.cos(radLat)));
    }

    // 检查点是否在给定的经纬度范围内
    public static boolean isWithinRadius(double centerLat, double centerLon, double lat, double lon, double radiusMeters) {
        double latDiff = Math.abs(lat - centerLat);
        double lonDiff = Math.abs(lon - centerLon);
        double latDegreeDiff = getLatitudeDegreeDifference(radiusMeters);
        double lonDegreeDiff = getLongitudeDegreeDifference(centerLat, radiusMeters);
        return latDiff <= latDegreeDiff && lonDiff <= lonDegreeDiff;
    }

    /**
     * 使用Haversine公式计算两点之间的距离（单位：千米）
     *
     * @param geo1 第一个点的纬度
     * @param geo2 第一个点的经度
     * @return 两点之间的距离（千米）
     */
    public static double calculateDistanceInKilometers(GeoPoint geo1, GeoPoint geo2) {
        double lat1 = Math.toRadians(geo1.getLatitude());
        double lon1 = Math.toRadians(geo1.getLongitude());
        double lat2 = Math.toRadians(geo2.getLatitude());
        double lon2 = Math.toRadians(geo2.getLongitude());
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * 使用Haversine公式计算两点之间的距离（单位：英里）
     *
     * @param geo1 第一个点的纬度
     * @param geo2 第二个点的纬度
     * @return 两点之间的距离（英里）
     */
    public static double calculateDistanceInMiles(GeoPoint geo1, GeoPoint geo2) {
        double lat1 = Math.toRadians(geo1.getLatitude());
        double lon1 = Math.toRadians(geo1.getLongitude());
        double lat2 = Math.toRadians(geo2.getLatitude());
        double lon2 = Math.toRadians(geo2.getLongitude());
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_MILES * c;
    }

    public static void main(String[] args) {
        // 测试：计算北京（纬度39.9042，经度116.4074）和上海（纬度31.2304，经度121.4737）之间的距离
        double distanceKm = calculateDistanceInKilometers(new GeoPoint(39.9042, 116.4074), new GeoPoint(31.2304, 121.4737));
        System.out.println("北京和上海之间的距离（千米）: " + distanceKm);

        double distanceMiles = calculateDistanceInMiles(new GeoPoint(39.9042, 116.4074), new GeoPoint(31.2304, 121.4737));
        System.out.println("北京和上海之间的距离（英里）: " + distanceMiles);
        // 示例：中心点坐标和半径（单位：米）
        double centerLat = 39.9042; // 北京的纬度（示例）
        double centerLon = 116.4074; // 北京的经度（示例）
        double radiusMeters = 1000; // 范围半径（单位：米）

        // 模拟一些点坐标
        List<GeoPoint> points = Arrays.asList(
                new GeoPoint(39.9043, 116.4075), // 在范围内
                new GeoPoint(39.9042, 116.5074), // 不在范围内但经度接近
                new GeoPoint(39.8042, 116.4074), // 不在范围内但纬度接近
                new GeoPoint(40.0042, 116.4074)  // 明显不在范围内
        );

        // 使用简化的方法过滤点列表
        List<GeoPoint> filteredPoints = new ArrayList<>();
        for (GeoPoint point : points) {
            if (isWithinRadius(centerLat, centerLon, point.getLatitude(), point.getLongitude(), radiusMeters)) {
                filteredPoints.add(point);
            }
        }

        // 输出过滤后的点列表
        for (GeoPoint point : filteredPoints) {
            System.out.println("符合条件的点: " + point.getLatitude() + ", " + point.getLongitude());
        }
    }
}
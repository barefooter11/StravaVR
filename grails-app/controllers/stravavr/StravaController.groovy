package stravavr

import org.jstrava.authenticator.AuthResponse;
import org.jstrava.authenticator.StravaAuthenticator
import org.jstrava.connector.JStravaV3
import org.jstrava.entities.activity.Activity
import org.jstrava.entities.athlete.Athlete
import org.jstrava.entities.stream.Stream;

class StravaController {

    JStravaV3 jStrava

    def login() {

        redirect(url:"https://www.strava.com/oauth/authorize?" +
                    "client_id=15993&" +
                    "response_type=code&" +
                    "redirect_uri=http://localhost:8080/strava/tokenExchange")
    }

    def tokenExchange() {

        StravaAuthenticator authenticator = new StravaAuthenticator(15993, "http://localhost:8080/strava/tokenExchange", "6127a5de44a0d74e8b3b75bc02080ca09b536192");
        AuthResponse response = authenticator.getToken(params.code);
        String token = response.getAccess_token()

        jStrava = new JStravaV3(token);

        redirect action: 'viewAthlete'

    }

    def viewAthlete() {

        Athlete athlete = jStrava.getCurrentAthlete()
        List<Activity> activities = jStrava.getCurrentAthleteActivities(1,50)

        [athlete:athlete,activities:activities]

    }

    def viewActivity() {

        def activityID = Integer.parseInt(params.id)

        Activity activity = jStrava.findActivity(activityID)
        def averageSpeed = activity.getAverage_speed()
        def maxSpeed = activity.getMax_speed()

        List<Stream> streams = jStrava.findActivityStreams(activityID, "distance,latlng,altitude,time")

        def latlngs = streams.get(0).data
        def times = streams.get(1).data
        def distances = streams.get(2).data
        def altitudes = streams.get(3).data

        double lonAdjustment
        double maxLat, minLat, maxLon, minLon, maxAltitude, minAltitude

        def points = []

        for(int i = 0; i < latlngs.size(); i++) {

            def distance = distances.get(i);
            def latlng = latlngs.get(i);
            def altitude = altitudes.get(i)
            def time = times.get(i);

            if(i % 3 != 0) {
                continue
            }

            def point = [:]

            double lat = latlng.get(0)
            double lon = latlng.get(1)

            point['x'] = lon
            point['y'] = altitude
            point['z'] = lat
            point['distance'] = distance
            point['time'] = time

            points.add(point)

            if(i == 0) {

                point['speed'] = 0

                maxLat = lat
                minLat = lat
                maxLon = lon
                minLon = lon
                maxAltitude = altitude
                minAltitude = altitude
                lonAdjustment = Math.cos(Math.toRadians(lat))
                printf("Lat: " + lat + "Adjust Amount: " + Math.cos(lat) + "\n")
            } else {

                def prevPoint = points.get(points.size() - 2)
                def speed = (distance - prevPoint['distance']) / (time - prevPoint['time'])
                point['speed'] = speed

                if (lat > maxLat) maxLat = lat
                if (lat < minLat) minLat = lat
                if (lon > maxLon) maxLon = lon
                if (lon < minLon) minLon = lon
                if(altitude > maxAltitude) maxAltitude = altitude
                if(altitude < minAltitude) minAltitude = altitude
            }
        }

        def latDiff = maxLat - minLat
        def lonDiff = (maxLon - minLon) * lonAdjustment
        def aspectRatio = latDiff/lonDiff
        def metersAcross = lonDiff * lonAdjustment * 111319.9
        def altitudeDiff = maxAltitude - minAltitude

        def mapCenter = ["maLat "]

        def adjustedMaxLon = 10.0
        def adjustedMaxLat = adjustedMaxLon * latDiff / lonDiff
        def adjustedMaxAltitude = adjustedMaxLon * altitudeDiff / metersAcross

        points.each { point ->

            def adjustedLon = (point['x'] - minLon) * lonAdjustment
            def adjustedLat = point['z'] - minLat
            def adjustedAltitude = point['y'] - minAltitude

            adjustedLon = adjustedMaxLon * adjustedLon / lonDiff - (adjustedMaxLon / 2.0)
            adjustedLat = adjustedMaxLat * adjustedLat / latDiff - (adjustedMaxLat / 2.0)
            adjustedLat *= -1
            adjustedAltitude = adjustedMaxAltitude * adjustedAltitude / altitudeDiff

            point['x'] = adjustedLon
            point['z'] = adjustedLat
            point['y'] = adjustedAltitude
            point['speedColor'] = getColor( point['speed'], maxSpeed, averageSpeed )
        }

        printf("Max Lat: " + maxLat + "\n")
        printf("Min Lat: " + minLat + "\n")

        printf("Max Lon: " + maxLon + "\n")
        printf("Min Lon: " + minLon + "\n")

        printf("Min Altitude: " + minAltitude + "\n")
        printf("Max Altitude: " + maxAltitude + "\n")

        printf("Adjusted Max X: " + adjustedMaxLon + "\n")
        printf("Adjusted Max Z: " + adjustedMaxLat + "\n")

        printf("Max Speed: " + maxSpeed + "\n")

        printf("Meters Across: " + metersAcross + "\n")

        [activity:activity, points:points, aspectRatio:aspectRatio]

    }

    private String getColor(speed, maxSpeed, averageSpeed) {

        def weight
        if(speed == averageSpeed) weight = 0.5;
        else if(speed < averageSpeed) weight = (speed / averageSpeed) * 0.5
        else weight = ((speed - averageSpeed) / maxSpeed) * 0.5 + 0.5

        def color1, color2;

        if(weight < (1 / 3)) {
            color1 = [255, 0, 0];
            color2 = [94, 43, 2];
        } else if(weight < (2 / 3)) {
            weight -= (1/3)
            color1 = [94, 43, 2];
            color2 = [255, 255, 0];
        } else {
            weight -= (2/3)
            color1 = [255, 255, 0];
            color2 = [0, 255, 0];
        }

        def p = weight / (1/3);
        def w = p * 2 - 1;
        def w1 = (w/1+1) / 2;
        def w2 = 1 - w1;
        def rgb = [(int)Math.round(color1[0] * w1 + color2[0] * w2),
                   (int)Math.round(color1[1] * w1 + color2[1] * w2),
                   (int)Math.round(color1[2] * w1 + color2[2] * w2)];

        def r = Integer.toHexString(rgb[0]);
        def g = Integer.toHexString(rgb[1]);
        def b = Integer.toHexString(rgb[2]);

        if(r.size() < 2) r = "0" + r
        if(g.size() < 2) g = "0" + g
        if(b.size() < 2) b = "0" + b

        return "#"+ r + g + b;
    }
}

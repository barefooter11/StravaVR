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
        List<Activity> activities = jStrava.getCurrentAthleteActivities(1,10)

        [athlete:athlete,activities:activities]

    }

    def viewActivity() {

        def activityID = Integer.parseInt(params.id)

        Activity activity = jStrava.findActivity(activityID)
        List<Stream> streams = jStrava.findActivityStreams(activityID, "latlng")
        def latlng = streams.get(0)

        for(ArrayList<Double> pair: latlng.data) {
            printf(pair.get(0) + " " + pair.get(1) + "\n")
        }

        printf("test")

        [activity:activity, latlng:streams.get(0)]

    }
}

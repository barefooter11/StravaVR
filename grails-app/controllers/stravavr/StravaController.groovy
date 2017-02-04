package stravavr

import org.jstrava.authenticator.AuthResponse;
import org.jstrava.authenticator.StravaAuthenticator
import org.jstrava.connector.JStravaV3
import org.jstrava.entities.athlete.Athlete;

class StravaController {

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

        JStravaV3 jStrava = new JStravaV3(token);

        Athlete test = jStrava.getCurrentAthlete();
        def name = test.getBikes().get(0).getModel_name();

        print name

    }
}

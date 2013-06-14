package controllers;

import api.BimeApi;
import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;

public class Bime extends Controller {

    static final String API_BASE_URL = "https://api.bimeapp.com/v2";

    static OAuthService service = new ServiceBuilder()
            .provider(BimeApi.class)
            .apiKey("q9AFdIlKCfgaPpjIC78BhrKMwCgw4FR9GjUFCI4S") //Replace by your own Consumer Key
            .apiSecret("IDgK2lPpZCLh7qeViKki1UaJ0OWwZPdCJqKQTttc") //Replace by your own Consumer Secret
            .callback("http://localhost:9000/callback")
            .build();
    static Token accessToken = null;

    public static Result index() {
        return redirect("/connect");
    }

    public static Result connect() {
        return redirect(service.getAuthorizationUrl(null));
    }

    public static Result callback() {
        Request request = request();
        //The url will look like : http://localhost:9000/callback?code=N7ixzVjgaQhZjgHBXI4O&state=
        //So we have to get `code` from the query string
        String code = StringUtils.join(request.queryString().get("code"), "");
        Verifier verifier = new Verifier(code);
        Bime.accessToken = service.getAccessToken(null, verifier);

        return redirect("/actions");
    }

    public static Result actions() {
        return ok(actions.render());
    }

    public static Result dashboards() {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, API_BASE_URL + "/dashboards");
        service.signRequest(Bime.accessToken, oauthRequest);
        org.scribe.model.Response response = oauthRequest.send();

        return ok(response.getStream()).as("application/json");
    }

    public static Result connections() {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, API_BASE_URL + "/connections");
        service.signRequest(Bime.accessToken, oauthRequest);
        org.scribe.model.Response response = oauthRequest.send();

        return ok(response.getStream()).as("application/json");
    }

}

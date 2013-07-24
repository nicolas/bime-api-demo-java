package controllers;

import api.BimeApi;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;

public class Bime extends Controller {

    static final String API_BASE_URL = "https://api.bimeapp.com/v2";

    static OAuthService service = new ServiceBuilder()
            .provider(BimeApi.class)
            .apiKey("yAeUoZU3qmIRN05e3kNQ1T94vOZKYIHYGd4ls92U") //Replace by your own Consumer Key
            .apiSecret("YERpNO0NjJ6lAq7kxHOk1OFdPLreisvGS3no1D2u") //Replace by your own Consumer Secret
            .callback("http://localhost:9000/callback")
            .build();
    static Token accessToken = new Token("rtcIe1nbtwhG8fjfSPnUiIM4hWegrWGNGeRokHdI", ""); //Replace by your own Access Token

    public static Result actions() {
        return ok(views.html.actions.render());
    }

    public static Result dashboards() {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, API_BASE_URL + "/dashboards");
        service.signRequest(Bime.accessToken, oauthRequest);
        org.scribe.model.Response response = oauthRequest.send();

        return ok(response.getBody()).as("application/json");
    }

    public static Result connections() {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, API_BASE_URL + "/connections");
        service.signRequest(Bime.accessToken, oauthRequest);
        org.scribe.model.Response response = oauthRequest.send();

        return ok(response.getBody()).as("application/json");
    }

    public static Result connection(int id) {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, API_BASE_URL + "/connections/" + id);
        service.signRequest(Bime.accessToken, oauthRequest);
        org.scribe.model.Response response = oauthRequest.send();

        return ok(response.getBody()).as("application/json");
    }

    public static Result workflow() {
        //Create a named user group
        OAuthRequest nugRequest = new OAuthRequest(Verb.POST, API_BASE_URL + "/named_user_groups");
        nugRequest.addBodyParameter("name", "Test Group");
        service.signRequest(Bime.accessToken, nugRequest);
        org.scribe.model.Response nugResponse = nugRequest.send();

        JsonNode namedUserGroupJson = Json.parse(nugResponse.getBody());
        int namedUserGroupId = namedUserGroupJson.findValues("result").get(0).findPath("id").getIntValue();

        //Create a named user associated with the created group and get access token from the response to identify the user
        OAuthRequest nuRequest = new OAuthRequest(Verb.POST, API_BASE_URL + "/named_users");
        nuRequest.addBodyParameter("full_name", "North Ameria");
        nuRequest.addBodyParameter("named_user_group_id", Integer.toString(namedUserGroupId));
        service.signRequest(Bime.accessToken, nuRequest);
        org.scribe.model.Response nuResponse = nuRequest.send();

        JsonNode namedUserJson = Json.parse(nuResponse.getBody());
        String namedUserAccessToken = namedUserJson.findValues("result").get(0).findPath("access_token").getTextValue();

        //Create a data security rule
        OAuthRequest ruleRequest = new OAuthRequest(Verb.POST, API_BASE_URL + "/data_security_rules");
        ruleRequest.addHeader("Content-Type", "application/json");
        ruleRequest.addPayload("{\"connection_id\": 31331, \"datafield\": \"country\", \"authorized_values\": [\"Canada\", \"USA\"]}");
        service.signRequest(Bime.accessToken, ruleRequest);
        org.scribe.model.Response ruleResponse = ruleRequest.send();

        JsonNode ruleJson = Json.parse(ruleResponse.getBody());
        int ruleId = ruleJson.findValues("result").get(0).findPath("id").getIntValue();

        //Associate named user group with data security rule
        OAuthRequest nugsRequest = new OAuthRequest(Verb.POST, API_BASE_URL + "/named_user_group_securities");
        nugsRequest.addBodyParameter("named_user_group_id", Integer.toString(namedUserGroupId));
        nugsRequest.addBodyParameter("data_security_rule_id", Integer.toString(ruleId));
        service.signRequest(Bime.accessToken, nugsRequest);
        org.scribe.model.Response nugsResponse = nugsRequest.send();

        //Associate named user group with dashboard
        OAuthRequest dsRequest = new OAuthRequest(Verb.POST, API_BASE_URL + "/dashboard_subscriptions");
        dsRequest.addBodyParameter("dashboard_id", Integer.toString(24334));
        dsRequest.addBodyParameter("named_user_group_id", Integer.toString(namedUserGroupId));
        service.signRequest(Bime.accessToken, dsRequest);
        org.scribe.model.Response dsResponse = dsRequest.send();

        return ok(views.html.dashboard.render(namedUserAccessToken));
    }

}

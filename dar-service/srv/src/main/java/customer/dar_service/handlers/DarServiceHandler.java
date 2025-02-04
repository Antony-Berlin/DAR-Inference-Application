package customer.dar_service.handlers;




import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Arrays.asList;
import static com.sap.cds.ResultBuilder.selectedRows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.util.EntityUtils;

import com.sap.cds.Result;
import cds.gen.darservice.*;

@Component
@ServiceName("DarService")
public class DarServiceHandler implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(DarServiceHandler.class);

    @On(event = CqnService.EVENT_CREATE, entity = "DarService.Root")
    public void onCreate(CdsCreateEventContext context) {


        log.debug("onCreate called");

        Map<String, Object> results = new HashMap<>();
        context.getCqn().entries().forEach(i -> {
            results.put("topN", i.get("topN"));
            results.put("objects", i.get("objects"));
        });

        String token = getAccessToken();

        String Responsetext = getDarInference(results.get("topN"), results.get("objects"), token);
        Response rsp = Response.create();
        rsp.setResponseText(Responsetext);
        results.put("Response", rsp);
        Result res = selectedRows(asList(results)).result();
        context.setResult(res);

    }

   

    String getAccessToken() {
        String authUrl = "https://iprtenant4.authentication.eu12.hana.ondemand.com/oauth/token";
        String clientId = "sb-afc88072-073c-40ae-bb1e-03e85018d6d2!b8008|dar-std-internal-production!b337116";
        String clientSecret = "7074ca87-1829-41be-a2d4-3fb7d6a00c57$Swn3k-S4fuBFRvcChejZNHHQeaf59mAGznw_dCo6X-w=";
        String grantType = "client_credentials";

        JsonObject payload = new JsonObject();
        payload.addProperty("client_id", clientId);
        payload.addProperty("client_secret", clientSecret);
        payload.addProperty("grant_type", grantType);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(authUrl);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder form = new StringBuilder();
            form.append("client_id=").append(clientId)
            .append("&client_secret=").append(clientSecret)
            .append("&grant_type=").append(grantType);

            post.setEntity(new StringEntity(form.toString()));

            try (CloseableHttpResponse response = client.execute(post)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.get("access_token").getAsString();
            }
        } catch (IOException e) {
            log.error("Error while getting access token", e);
            return null;
        }
    }


    String  getDarInference(Object topN, Object objects, String token) {
        String modelID = "MDLFF684BC9-597A-4675-9DA6-CBBBB4D57BD9";
        String darUrl = "https://aiservices-dar.cfapps.eu12.hana.ondemand.com/inference/api/v3/models/"+modelID+"/versions/1";
       
        System.out.println("calling dar service");

        JsonObject payload = new JsonObject();
        payload.addProperty("topN", (Integer)topN);
        
        // Convert objects to JsonArray

        
        JsonArray objectsArray = new JsonArray();
        if (objects instanceof List) {
            System.out.println("objects is a list");
            List<?> objectsList = (List<?>) objects;
            for (Object obj : objectsList) {
            if (obj instanceof Map) {
                JsonObject jsonObject = new JsonObject();
                Map<?, ?> map = (Map<?, ?>) obj;
                    if (map.containsKey("features") && map.get("features") instanceof List) {
                        JsonArray featuresArray = new JsonArray();
                        List<?> featuresList = (List<?>) map.get("features");
                        for (Object featureObj : featuresList) {
                            if (featureObj instanceof Map) {
                                JsonObject featureJson = new JsonObject();
                                Map<?, ?> featureMap = (Map<?, ?>) featureObj;
                                featureJson.addProperty("name", featureMap.get("name").toString());
                                featureJson.addProperty("value", featureMap.get("value").toString());
                                featuresArray.add(featureJson);
                            }
                        }
                        jsonObject.add("features", featuresArray);
                }
                objectsArray.add(jsonObject);
            }
            }
        }
        payload.add("objects", objectsArray);
        
        System.out.println("payload: "+payload.toString());

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(darUrl);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + token);

            post.setEntity(new StringEntity(payload.toString()));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                System.out.println(jsonResponse.toString());
                return jsonResponse.toString();
            }
        } catch (IOException e) {
            log.error("Error while getting DAR inference", e);
            return null;
        }
    }


  
}


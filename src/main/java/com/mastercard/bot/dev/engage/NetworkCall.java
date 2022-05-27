package com.mastercard.bot.dev.engage;

import com.microsoft.bot.integration.ClasspathPropertiesConfiguration;
import com.microsoft.bot.integration.Configuration;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import org.json.JSONArray;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkCall {

    static String jwtToken = "";
    static String code = "";

    public static String getAccounts() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api-sandbox.aiia.eu/v1/accounts")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();
            String retVal = parseAccountData(response.body().string());
            int responseCode = response.code();
            if (responseCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
            response.close();
            return retVal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "invalid response";
    }

    public static String makePost() {

        try {
            URL url = new URL("https://api-sandbox.aiia.eu/v1/accounts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return parseAccountData(content.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "nothing to show";
    }


    public static String getTransactions() {

        try {
            System.out.println("Transactions from network call");
            URL url = new URL("https://api-sandbox.aiia.eu/v1/accounts/MzViNjhkOGYtOGI2OC00YjIxLTk3MDctOTExZDBmMjhlMmI1fERLX1Rlc3RCYW5rfFJPS0huNkdXVEVzMHdCeng1d01zTEhiWVU2UXpXNHEyNEh2OVJuRmVsUUkuOWEzZDRhMDRjMmVm/transactions?pageSize=5");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code :" + conn.getResponseCode());
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return parseTransactionData(content.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Invalid";
    }

    public static  void onBoardToken(String ref_code) {
        Configuration withConfiguration = new ClasspathPropertiesConfiguration();
        try {
            String redirectUrl = withConfiguration.getProperty("redirect");
            String tokenUrl = withConfiguration.getProperty("tokenUrl");
            String client_Id = withConfiguration.getProperty("client_Id");
            String client_Secret = withConfiguration.getProperty("client_Secret");
            code = ref_code;
            JSONObject object = new JSONObject();
            object.put("grant_type", "authorization_code");
            object.put("code", ref_code);
            object.put("redirect_uri", redirectUrl);


            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(JSON, object.toString());
            System.out.println("Token url " + tokenUrl);
            Request request = new Request.Builder()
                    .url(tokenUrl)
                    .addHeader("Authorization", Credentials.basic(client_Id, client_Secret))
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println("Response code " + response.code());
            ResponseBody body = response.body();
            String retVal = body.string();
            System.out.println("Our response = " + retVal);

            jwtToken = new JSONObject(retVal).getString("access_token");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static String parseTransactionData(String json){
        StringBuilder stb = new StringBuilder();
        try{
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonData = jsonObject.getJSONArray("transactions");
        stb.append("-- Last 5 Transactions --");
        stb.append("\r\n");
        stb.append("------");
        stb.append("\r\n");

        jsonData.iterator().forEachRemaining(element -> {
            JSONObject newObject = new JSONObject(element.toString());
            stb.append(newObject.get("date"));
            stb.append("\r\n");
            stb.append("Amount: ");
            stb.append(newObject.getBigDecimal("amount"));
            stb.append("\r\n");
            stb.append("Balance: ");
            stb.append(new JSONObject(newObject.get("estimatedBalance").toString()).get("value"));
            stb.append("\r\n");
            stb.append("Description: ");
            stb.append(newObject.get("text"));
            stb.append("\r\n");
            stb.append("------");
            stb.append("\r\n");
        });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return stb.toString();
    }



    private static String parseAccountData(String json) {
        StringBuilder stb = new StringBuilder();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonData = jsonObject.getJSONArray("accounts");
            stb.append("Owner - "+jsonData.getJSONObject(0).get("owner"));
            stb.append("\r\n");
            stb.append("------");
            stb.append("\r\n");
            jsonData.iterator().forEachRemaining(element -> {
                JSONObject newObject = new JSONObject(element.toString());
                stb.append("IBAN - "+ newObject.getJSONObject("number").get("iban"));
                stb.append("\r\n");
                stb.append("Account type  - " + newObject.getString("name") + "\r\n Currency - " + newObject.getString("currency"));
                stb.append("\r\n");
                stb.append("Balance - " + newObject.getBigDecimal("bookedBalance"));
                stb.append("\r\n");
                stb.append("------");
                stb.append("\r\n");
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stb.toString();
    }
}

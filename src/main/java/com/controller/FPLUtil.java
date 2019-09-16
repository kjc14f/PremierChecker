package com.controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.Main.DUMMY;

public class FPLUtil {

    public static int GAMEDAYS = 8;
    public static int WEEK_OFFSET = 0;
    public static int CURRENT_GAMEWEEK = 0;
    public static final String BASE_FPL_URL = "https://fantasy.premierleague.com/api/";
    public static final String LOGIN_FPL_URL = "https://users.premierleague.com/accounts/login/";
    public static JSONObject FPL_DATA;
    public static final String INJURIES_URL = "https://www.premierinjuries.com/injury-table.php";
    public static final String BONUS_POINTS_URL = "https://www.anewpla.net/fpl/live/";

    private static final String AUTH_COOKIE_NAME = "sessionid";
    private static final String PROFILE_COOKIE_NAME = "pl_profile";
    public static Cookie AUTH_COOKIE = null;
    public static Cookie PROFILE_COOKIE = null;

    public static Object makeFPLRequest(String parameter, boolean isArray, boolean auth) {
        return DUMMY ? readLocal(parameter, isArray) : makeHttpRequest(parameter, isArray, auth);
    }

    private static Object makeHttpRequest(String parameter, boolean isArray, boolean auth) {

        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build());
        HttpGet request = new HttpGet(parameter);

        if (auth) {
            if (AUTH_COOKIE == null || AUTH_COOKIE.getValue() == null || PROFILE_COOKIE == null || PROFILE_COOKIE.getValue() == null) loginRequest();
            CookieStore cookieStore = new BasicCookieStore();
            cookieStore.addCookie(AUTH_COOKIE);
            cookieStore.addCookie(PROFILE_COOKIE);
            builder.setDefaultCookieStore(cookieStore);
        }

        HttpClient client = builder.build();

        try {
            HttpResponse response = client.execute(request);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            return isArray ? new JSONArray(content.toString()) : new JSONObject(content.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void loginRequest() {
        Properties prop = new Properties();
        try {
            prop.load(FPLUtil.class.getResourceAsStream("/application.properties"));

            CookieStore httpCookieStore = new BasicCookieStore();
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
            HttpClient http = builder
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();

            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addTextBody("login", prop.getProperty("fpl.username"))
                    .addTextBody("password", prop.getProperty("fpl.password"))
                    .addTextBody("app", "plfpl-web")
                    .addTextBody("redirect_uri", "https://fantasy.premierleague.com/a/login")
                    .build();

            HttpPost httpPost = new HttpPost(LOGIN_FPL_URL);
            httpPost.setEntity(entity);
            try {
                HttpResponse httpResponse = http.execute(httpPost);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Cookie> cookies = httpCookieStore.getCookies();
            Optional<Cookie> sessionOptional = cookies.stream().filter(e -> e.getName().equals(AUTH_COOKIE_NAME)).findFirst();
            AUTH_COOKIE = sessionOptional.orElse(null);
            Optional<Cookie> profileOptional = cookies.stream().filter(e -> e.getName().equals(PROFILE_COOKIE_NAME)).findFirst();
            PROFILE_COOKIE = profileOptional.orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray readLocal(String parameter, boolean isArray) {

        //TODO implement

        File file = new File(FPLUtil.class.getClassLoader().getResource("historical/end19/" + parameter + ".json").getFile());

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

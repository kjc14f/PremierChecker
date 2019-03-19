package com.controller;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import static com.Main.DUMMY;
import static com.controller.Controller.BASE_FPL_URL;

public class FPLUtil {

    public static JSONArray makeFPLRequest(String parameter) {
        return DUMMY ? readLocal(parameter) : makeHttpRequest(parameter);
    }

    private static JSONArray makeHttpRequest(String parameter) {
        try {
            URL url = new URL(BASE_FPL_URL + parameter);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();
            return new JSONArray(content.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JSONArray readLocal(String parameter) {

        File file = new File(FPLUtil.class.getClassLoader().getResource("historical/" + parameter + ".json").getFile());

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

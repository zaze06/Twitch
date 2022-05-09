package me.alien.twitch.integration;

import java.io.*;

public class Loader {
    public static String loadFile(InputStream s) {
        return loadFile(s, "");
    }

    public static String loadFile(InputStream s, String newLine){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            StringBuilder data = new StringBuilder();
            String tmp = "";
            while((tmp=in.readLine())!=null){
                data.append(tmp);
                data.append(newLine);
            }
            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

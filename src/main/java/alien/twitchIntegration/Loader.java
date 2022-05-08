package alien.twitchIntegration;

import java.io.*;

public class Loader {
    public static String loadFile(InputStream s) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            StringBuilder data = new StringBuilder();
            String tmp = "";
            while((tmp=in.readLine())!=null){
                data.append(tmp);
            }
            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

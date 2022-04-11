package alien.twitchIntegration;

import com.github.twitch4j.helix.domain.User;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class Loader {
    public static String leadFile(InputStream s) {
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

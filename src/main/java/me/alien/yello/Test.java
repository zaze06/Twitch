/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello;

import me.limeglass.streamelements.api.StreamElements;
import me.limeglass.streamelements.api.StreamElementsBuilder;
import me.limeglass.streamelements.api.objects.Points;
import me.limeglass.streamelements.api.objects.User;
import org.json.JSONObject;

public class Test {
    private static final JSONObject credentials = new JSONObject(Loader.loadFile(Test.class.getResource("/credentials.json.old").getFile())).getJSONObject("SE");

    public static void main(String[] args) {

        //alternative constructor if you please.
        //instance = new StreamElementsBuilder(config.getString("client.account"), config.getString("client.token")).build();
        StreamElements instance = new StreamElementsBuilder()
                .withAccountID("5fabf44d9b9b4d54d1d7c327")
                .withToken(credentials.getString("token"))
                .withConnectionTimeout(10000)
                .build();

        Points points = instance.getUserPoints("AlienFromDia");

        //Grab the user instance.
        User user = points.getUser();

        //Example usage.
        System.out.println(user.getName()
                + " is #" + points.getRank() + " in the leaderboard"
                + " with " + points.getCurrentPoints() + " points! PogChamp");

        long org = points.getCurrentPoints();

        instance.setCurrentUserPoints(user, 1000);

        points = instance.getUserPoints(user);
        System.out.println(user.getName() + " now has " + points.getCurrentPoints() + " points!");

        instance.setCurrentUserPoints(user, org);
        points = instance.getUserPoints(user);
        System.out.println(user.getName() + " now has " + points.getCurrentPoints() + " points!");

        /*try {
            URL url = new URL("https://api.streamelements.com/kappa/v2/points/623de6ad7849053ef4d69f1c/top/");
            URLConnection con = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String tmp = "";
            while((tmp = reader.readLine()) != null){
                builder.append(tmp);
            }
            System.out.println(builder);
            JSONObject response = new JSONObject(builder.toString());
            Map<String, Integer> map = new HashMap<>();
            for(Object obj : response.getJSONArray("users").toList()){
                if(obj instanceof HashMap user){
                    map.put((String) user.get("username"), (Integer) user.get("points"));
                }else{
                    System.out.println(obj.getClass());
                }
            }
            System.out.println(Arrays.toString(map.entrySet().toArray()));
        }catch (Exception ignored){

        }*/
    }
}

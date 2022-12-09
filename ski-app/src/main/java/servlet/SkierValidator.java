package servlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.Utils;

import static java.lang.Integer.parseInt;

public class SkierValidator {

    public static boolean urlExisting(String url) {
        String[] urlPath = url.split("/");
        if (urlPath == null || urlPath.length != 8) {
            return false;
        }

        return true;
    }
    public static boolean urlIsValid(String url) {
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        String[] urlPath = url.split("/");

        // validate {resortID}
        if (!Utils.isNumeric(urlPath[1])) {
            return false;
        }

        // validate 'seasons'
        if (!urlPath[2].equals("seasons")) {
            return false;
        }

        // validate {seasonID}
        if (!Utils.isNumeric(urlPath[3])) {
            return false;
        }

        // validate 'days'
        if (!urlPath[4].equals("days")) {
            return false;
        }

        // validate {dayID}
        if (!Utils.isNumeric(urlPath[5]) || parseInt(urlPath[5]) < 1 || Integer.parseInt(urlPath[5]) > 366) {
            return false;
        }

        // validate 'skiers'
        if (!urlPath[6].equals("servlet")) {
            return false;
        }

        // validate {skierID}
        return Utils.isNumeric(urlPath[7]);
    }

    public static boolean bodyIsValid(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        try {
            JsonObject bodyJson = JsonParser.parseString(s).getAsJsonObject();
            JsonElement timeElement = bodyJson.get("time");
            String time = Utils.getStringFromJsonElement(timeElement);
            JsonElement liftIdElement = bodyJson.get("liftID");
            String liftId = Utils.getStringFromJsonElement(liftIdElement);

            return Utils.isNumeric(time) && Utils.isNumeric(liftId);
        } catch (Exception e) {
            return false;
        }
    }


}

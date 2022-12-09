package utils;

import com.google.gson.JsonElement;

public class Utils {
    public static boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }

        try {
            int num = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String getStringFromJsonElement(JsonElement obj) {
        return obj == null || obj.isJsonNull() ? "" : obj.getAsString();
    }

    public static boolean stringIsEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

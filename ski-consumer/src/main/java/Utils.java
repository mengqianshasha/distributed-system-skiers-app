import com.google.gson.JsonElement;

public class Utils {
    public static String getStringFromJsonElement(JsonElement obj) {
        return obj == null || obj.isJsonNull() ? "" : obj.getAsString();
    }

    public static int getIntFromJsonElement(JsonElement obj) {
        String str = getStringFromJsonElement(obj);
        return Integer.parseInt(str);
    }
}

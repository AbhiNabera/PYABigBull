package nabera.ranjan.abhinabera.pyabigbull.Transactions;

import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by AVINASH on 1/2/2019.
 */

public class JsonObjectFormatter {

    JsonObject parent;
    String last_key;

    public JsonObjectFormatter(JsonObject object) {
        this.parent = object;
    }

    public JsonObjectFormatter child(String key) {
        last_key = key;
        if (parent.get(key)==null) {
            parent.add(key, new JsonObject());
            return new JsonObjectFormatter(parent.get(key).getAsJsonObject());
        }

        return new JsonObjectFormatter(parent.get(key).getAsJsonObject());
    }

    public JsonObject pushObject(String key, JsonObject object) {
        Log.d("PUSH", last_key + ":" + object);
        parent.add(key, object);

        return parent;
    }

    public JsonObject pushValue(String key, String data) {
        parent.addProperty(key, data);
        return parent;
    }

    public JsonObject remove(String key) {
        parent.remove(key);

        return parent;
    }

    public JsonObject get(String key) {
        JsonObject object = parent.getAsJsonObject(key);
        return object;
    }
}

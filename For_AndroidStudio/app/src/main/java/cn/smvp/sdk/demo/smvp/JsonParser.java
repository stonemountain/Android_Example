package cn.smvp.sdk.demo.smvp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.smvp.sdk.demo.util.MyLogger;


public class JsonParser {
    private static JsonParser jsonParser = null;
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static synchronized void init() {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }

    }

    public static JsonParser getInstance() {
        if (jsonParser == null) {
            init();
        }

        return jsonParser;
    }

    public List<SmvpVideo> parseJsonStringToObject(String result) {
        List<SmvpVideo> videoList = new ArrayList<SmvpVideo>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            Gson gson = new Gson();
            Type type = new TypeToken<List<SmvpVideo>>() {
            }.getType();
            videoList = gson.fromJson(jsonArray.toString(), type);
        } catch (Exception e) {
            e.printStackTrace();
            MyLogger.w(LOG_TAG, "parseJsonStringToObject error:", e);
        }

        return videoList;
    }

}

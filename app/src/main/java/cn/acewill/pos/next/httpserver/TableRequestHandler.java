package cn.acewill.pos.next.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by Acewill on 2016/6/13.
 */
public class TableRequestHandler implements PosRequestHandler{
    @Override
    public String handle(String uri, Map<String, String> params) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(params);
        //return "tables" + params.toString();
    }
}

package cn.acewill.pos.next.httpserver;

import java.util.Map;

/**
 * Created by Acewill on 2016/6/13.
 */
public interface PosRequestHandler {
    public String handle(String uri, Map<String, String> params);
}

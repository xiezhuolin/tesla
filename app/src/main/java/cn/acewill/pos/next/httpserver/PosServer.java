package cn.acewill.pos.next.httpserver;

import android.util.Log;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Acewill on 2016/6/12.
 */
public class PosServer extends NanoHTTPD {
    private static final String TAG = "PosServer";
    private TableRequestHandler tableHandler;
    private DishRequestHandler dishRequestHandler;
    private String DEFAULT_RESPONSE = "<html><body><h1>Hello From Android App</h1></body></html>\n";

    public PosServer(int port) {
        super(port);
        tableHandler = new TableRequestHandler();
        dishRequestHandler = new DishRequestHandler();
    }


    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> params = session.getParms();

        Log.i(TAG, "Request: " + method + "," + uri + "," + params.toString());

        if (params.isEmpty() && uri.equals("/")) {
            return newFixedLengthResponse(DEFAULT_RESPONSE);
        } else if (uri.startsWith("/tables")) {
            return newFixedLengthResponse(tableHandler.handle(uri, params));
        } else if (uri.startsWith("/dishes")) {
            return newFixedLengthResponse(dishRequestHandler.handle(uri, params));
        } else {
            return newFixedLengthResponse(DEFAULT_RESPONSE);
        }
    }
}

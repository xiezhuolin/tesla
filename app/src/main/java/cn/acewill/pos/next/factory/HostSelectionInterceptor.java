package cn.acewill.pos.next.factory;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Acewill on 2016/8/8.
 */
public class HostSelectionInterceptor implements Interceptor {
	private static final String TAG = "Interceptor";
	private static String currentUrl;
	private static boolean urlChanged = false;

	public static void setBaseUrl(String baseUrl) {
		currentUrl = baseUrl;
		urlChanged = true;
	}

	//    @Override
	//    public Response intercept(Chain chain) throws IOException {
	//        Request request = chain.request();
	//        String query = request.url().encodedQuery();
	//        String path = request.url().encodedPath();
	//
	//       // if (currentUrl != null && urlChanged) {
	//            request = request.newBuilder()
	//                    .url(currentUrl + path + "?" + query)
	//                    .build();
	//            urlChanged = false;
	//      //  }
	//
	//        return chain.proceed(request);
	//    }

	@Override
	public Response intercept(Chain chain) throws IOException {
		//获得请求信息，此处如有需要可以添加headers信息
		Request request = chain.request();

		String  query   = request.url().encodedQuery();
		String  path    = request.url().encodedPath();

		// if (currentUrl != null && urlChanged) {
		request = request.newBuilder()
				.url(currentUrl + path + "?" + query)
				.build();
		urlChanged = false;
		//  }




		StringBuilder sb = new StringBuilder();
		//打印请求信息
		Log.i(TAG, "url:" + request.url());
		sb.append("url:" + request.url() + "\n");
		Log.i(TAG, "method:" + request.method());
		sb.append("method:" + request.method() + "\n");
		Log.i(TAG, "request-body:" + request.body());
		sb.append("request-body:" + request.body() + "\n");
		//记录请求耗时
		long             startNs = System.nanoTime();
		okhttp3.Response response;
		try {
			//发送请求，获得相应，
			response = chain.proceed(request);
		} catch (Exception e) {
			throw e;
		}
		long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
		//打印请求耗时
		Log.i(TAG, "耗时:" + tookMs + "ms");
		sb.append("耗时:" + tookMs + "ms" + "\n");
		//使用response获得headers(),可以更新本地Cookie。
		Log.i(TAG, "headers==========");
		sb.append("headers==========" + "\n");
		Headers headers = response.headers();
		Log.i(TAG, headers.toString());
		sb.append(headers.toString() + "\n");
		//获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
		ResponseBody responseBody = response.body();

		//为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
		BufferedSource source = responseBody.source();
		source.request(Long.MAX_VALUE); // Buffer the entire body.
		//获得返回的数据
		Buffer buffer = source.buffer();
		//使用前clone()下，避免直接消耗
		Log.i(TAG, "response:" + buffer.clone().readString(Charset.forName("UTF-8")));
		sb.append("response:" + buffer.clone().readString(Charset.forName("UTF-8")) + "\n");

		String sUrl = request.url().toString();
		if (!sUrl.contains("terminal") && !sUrl.contains("getMemberInfo") && !sUrl
				.contains("getAllTemplates") && !sUrl.contains("getPrinters") && !sUrl
				.contains("getKichenStalls") && !sUrl.contains("getKDSes") && !sUrl
				.contains("getStoreConfiguration") && !sUrl
				.contains("https://dexio.syncrock.com/pos/pub/data.action")) {
			Log.i(TAG, "Response content>" + sb.toString());
		} else {
			Log.i(TAG, "Response url>" + sUrl);
		}










		return response;
	}
}

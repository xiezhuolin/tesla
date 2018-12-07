package cn.acewill.pos.next.service.retrofit.response.pay;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：使用Gson解析Json数据信息的工具类
 *
 * @author jiangyizhao@huaxunchina.cn
 * @date 2014年7月22日 上午8:59:31
 */
public class GsonUtils {


	public static String getString(String key, String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     data       = jsonobject.getString(key);
		return data;
	}

	public static boolean getBoolean(String key, String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		boolean    data       = jsonobject.getBoolean(key);
		return data;
	}


	public static long getLong(String json, String key)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		long       contentId  = jsonobject.getLong(key);
		return contentId;
	}

	/**
	 * 功能描述：把JSON数据转换成普通字符串列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static List<String> getStringList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<String>>() {
		}.getType());
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象
	 *
	 * @param jsonData JSON数据
	 * @param clazz    指定的java对象
	 * @return
	 * @throws Exception
	 */
	public static <T> T getSingleBean(String jsonData, Class<T> clazz)
			throws Exception {
		return new Gson().fromJson(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getBeanList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<T>>() {
		}.getType());
	}

	public static <T, clazz> List<T> getBeanList(String jsonData, Class<T> clazz)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<clazz>>() {
		}.getType());
	}

	/**
	 * 功能描述：把JSON数据转换成较为复杂的java对象列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getBeanMapList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData,
				new TypeToken<List<Map<String, Object>>>() {
				}.getType());
	}


	public static String objectToString(Object object) {
		return new Gson().toJson(object);

	}

	public static int getInt(String key, String json) throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		int        data       = jsonobject.getInt(key);
		return data;
	}

}

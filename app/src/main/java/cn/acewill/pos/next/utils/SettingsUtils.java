package cn.acewill.pos.next.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class SettingsUtils {

	/**
	 * 保存字符串到SharedPreferences中
	 * 
	 * @param key
	 * @param value
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean setStringToSharedPreferences(String key,
			String value, Context context) throws Exception {
		if (key == null || value == null)
			return false;
		if ("".equalsIgnoreCase(key.trim()))
			return false;
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_WRITEABLE);
		Editor editor = shares.edit();
		editor.putString(key.trim(), value.trim());
		editor.commit();
		return true;
	}

	/**
	 * 从SharedPreferences中获取指定key值
	 * 
	 * @param key
	 * @param defaultValue
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getStringFromSharedPreferences(String key,
			String defaultValue, Context context) throws Exception {
		if (key == null || defaultValue == null)
			return null;
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_READABLE);
		String returnValue = shares.getString(key.trim(), defaultValue);
		return returnValue;
	}

	/**
	 * 得到时间戳
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getTimeStamp(Context context) throws Exception {
		String timeStamp = getStringFromSharedPreferences(
				Constant.SERVERTIMESTAMP, "0", context);
		if (timeStamp == null) {
			return "0";
		} else {
			return timeStamp;
		}
	}

	/**
	 * 设置时间戳
	 * 
	 * @param context
	 * @param timeStamp
	 * @return
	 * @throws Exception
	 */
	public static boolean setTimeStamp(Context context, String timeStamp)
			throws Exception {
		return setStringToSharedPreferences(Constant.SERVERTIMESTAMP,
				timeStamp, context);
	}

	/**
	 * 设置数据库时间戳
	 */
	public static boolean setDBTimeStamp(Context context, Long timeStamp) {
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_WRITEABLE);
		Editor editor = shares.edit();
		editor.putLong(Constant.DB_TIMESTAMP, timeStamp);
		editor.commit();
		return true;
	}

	public static Long getDBTimeStamp(Context context) {
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_WRITEABLE);
		return shares.getLong(Constant.DB_TIMESTAMP, 0);
	}

	/**
	 * 获得服务器的Ip地址
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getServerIP(Context context) throws Exception {
		return getStringFromSharedPreferences(Constant.SETTING_SERVERIP, "",
				context);
	}

	public static boolean setLicense(Context context, String license)
			throws Exception {
		return setStringToSharedPreferences(Constant.LICENSE, license, context);
	}

	public static String getLicense(Context context) throws Exception {
		return getStringFromSharedPreferences(Constant.LICENSE, "", context);
	}

	/**
	 * 判断是不是选择Https协议
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isHttpsProtocol(Context context) throws Exception {
		boolean isHttpsProtocol = false;
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_READABLE);
		isHttpsProtocol = shares.getBoolean(Constant.SETTING_PROTOCOL, false);
		return isHttpsProtocol;
	}

	public static boolean isAliposProtocol(Context context) throws Exception {
		boolean isAliposProtocol = false;
		SharedPreferences shares = context.getSharedPreferences(
				Constant.SETTING, Context.MODE_WORLD_READABLE);
		isAliposProtocol = shares.getBoolean(Constant.SETTING_ALIPOS, false);
		return isAliposProtocol;
	}

	/**
	 * 得到服务器的Url
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	// public static String getServerUrl(Context context) throws Exception {
	// String protocol = "";
	// String ip = "";
	// ip = getServerIP(context);
	// if (ip == "") {
	// return "";
	// } else {
	// if (!isHttpsProtocol(context)) {
	// protocol = "http";
	// } else {
	// protocol = "https";
	// }
	// return protocol + "://" + ip;
	// }
	// }

	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static int getHandId(Context context) throws Exception {
		int handId = 0;
		handId = Integer.valueOf(getHandIdStr(context));
		return handId;
	}

	public static String getHandIdStr(Context context) throws Exception {
		return getStringFromSharedPreferences(Constant.SETTING_HAND, "1",
				context).replaceAll("\"", "").trim();
	}

	public static String getMid(Context context) throws Exception {
		return getStringFromSharedPreferences(Constant.MID, "-1", context);
	}

	public static boolean setMid(Context context, String mid) throws Exception {
		return setStringToSharedPreferences(Constant.MID, mid, context);
	}

	public static String getSids(Context context) throws Exception {
		return getStringFromSharedPreferences(Constant.SIDS, "", context);
	}

	public static boolean setSids(Context context, String sids)
			throws Exception {
		return setStringToSharedPreferences(Constant.SIDS, sids, context);
	}

	/**
	 * 获取网络图片
	 */
	public static Drawable loadImageFromNetwork(String imageUrl) {
		Drawable drawable = null;
		String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		try {
			// 可以在这里通过文件名来判断，是否本地有此图片
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), imageName);
		} catch (IOException e) {
			Log.d("test", e.getMessage());
		}
		if (drawable == null) {
			Log.d("test", "null drawable");
		} else {
			Log.d("test", "not null drawable");
		}

		return drawable;
	}

}

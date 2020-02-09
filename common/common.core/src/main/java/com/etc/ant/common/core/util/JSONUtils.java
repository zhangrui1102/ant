package com.etc.ant.common.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 专门处理json格式的解析
 * 
 * @version 1.0 2015年6月9日
 */
public class JSONUtils {
	private static ObjectMapper objectMapper = null;
	/**
	 * JSON初始化
	 */
	static {
		objectMapper = new ObjectMapper();
		// 去掉默认的时间戳格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
				false);
		// 设置为中国上海时区
		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,
				false);
		// 空值不序列化
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// 反序列化时，属性不存在的兼容处理
		objectMapper.getDeserializationConfig().withoutFeatures(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 序列化时，日期的统一格式
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 单引号处理
		objectMapper
				.configure(
						com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
						true);

	}

	private JSONUtils() {
	}

	/**
	 * 把对象转换成为Json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertObjectToJson(Object obj) {
		if (obj == null) {
			// throw new IllegalArgumentException("对象参数不能为空。");
			return null;
		}
		try {
			return objectMapper.writeValueAsString(obj);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 把json字符串转成Object对象
	 * 
	 * @param jsonString
	 * @return T
	 */
	public static <T> T parseJsonToObject(String jsonString, Class<T> valueType) {

		if (jsonString == null || "".equals((jsonString))) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonString, valueType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把json字符串转成List对象
	 * 
	 * @param jsonString
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> parseJsonToList(String jsonString,
			Class<T> valueType) {

		if (jsonString == null || "".equals((jsonString))) {
			return null;
		}

		List<T> result = new ArrayList<T>();
		try {
			List<LinkedHashMap<Object, Object>> list = objectMapper.readValue(
					jsonString, List.class);

			for (LinkedHashMap<Object, Object> map : list) {

				String jsonStr = convertObjectToJson(map);

				T t = parseJsonToObject(jsonStr, valueType);

				result.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the
	 * hierarchy的方法
	 * 
	 * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
	 * 
	 * @param text
	 * @return
	 */
	public static JSONObject getJsonObject(String text) {


		return JSONObject.parseObject(text);
	}

	/**
	 * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the
	 * hierarchy的方法
	 * 
	 * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
	 * 
	 * @param text
	 * @return
	 */
	public static JSONArray getJsonArray(String text) {

		return JSONArray.parseArray(text);
	}

	/**
	 * 解析JSON字符串成一个MAP
	 * 
	 * @param jsonStr
	 *            json字符串，格式如： {dictTable:"BM_XB",groupValue:"分组值"}
	 * @return
	 */
	public static Map<String, Object> parseJsonStrForMap(String jsonStr) {

		Map<String, Object> result = new HashMap<String, Object>();

		JSONObject jsonObj = JSONUtils.getJsonObject(jsonStr);

		for (Object key : jsonObj.keySet()) {
			result.put((String) key, jsonObj.get(key));
		}
		return result;
	}

	public static List<String> toJsonList(Collection<?> values) {
		if (values == null) {
			return null;
		}

		List<String> result = new ArrayList<String>();
		for (Object obj : values) {
			result.add(convertObjectToJson(obj));
		}
		return result;
	}

	public static <T> List<T> parseJsonList(List<String> list, Class<T> clazz) {
		if (list == null) {
			return null;
		}

		List<T> result = new ArrayList<T>();
		for (String s : list) {
			result.add(parseJsonToObject(s, clazz));
		}
		return result;
	}

	public static JSONObject urlToJSON(String url){
		JSONObject contextJSON = new JSONObject();
		String responseArray[] = url.split("&");

		Arrays.asList(responseArray).stream().forEach(param -> {
			String [] p = param.split("=");
			String key = p[0];
			if(p.length==2){
				String value = p[1];
				contextJSON.put(key,value);
			}
		});
		return contextJSON;
	}

}

package com.wonhigh.base.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * TODO: 仅用于发送聊天信息时拼接json，用LinkedHashMap代替HashMap解决key-value无序问题，其他场景不适应。
 * 
 * @author yang.dl
 * @date 2015-2-4 上午11:01:35
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class ChatMsgJSONObject extends JSONObject {

	private LinkedHashMap<Object, Object> mHashMap;

	public ChatMsgJSONObject() {
		mHashMap = new LinkedHashMap<Object, Object>();
	}

	@Override
	public JSONObject put(String name, boolean value) throws JSONException {
		// TODO Auto-generated method stub
		return put(name, value);
	}

	@Override
	public JSONObject put(String name, double value) throws JSONException {
		// TODO Auto-generated method stub
		return put(name, value);
	}

	@Override
	public JSONObject put(String name, int value) throws JSONException {
		// TODO Auto-generated method stub
		return put(name, value);
	}

	@Override
	public JSONObject put(String name, long value) throws JSONException {
		// TODO Auto-generated method stub
		return put(name, value);
	}

	public JSONObject put(String key, Object value) throws JSONException {
		if (key == null) {
			throw new JSONException("Null key.");
		}
		if (value != null) {
			testValidity(value);
			mHashMap.put(key, value);
		} else {
			remove(key);
		}
		return this;
	}

	public Object remove(String key) {
		return mHashMap.remove(key);
	}

	static void testValidity(Object o) throws JSONException {
		if (o != null) {
			if (o instanceof Double) {
				if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
					throw new JSONException("JSON does not allow non-finite numbers.");
				}
			} else if (o instanceof Float) {
				if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
					throw new JSONException("JSON does not allow non-finite numbers.");
				}
			}
		}
	}

	public String toString() {
		try {
			Iterator<Object> keys = mHashMap.keySet().iterator();
			StringBuffer sb = new StringBuffer("{");

			while (keys.hasNext()) {
				if (sb.length() > 1) {
					sb.append(',');
				}
				Object o = keys.next();
				sb.append(quote(o.toString()));
				sb.append(':');
				sb.append(valueToString(mHashMap.get(o)));
			}
			sb.append('}');
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	static String valueToString(Object value) throws JSONException {
		if (value == null || value.equals(null)) {
			return "null";
		}
		if (value instanceof JSONStringer) {
			Object o;
			try {
				o = ((JSONStringer) value).toString();
			} catch (Exception e) {
				throw new JSONException(e.getMessage());
			}
			if (o instanceof String) {
				return (String) o;
			}
			throw new JSONException("Bad value from toJSONString: " + o);
		}
		if (value instanceof Number) {
			return numberToString((Number) value);
		}
		if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
			return value.toString();
		}
		if (value instanceof Map) {
			return new JSONObject((Map) value).toString();
		}
		if (value instanceof Collection) {
			return new JSONArray((Collection) value).toString();
		}
		//		if (value.getClass().isArray()) {
		//			return new JSONArray(value).toString();
		//		}
		return quote(value.toString());
	}

}
/**
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
 */

package com.shawnhu.seagull.utils.JSONSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class JSONSerializer {
	private static boolean debugMode = false;

	public static <T extends JSONParcelable> T[] createArray(final JSONParcelable.Creator<T> creator,
			final JSONArray json) {
		if (creator == null) throw new NullPointerException("JSON_CREATOR must not be null!");
		if (json == null) return null;
		final int size = json.length();
		final T[] array = creator.newArray(size);
		for (int i = 0; i < size; i++) {
			array[i] = creator.createFromParcel(new JSONParcel(json.optJSONObject(i)));
		}
		return array;
	}

	public static <T extends JSONParcelable> ArrayList<T> createArrayList(final JSONParcelable.Creator<T> creator,
			final JSONArray json) {
		if (creator == null) throw new NullPointerException("JSON_CREATOR must not be null!");
		if (json == null) return null;
		final int size = json.length();
		final ArrayList<T> list = new ArrayList<T>(size);
		for (int i = 0; i < size; i++) {
			list.add(creator.createFromParcel(new JSONParcel(json.optJSONObject(i))));
		}
		return list;
	}

	public static <T extends JSONParcelable> T createObject(final JSONParcelable.Creator<T> creator,
			final JSONObject json) {
		if (creator == null) throw new NullPointerException("JSON_CREATOR must not be null!");
		if (json == null) return null;
		return creator.createFromParcel(new JSONParcel(json));
	}

	public static <T extends JSONParcelable> byte[] getByteArray(final T parcelable) {
		final JSONObject json = toJSONObject(parcelable);
		final String string = jsonToString(json);
		if (string == null) return null;
		return string.getBytes(Charset.defaultCharset());
	}

	public static <T extends JSONParcelable> byte[] getByteArray(final T[] array) {
		final JSONArray json = toJSONArray(array);
		final String string = jsonToString(json);
		if (string == null) return null;
		return string.getBytes(Charset.defaultCharset());
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static void setIsDebugMode(final boolean debug) {
		debugMode = debug;
	}

	public static <T extends JSONParcelable> byte[] toByteArray(final T parcelable) throws IOException {
		final String jsonString = jsonToString(toJSONObject(parcelable));
		if (jsonString == null) return null;
		return jsonString.getBytes(Charset.defaultCharset());
	}

	public static <T extends JSONParcelable> byte[] toByteArray(final T[] array) throws IOException {
		final String jsonString = jsonToString(toJSONArray(array));
		if (jsonString == null) return null;
		return jsonString.getBytes(Charset.defaultCharset());
	}

	public static <T extends JSONParcelable> JSONArray toJSONArray(final T[] array) {
		if (array == null) return null;
		final JSONArray json = new JSONArray();
		for (final T parcelable : array) {
			json.put(toJSONObject(parcelable));
		}
		return json;
	}

	public static <T extends JSONParcelable> String toJSONArrayString(final T[] array) {
		return jsonToString(toJSONArray(array));
	}

	public static <T extends JSONParcelable> JSONObject toJSONObject(final T parcelable) {
		if (parcelable == null) return null;
		final JSONObject json = new JSONObject();
		parcelable.writeToParcel(new JSONParcel(json));
		return json;
	}

	public static <T extends JSONParcelable> String toJSONObjectString(final T parcelable) {
		return jsonToString(toJSONObject(parcelable));
	}

	static String jsonToString(final JSONArray json) {
		if (json == null) return null;
		if (debugMode) {
			try {
				return json.toString(4);
			} catch (final JSONException e) {
				e.printStackTrace();
			}
			return json.toString();
		} else
			return json.toString();
	}

	static String jsonToString(final JSONObject json) {
		if (json == null) return null;
		if (debugMode) {
			try {
				return json.toString(4);
			} catch (final JSONException e) {
				e.printStackTrace();
			}
			return json.toString();
		} else
			return json.toString();
	}

}

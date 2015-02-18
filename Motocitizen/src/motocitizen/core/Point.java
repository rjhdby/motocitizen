package motocitizen.core;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Point {
	public Map<String, String> common;
	public Map<String, String> details;
	public int id;

	public Point() {
		this(new HashMap<String, String>());
	}

	public Point(Map<String, String> data) {
		common = new HashMap<String, String>();
		details = new HashMap<String, String>();
		Log.d("KEY:", String.valueOf(data.size()));
		for (String key : data.keySet()) {
			common.put(key, data.get(key));
		}
		try {
			id = Integer.parseInt(common.get("id"));
		} catch (NumberFormatException e) {
			id = 0;
		}
	}

	public String get(String key) {
		return common.get(key);
	}

	public void set(String key, String value) {
		if (common.containsKey(key)) {
			common.remove(key);
		}
		common.put(key, value);
	}

	public String getDet(String key) {
		return details.get(key);
	}

	public void setDet(String key, String value) {
		if (details.containsKey(key)) {
			details.remove(key);
		}
		details.put(key, value);
	}

	public int getId() {
		return id;
	}

	public String toString() {
		String result = "id:" + String.valueOf(id) + ",";
		for (String key : common.keySet()) {
			result += key + ":" + common.get(key) + ",";
		}
		return result.substring(0, result.length() - 1);
	}
}

/*
 * Свойства: id; timestamp; координаты; история изменения
 * координат/типа/подтипа/описания кто когда новое значение; создатель;
 * статус(норма, мг достаточно, отбой, конфликт); адрес строкой; тип(дтп,
 * поломка, прочее); подтип(в зависимости от типа); описание своими словами;
 * комментарии id кто когда текст; едут кто когда расстояние на месте кто когда
 * уехали кто когда Методы(публичные): создать; изменить статус(норма, мг
 * достаточно, отбой, конфликт); еду; создать сообщение; скрыть сообщение;
 * редактировать/удалить сообщение; переместить точку; откатить перенос точки;
 * изменить тип/подтип; изменить описание; изменить адрес; откатить изменение
 * типа/подтипа/описания/адреса;
 */
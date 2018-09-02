package com.weibo.api.api_test;

import net.sf.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class JsonParseTest {
	public static void main(String[] args) {
		try {
			while (true) {
				BufferedReader reader = new BufferedReader(new FileReader(new File("./src/main/resources/attitudes.txt")));
				String attitudes = reader.readLine();
				JSONArray jsonArray = JSONArray.fromObject(attitudes);
				Thread.sleep(100L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

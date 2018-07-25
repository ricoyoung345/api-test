package Proxy;

import java.util.ArrayList;
import java.util.List;

public class App {
	static class OOMObject {

	}

	public static void main (String[] args) {
		List<OOMObject> list = new ArrayList<OOMObject>();
		while (true) {
			list.add(new OOMObject());
		}
	}
}

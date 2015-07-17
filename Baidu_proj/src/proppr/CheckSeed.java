package proppr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CheckSeed {
	// <relations, seed>
	private static HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	private static Set<String> set = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		String seedFile = args[0];
		String cfactsFile = args[1];
		loadMap(seedFile);
		loadSet(cfactsFile);
		check();
	}

	public static void loadMap(String seedFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(seedFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (map.containsKey(tokens[1])) {
				ArrayList<String> list = map.get(tokens[1]);
				list.add(tokens[2]);
				map.put(tokens[1], list);
			}
			else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(tokens[2]);
				map.put(tokens[1], list);
			}
		}
		br.close();
	}

	public static void loadSet(String cfactsFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(cfactsFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			set.add(tokens[1]);
		}
		br.close();
	}

	public static void check() {

		for (ArrayList<String> list : map.values()) {
			boolean isOk = false;
			for (String pair : list) {
				if (set.contains(pair)) {
					isOk = true;
					break;
				}
			}
			if (!isOk) {
				System.out.print("wrong");
				return;
			}
		}
		System.out.print("right");
	}
}
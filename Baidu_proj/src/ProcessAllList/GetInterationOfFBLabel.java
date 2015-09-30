package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GetInterationOfFBLabel {
	private static HashSet<String> labelSet = new HashSet<>();
	private static HashMap<String, ArrayList<String>> seedMap = new HashMap<>();

	public static void main(String[] args) throws IOException {
		String freeBaseFile = args[0];
		String labelDocFile = args[1];
		String interactionFIle= args[2];
		loadLabeledDoc(freeBaseFile);
		loadFreeBaseDoc(labelDocFile);
		getAllSeedLine(interactionFIle);
	}

	public static void getAllSeedLine(String file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		for (String key : labelSet) {
			ArrayList<String> items = null;
			if ((items = seedMap.get(key)) != null) {
				for (String item : items) {
					bw.write(key + "\tQ0\t" + item + "\t0\t1\t0\n");
				}
			}
		}
		bw.close();
	}

	public static void loadLabeledDoc(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = br.readLine()) != null) {
			String pair = line.split("\t")[0];
			labelSet.add(pair);
		}
		br.close();
	}

	public static void loadFreeBaseDoc(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		// bw.write(key + "\tQ0\t" + item + "\t0\t1\t0\n");
		while ((line = br.readLine()) != null) {
			String[] token = line.split("\t");
			String key = token[0];
			String item = token[2];
			if (seedMap.containsKey(key)) {
				seedMap.get(key).add(item);
			}
			else {
				ArrayList<String> list = new ArrayList<>();
				list.add(item);
				seedMap.put(key, list);
			}
		}
		br.close();
	}
}

package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetFreeBaseAndDoc {
	private static HashMap<String, ArrayList<String>> labelMap = new HashMap<>();
	private static HashMap<String, ArrayList<String>> seedMap = new HashMap<>();

	public static void main(String[] args) throws IOException {
		String seedPath = args[0];
		String labelDocPath = args[1];
		String outputFile = args[2];
		loadSeedTriple(seedPath);
		loadLabeledDoc(labelDocPath);
		getAllSeedLine(outputFile);
		// getInteraction(outputFile);
	}

	public static void getAllSeedLine(String file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		for (String key : seedMap.keySet()) {
			ArrayList<String> items = seedMap.get(key);
			for (String item : items) {
				bw.write(key + "\tQ0\t" + item + "\t0\t1\t0\n");
			}
		}
		bw.close();
	}

	public static void getInteraction(String file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		for (String key : labelMap.keySet()) {
			if (seedMap.containsKey(key)) {
				ArrayList<String> items = seedMap.get(key);
				for (String item : items) {
					bw.write(key + "\tQ0\t" + item + "\t0\t1\t0\n");
				}
			}
		}
		bw.close();
	}

	public static void loadSeedTriple(String seedDoc) throws IOException {
		File folder = new File(seedDoc);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				String relation = tokens[1].replaceAll(" ", "_").toLowerCase();
				String key = parse(tokens[0]) + "_" + relation;
				String item = parse(tokens[2]);
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

	public static String parse(String input) {
		return input.trim().replaceAll("\\s+", "_").toLowerCase();
	}

	public static void loadLabeledDoc(String labelDoc) throws IOException {
		File folder = new File(labelDoc);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			String filename = file.getName();
			filename = filename.substring(0, filename.length() - 4);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim().replaceAll("\\s+", "_").toLowerCase();
				if (!line.equals("")) {
					if (labelMap.containsKey(filename)) {
						labelMap.get(filename).add(line);
					}
					else {
						ArrayList<String> list = new ArrayList<>();
						list.add(line);
						labelMap.put(filename, list);
					}
				}
			}
			br.close();
		}
	}
}

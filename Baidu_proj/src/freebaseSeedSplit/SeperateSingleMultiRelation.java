package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SeperateSingleMultiRelation {
	/*
	 * args[0] aggregate seed file args[1] single seed file args[2] multi seed
	 * file args[3] all relation file with "," split.
	 */
	public static void main(String[] args) throws IOException {
		String basePath = args[2];
		HashMap<String, ArrayList<String>> all = new HashMap<String, ArrayList<String>>();
		HashMap<String, BufferedWriter> outFileList = new HashMap<String, BufferedWriter>();
		System.out.println(args[1]);
		String[] relations = args[1].split(",");
		for (String relation : relations) {
			outFileList.put(relation + "_single", new BufferedWriter(new FileWriter(basePath + relation + "_single")));
			outFileList.put(relation + "_multi", new BufferedWriter(new FileWriter(basePath + relation + "_multi")));

		}
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String key = tokens[2];
			if (all.containsKey(key)) {
				all.get(key).add(line);
			}
			else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(line);
				all.put(key, list);
			}
		}
		br.close();
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(basePath + "all_single"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(basePath + "all_multi"));
		for (ArrayList<String> list : all.values()) {
			if (list.size() == 1) {
				String seed = list.get(0);
				String rel = seed.split("\t")[1];
				outFileList.get(rel + "_single").write(seed + "\n");
				bw1.write(seed + "\n");
			}
			else {
				for (String seed : list) {
					String rel = seed.split("\t")[1];
					outFileList.get(rel + "_multi").write(seed + "\n");
					bw2.write(seed + "\n");
				}
			}
		}
		bw1.close();
		bw2.close();
		for (BufferedWriter buf : outFileList.values()) {
			buf.close();
		}
	}
}

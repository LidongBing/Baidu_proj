package freebase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RelationFreq {

	public static void main(String[] args) throws IOException {
		HashMap<String, Integer> count = new HashMap<>();
		String factPath = "/remote/curtis/baidu/mingyanl/freebase/freebase-easy-14-04-14/facts.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter("frequency"));
		BufferedReader br = new BufferedReader(new FileReader(factPath));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens.length == 4) {
				String key = tokens[1];
				if (count.containsKey(key)) {
					count.put(key, count.get(key) + 1);
				}
				else {
					count.put(key, 1);
				}
			}
		}
		br.close();
		for (Map.Entry<String, Integer> entry : count.entrySet()) {
			bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		bw.flush();
		bw.close();
	}

}

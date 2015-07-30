package FreebaseWikipediaPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public class RerankFBCounts {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		HashMap<String, ArrayList<String>> predMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, Integer> predCnt = new HashMap<String, Integer>();

		String line = null;
		ArrayList<String> list = null;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("\t")) {
				list = new ArrayList<String>();
				String key = line.split("\\t")[0];
				int value = Integer.parseInt(line.split("\\t")[1]);
				predCnt.put(key, value);
				predMap.put(key, list);

			} else {
				list.add(line);
			}
		}

		ArrayList<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
		entries.addAll(predCnt.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		for (Entry<String, Integer> entry : entries) {
			bw.write(entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
			for (String exmp : predMap.get(entry.getKey())) {
				bw.write("\t" + exmp);
				bw.newLine();
			}
		}

		bw.close();
		br.close();
	}
}

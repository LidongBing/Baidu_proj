package FreebaseWikipediaPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/*
 * Count unique predicates in the triples of freebase.
 * Give each predict three examples
 */
public class FBPredicatesCounter {

	public static HashMap<String, ArrayList<String>> predMap = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, Integer> predCnt = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.out
					.println("ERROR: please give the gzFreebaseTripleDump, and outfile.");
			System.exit(0);
		}

		String inputGZ = args[0];
		InputStream fileStream = new FileInputStream(inputGZ);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		long cnt = 0;
		String line = null;
		while ((line = buffered.readLine()) != null) {
			String[] toks = line.split("\\t+");
			String predicate = toks[1];
			ArrayList<String> exmps = predMap.get(predicate);
			if (exmps == null) {
				exmps = new ArrayList<String>();
				exmps.add(line);
				predMap.put(predicate, exmps);
				predCnt.put(predicate, 1);
			} else {
				if (exmps.size() < 3)
					exmps.add(line);
				predCnt.put(predicate, predCnt.get(predicate) + 1);
			}

			cnt++;
			if (cnt % 10000000 == 0)
				System.out.println(cnt + " triples processed");
		}

		buffered.close();

		ArrayList<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
		entries.addAll(predCnt.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		for (Entry<String, Integer> entry : entries) {
			bw.write(entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
			for (String exmp : predMap.get(entry.getKey())) {
				bw.write("\t" + exmp);
				bw.newLine();
			}
		}

		bw.close();

	}
}

package FreebaseWikipediaPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class GetSpecificFBSeeds {

	public static HashMap<String, String> nameMap;
	public static HashMap<String, String> predicateMap;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 4) {
			System.out
					.println("ERROR: please give four parameters: gzFreebaseTripleDump, rdfNameMap, usefulPredicates and outfileDir.");
			System.exit(0);
		}

		String inputGZ = args[0];
		InputStream fileStream = new FileInputStream(inputGZ);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);

		nameMap = loadTwoColumnMap(args[1]);
		predicateMap = loadTwoColumnMap(args[2]);

		ArrayList<String> preds = new ArrayList<String>();
		preds.addAll(predicateMap.keySet());
		BufferedWriter[] bws = new BufferedWriter[preds.size()];
		for (int i = 0; i < preds.size(); i++) {
			String fileName = preds.get(i).substring(
					preds.get(i).indexOf("medicine.") + "medicine.".length(),
					preds.get(i).length() - 1);
			bws[i] = new BufferedWriter(
					new FileWriter(args[3] + "/" + fileName));
		}

		long cnt = 0;
		String line = null;
		while ((line = buffered.readLine()) != null) {

			String[] toks = line.split("\\t+");
			String subTok = toks[0];
			String predicate = toks[1];
			String objTok = toks[2];
			if (predicateMap.containsKey(predicate)) {
				String subName = nameMap.get(subTok);
				String objName = nameMap.get(objTok);
				if (subName != null && objName != null) {
					subName.replaceAll("\"", "");
					objName.replaceAll("\"", "");
					bws[preds.indexOf(predicate)].write(subName + "\t"
							+ objName);
					bws[preds.indexOf(predicate)].newLine();
					bws[preds.indexOf(predicate)].flush();
				}
			}

			cnt++;
			if (cnt % 10000000 == 0)
				System.out.println(cnt + " triples processed");
		}

		for (int i = 0; i < preds.size(); i++) {
			bws[i].close();
		}
		buffered.close();
	}

	public static HashMap<String, String> loadTwoColumnMap(String inputfile)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		HashMap<String, String> ret = new HashMap<String, String>();

		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().length() > 0)
				ret.put(line.split("\\t+")[0], line.split("\\t+")[1]);
		}

		br.close();
		return ret;
	}
}

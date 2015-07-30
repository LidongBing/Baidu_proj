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

public class GetSpecificTriples {

	public static HashMap<String, String> predicateMap;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out
					.println("ERROR: please give three parameters: gzFreebaseTripleDump, usefulPredicates and outfileDir.");
			System.exit(0);
		}

		String inputGZ = args[0];
		InputStream fileStream = new FileInputStream(inputGZ);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);

		predicateMap = loadTwoColumnMap(args[1]);

		ArrayList<String> preds = new ArrayList<String>();
		preds.addAll(predicateMap.keySet());
		BufferedWriter[] bws = new BufferedWriter[preds.size()];
		for (int i = 0; i < preds.size(); i++) {
			String fileName = preds.get(i).substring(
					preds.get(i).indexOf("medicine.") + "medicine.".length(),
					preds.get(i).indexOf('>'))
					+ ".RDF_Triple";
			bws[i] = new BufferedWriter(
					new FileWriter(args[2] + "/" + fileName));
		}

		long cnt = 0;
		String line = null;
		while ((line = buffered.readLine()) != null) {

			String[] toks = line.split("\\t+");
			String predicate = toks[1];
			if (predicateMap.containsKey(predicate)) {
				bws[preds.indexOf(predicate)].write(line);
				bws[preds.indexOf(predicate)].newLine();
				// bws[preds.indexOf(predicate)].flush();
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
				ret.put(line.split("\\t+")[0].trim(),
						line.split("\\t+")[1].trim());
		}

		br.close();
		return ret;
	}
}

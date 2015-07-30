package FreebaseWikipediaPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/*
 * convert the triples in RDF format to text format
 */
public class RDFTripleToTextTriple {

	public static HashMap<String, String> nameMap;
	public static HashMap<String, String> predicateMap;

	public static String currentPredicate = null;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//
		// File testDir = new File("data");
		// System.out.println(testDir.getName());

		if (args.length != 3) {
			System.out
					.println("ERROR: please give three parameters: rdfNameMap, rdfFileDir and outfileDir.");
			System.out.println("   OR: give rdfNameMap, rdfFile and outfile.");
			System.out
					.println("   Note: each input file of the RDF relation should be named with the predicate");
			System.exit(0);
		}
		nameMap = loadTwoColumnMap(args[0]);

		File rdfDir = new File(args[1]);

		if (rdfDir.isDirectory()) {

			File[] rdfFiles = rdfDir.listFiles();
			for (int i = 0; i < rdfFiles.length; i++) {
				if (rdfFiles[i].getName().endsWith("RDF_triple")) {
					String infile = args[1] + "/" + rdfFiles[i].getName();
					String outfile = args[2] + "/" + rdfFiles[i].getName()
							+ ".txt";
					currentPredicate = rdfFiles[i].getName().replace(
							".RDF_triple", "");
					processOne(infile, outfile);
				}
			}
		} else {
			processOne(args[1], args[2]);
		}

	}

	public static void processOne(String infile, String outfile)
			throws IOException {
		String line = null;
		String[] toks = null;
		String subRDF = null;
		String predRDF = null;
		String objRDF = null;
		String subTxt = null;
		String predTxt = null;
		String objTxt = null;
		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		line = null;
		while ((line = br.readLine()) != null) {
			toks = line.split("\\t+");
			if (toks.length < 3)
				continue;

			subRDF = toks[0].trim();
			predRDF = toks[1].trim();
			objRDF = toks[2].trim();

			if (!predRDF.contains(currentPredicate))
				continue;

			subTxt = nameMap.get(subRDF);
			predTxt = nameMap.get(predRDF);
			objTxt = nameMap.get(objRDF);
			if (subTxt != null && predTxt != null && objTxt != null) {
				bw.write(subTxt + "\t" + predTxt + "\t" + objTxt);
				bw.newLine();
			}
		}

		br.close();
		bw.close();

	}

	public static HashMap<String, String> loadTwoColumnMap(String inputfile)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		HashMap<String, String> ret = new HashMap<String, String>();

		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().length() > 0 && line.trim().endsWith("@en"))
				ret.put(line.split("\\t+")[0].trim(), line.split("\\t+")[1]
						.trim().replace("@en", "").replace("\"", ""));
		}

		br.close();
		return ret;
	}
}

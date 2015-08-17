package PropprSSLListEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Format the solution example in the format: listID <TAB> predict-labelID <TAB> score
 * to the prediction file of a gold test file.
 * 
 * Gold test file format: listID <TAB> true-labelID
 * Prediction file format: listID <TAB> predict-labelID, 
 * listIDs are the ones in the gold test file.
 */
public class SolutionToTestPredict {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out
					.println("ERROR: please give three args: solutionExample, goldTestPrediction, and outputFile");
			System.exit(0);
		}
		ArrayList<Triple> predList = loadPredictions(args[0]);
		HashMap<String, Triple> predMap = solveMultiClassPredicts(predList);

		ArrayList<Triple> goldTest = loadGold(args[1]);
		processOnePredict(predMap, goldTest, args[2]);

	}

	public static void processOnePredict(HashMap<String, Triple> predMap,
			ArrayList<Triple> goldTest, String outFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

		for (Triple gold : goldTest) {
			String predLabel = "";
			if (!predMap.containsKey(gold.ID)) {
				predLabel = "-1";
			} else {
				predLabel = predMap.get(gold.ID).label;
			}
			bw.write(gold.ID + "\t" + predLabel);
			bw.newLine();
		}

		bw.close();
	}

	public static ArrayList<Triple> loadGold(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		ArrayList<Triple> retList = new ArrayList<Triple>();
		while (line != null) {
			String[] toks = line.split("\\t");
			retList.add(new Triple(toks[0], toks[1], 1));
			line = br.readLine();
		}
		br.close();

		return retList;
	}

	public static ArrayList<Triple> loadPredictions(String file)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		ArrayList<Triple> retList = new ArrayList<Triple>();
		while (line != null) {
			String[] toks = line.split("\\t");
			retList.add(new Triple(toks[0], toks[1], Double
					.parseDouble(toks[2])));
			line = br.readLine();
		}
		br.close();

		return retList;
	}

	public static HashMap<String, Triple> solveMultiClassPredicts(
			ArrayList<Triple> preds) {
		HashMap<String, Triple> retmap = new HashMap<String, Triple>();

		for (Triple tri : preds) {
			if (!retmap.containsKey(tri.ID)) {
				retmap.put(tri.ID, tri);
			} else {
				if (retmap.get(tri.ID).score < tri.score) {
					retmap.put(tri.ID, tri);
				}
			}
		}

		return retmap;
	}
}

class Triple {
	public String ID;
	public String label;
	public double score;

	public Triple(String id, String lbl, double s) {
		this.ID = id;
		this.label = lbl;
		this.score = s;

	}
}
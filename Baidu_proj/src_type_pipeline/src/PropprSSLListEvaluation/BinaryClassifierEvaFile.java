package PropprSSLListEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Get binary classifier evaluation files for evaluating
 * the performance of ProPPR on the top 500 solution lists of a 
 * 10% FB seeds.
 * 
 * Input: Predict file: listID <TAB> label
 * 		Gold test file: listID <TAB> label
 * 
 * Oupput:
 * Bianry gold test file: listID <TAB> 1
 * 					listID <TAB> 1
 * 					listID <TAB> -1
 * 
 * Binary predict file has the same format
 */
public class BinaryClassifierEvaFile {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 5) {
			System.out
					.println("ERROR: please give three args: predictFile, goldPredFile, label, outPredBinaryFile and outGoldBinaryFile");
			System.exit(0);
		}
		ArrayList<Triple> preds = loadPredFile(args[0]);
		ArrayList<Triple> golds = loadPredFile(args[1]);

		processOne(preds, golds, args[2], args[3], args[4]);

	}

	public static void processOne(ArrayList<Triple> preds,
			ArrayList<Triple> golds, String label, String outPredBinaryFile,
			String outGoldBinaryFile) throws IOException {

		BufferedWriter bwPred = new BufferedWriter(new FileWriter(
				outPredBinaryFile));
		BufferedWriter bwGold = new BufferedWriter(new FileWriter(
				outGoldBinaryFile));

		for (int i = 0; i < preds.size(); i++) {
			if (!preds.get(i).ID.equals(golds.get(i).ID)) {
				System.out
						.println("ERROR: redictFile and goldPredFile are mismached at line: "
								+ i);
				System.exit(0);
			}
			String tmpLabelPred = "";
			String tmpLabelGold = "";
			if (preds.get(i).label.equals(label))
				tmpLabelPred = "1";
			else
				tmpLabelPred = "-1";

			if (golds.get(i).label.equals(label))
				tmpLabelGold = "1";
			else
				tmpLabelGold = "-1";

			bwPred.write(tmpLabelPred);
			bwPred.newLine();

			bwGold.write(tmpLabelGold);
			bwGold.newLine();

		}

		bwPred.close();
		bwGold.close();
	}

	public static ArrayList<Triple> loadPredFile(String file)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		ArrayList<Triple> retList = new ArrayList<Triple>();
		while (line != null) {
			String[] toks = line.split("\\t");
			retList.add(new Triple(toks[0], toks[1], 0.0));
			line = br.readLine();
		}
		br.close();

		return retList;
	}
}

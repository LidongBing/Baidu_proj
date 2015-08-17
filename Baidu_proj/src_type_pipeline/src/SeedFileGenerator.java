
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SeedFileGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] types = { "ingredient", "drug", "disease", "symptom" };
		double[] percentage = { 0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
				0.8, 0.9, 0.99 };
		HashMap<String, String> freebaseInstanceFile = new HashMap<String, String>();
		freebaseInstanceFile
				.put(types[0],
						"C:\\Users\\User\\Desktop\\WorkspaceJ\\FreeBaseEasy\\data\\instanceOf_Drug-ingredient.txt");
		freebaseInstanceFile
				.put(types[1],
						"C:\\Users\\User\\Desktop\\WorkspaceJ\\FreeBaseEasy\\data\\instanceOf_Drug.txt");
		freebaseInstanceFile
				.put(types[2],
						"C:\\Users\\User\\Desktop\\WorkspaceJ\\FreeBaseEasy\\data\\instanceOf_Disease_is-a-Disease-or-medical-condition.txt");
		freebaseInstanceFile
				.put(types[3],
						"C:\\Users\\User\\Desktop\\WorkspaceJ\\FreeBaseEasy\\data\\instanceOf_Symptom.txt");

		for (double perc : percentage) {
			for (String type : types) {
				generate(type, freebaseInstanceFile.get(type), perc,
						"data\\seed\\" + type + "_FB_seed_" + (int) (100 * perc)
								+ "p", "data\\seed\\" + type + "_FB_eva_"
								+ (int) (100 * perc) + "p");
			}
		}
		// if (args.length != 3) {
		// System.out
		// .println("ERROR: Please give three args, seed type, input file, and output file");
		// System.exit(0);
		// }
		// generate(args[0], args[1], args[2]);
	}

	public static void generate(String type, String infileName,
			double percetage, String outfileName, String freebaseEvaSet)
			throws IOException {
		BufferedWriter bwSeed = new BufferedWriter(new FileWriter(outfileName));
		BufferedWriter bwEva = new BufferedWriter(
				new FileWriter(freebaseEvaSet));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

		int cnt = 0; // count from 1 to 100

		// boolean isSeed = true;
		String line = br.readLine();
		while (line != null) {
			if (line.indexOf('(') != -1) {
				line = line.substring(0, line.indexOf('(')).trim();
			}

			if (line.length() > 60 && type.equals("drug")) {

				line = br.readLine();
				continue;
			}
			if (line.length() > 60 && type.equals("ingredient")) {

				line = br.readLine();
				continue;
			}
			if (line.length() > 30 && type.equals("symptom")) {

				line = br.readLine();
				continue;
			}
			if (line.length() > 40 && type.equals("disease")) {

				line = br.readLine();
				continue;
			}
			if (line.indexOf(',') != -1) {

				line = br.readLine();
				continue;
			}

			if (line.indexOf('/') != -1) {

				line = br.readLine();
				continue;
			}

			if (cnt < percetage * 100) {
				if (line.length() > 0) {
					bwSeed.write("seed\t" + type + "\t"
							+ line.replaceAll(" ", "_").toLowerCase());
					bwSeed.newLine();
				}
				cnt++;
			} else {
				if (line.length() > 0) {
					bwEva.write(line.toLowerCase());
					bwEva.newLine();
				}
				cnt++;
			}
			if (cnt == 100) {
				cnt = 0;
			}
			line = br.readLine();
		}

		bwSeed.close();
		bwEva.close();
		br.close();
	}

}

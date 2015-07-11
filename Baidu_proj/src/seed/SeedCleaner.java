package seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Clean the seeds that may be noise, also generate the seed files that only contain single class seeds
 */
public class SeedCleaner {

	public static HashMap<String, Integer> seedFreMap = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] types = { "ingredient", "drug", "disease", "symptom" };
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

		for (String type : types) {
			clean(type, freebaseInstanceFile.get(type), "data\\cleanedSeed\\"
					+ type + "_FB_seed");
		}
		for (String key : seedFreMap.keySet()) {
			if (seedFreMap.get(key) > 1)
				System.out.println(key + "\t" + seedFreMap.get(key));
		}
		for (String type : types) {
			getSingleClassList(type, "data\\cleanedSeed\\" + type + "_FB_seed",
					"data\\singleClassSeeds\\" + type + "_FB_seed_singleClass");
		}
	}

	public static void clean(String type, String infileName, String outfileName)
			throws IOException {
		BufferedWriter bwSeed = new BufferedWriter(new FileWriter(outfileName));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

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

			line = line.toLowerCase();
			if (line.length() > 0) {
				bwSeed.write(line);
				bwSeed.newLine();
				if (!seedFreMap.containsKey(line)) {
					seedFreMap.put(line, 0);
				}

				seedFreMap.put(line, seedFreMap.get(line) + 1);
			}
			line = br.readLine();
		}

		bwSeed.close();
		br.close();
	}

	public static void getSingleClassList(String type, String infileName,
			String outfileName) throws IOException {
		BufferedWriter bwSeed = new BufferedWriter(new FileWriter(outfileName));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

		// boolean isSeed = true;
		String line = br.readLine();
		while (line != null) {
			line = line.toLowerCase();

			if (seedFreMap.get(line) == 1) {
				bwSeed.write(line);
				bwSeed.newLine();
			}

			line = br.readLine();
		}

		bwSeed.close();
		br.close();

	}
}

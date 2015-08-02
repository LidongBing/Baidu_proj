package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class CheckSeedInCfactsFile {
	// <relations, seed>
	private static ArrayList<String> list = new ArrayList<>();
	private static HashSet<String> set = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		String seedFile = args[0];
		String cfactsFile = args[1];
		String validSeedFile=args[2];
		loadMap(seedFile);
		loadSet(cfactsFile);
		check(validSeedFile);
	}

	public static void loadMap(String seedFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(seedFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
	}

	public static void loadSet(String cfactsFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(cfactsFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			set.add(tokens[2]);
		}
		br.close();
	}

	public static void check(String validSeedFile) throws IOException {
		BufferedWriter bw =new BufferedWriter(new FileWriter(validSeedFile));
		int totalCount = 0;
		int exist = 0;
		for (String line : list) {
			totalCount++;
			String[] tokens= line.split("\t");
			String pair=tokens[2];
			if (set.contains(pair)) {
				exist++;
				bw.write(line+"\n");
			}
		}
		bw.close();
		System.out.println("\ntotalCount: " + totalCount + "\nexist count:" + exist);
	}
}

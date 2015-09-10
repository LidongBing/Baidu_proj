package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;


public class FormatFreeBaseAndLabelDoc {

	public static void main(String[] args) throws IOException {
		String seedPath = args[0];
		String labelDocPath = args[1];
		String freeBaseFile = args[2];
		String labelDocFile = args[3];

		loadLabeledDoc(labelDocPath,labelDocFile);
		loadFreeBaseDoc(seedPath,freeBaseFile);

	}
	public static void loadFreeBaseDoc(String seedPath,String freeBaseFile) throws IOException{
		File folder = new File(seedPath);
		File[] listOfFiles = folder.listFiles();
		BufferedWriter bw = new BufferedWriter(new FileWriter(freeBaseFile));
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				String relation = tokens[1].replaceAll(" ", "_").toLowerCase();
				String key = parse(tokens[0]) + "_" + relation;
				String item = parse(tokens[2]);
				bw.write(key + "\tQ0\t" + item + "\t0\t1\t0\n");
			}
			br.close();
		}
		bw.close();
	}
	public static String parse(String input) {
		return input.trim().replaceAll("\\s+", "_").toLowerCase();
	}
	public static void loadLabeledDoc(String labelPath,String labelFile) throws IOException{
		File folder = new File(labelPath);
		File[] listOfFiles = folder.listFiles();
		HashSet<String> set = new HashSet<>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(labelFile));
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			String filename = file.getName();
			filename = filename.substring(0, filename.length() - 4);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim().replaceAll("\\s+", "_").toLowerCase();
				if (!line.equals("")) {
					set.add(filename + "\tQ0\t" + line + "\t1\n");
				}
			}
			br.close();
		}
		for (String string : set) {
			bw.write(string);
		}
		bw.close();
	}

}

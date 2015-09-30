package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class MarkedResult {

	public static void main(String[] args) throws IOException {
		String path = args[0];
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		HashSet<String> set = new HashSet<>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
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

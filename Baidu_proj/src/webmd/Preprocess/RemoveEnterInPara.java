package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RemoveEnterInPara {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 2) {
			System.out
					.println("ERROR: please give the inputfile and outputfile name.");
			System.exit(0);
		}
		processFile(args[0], args[1]);
	}

	public static void processFile(String infile, String outfile)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		StringBuffer sb = new StringBuffer();
		String line = getBlock(br, sb);

		while (line != null) {
			bw.write(sb.toString());
			bw.newLine();
			sb = new StringBuffer();
			line = getBlock(br, sb);
		}

		bw.write(sb.toString());
		bw.newLine();
		bw.close();
		br.close();

	}

	public static String getBlock(BufferedReader br, StringBuffer sb)
			throws IOException {

		String line = br.readLine();
		while (line != null && line.trim().length() != 0) {
			sb.append(line);
			line = br.readLine();
		}
		return line;
	}

}

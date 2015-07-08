package freebase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractRelationSeed {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String factPath = "/remote/curtis/baidu/mingyanl/freebase/freebase-easy-14-04-14/facts.txt";
		String relativeOutPath = "../out/";
		BufferedWriter bw = new BufferedWriter(new FileWriter(relativeOutPath + args[0].replaceAll(" ", "_")));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		System.out.println(args[0]);
		while ((line = stdInput.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens[1].equals(args[0])) {
				System.out.println(line);
				bw.write(tokens[0] + "\t" + tokens[2]+"\n");
			}
		}
		bw.flush();
		bw.close();
	}
}

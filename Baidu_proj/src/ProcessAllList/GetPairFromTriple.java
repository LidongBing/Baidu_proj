package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class GetPairFromTriple {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		String line= null;
		while((line=br.readLine())!=null){
			String[] token = line.split("\t");
			bw.write("triple\t1\t"+token[1]+"@"+token[2]+"\n");
		}
		bw.close();
		br.close();
	}
}

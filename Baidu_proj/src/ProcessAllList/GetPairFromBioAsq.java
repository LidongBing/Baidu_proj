package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GetPairFromBioAsq {
	public static String REG_EXP = "\\+query\\(.*?\\)";
	public static Pattern PTN = Pattern.compile(REG_EXP);

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		String line= null;
		while((line=br.readLine())!=null){
			Matcher m = PTN.matcher(line);
			while (m.find()) {
				String matchResult=m.group();
				System.out.println(matchResult);
				String[] token=matchResult.split(",");
				bw.write("bioasq\t1\t" + token[1]+"@"+token[2].substring(0,token[2].length()-1)+"\n");
			}
		}
		bw.close();
		br.close();
	}

}

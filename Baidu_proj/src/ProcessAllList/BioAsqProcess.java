package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BioAsqProcess {
	private static ArrayList<String> list = new ArrayList<>(Arrays.asList("conditions_this_may_prevent", "side_effects", "used_to_treat", "treatments", "symptoms", "risk_factors", "causes", "prevention_factors"));

	public static void main(String[] args) throws IOException {
		String inputFile = args[0];
		String outputFile= args[1];
		BufferedReader br = new BufferedReader (new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String line = null;
		while((line=br.readLine())!=null){
			String output=caseJudge(line);
			bw.write(output+"\n");
		}
		br.close();
		bw.close();
	}

	public static String caseJudge(String line) {
		String[] token = line.split("\t");
		String pair = token[0];
		String item = token[2];
		String object = null;
		String subject = null;
		String relation = null;
		for (String key : list) {
			if (pair.endsWith(key)) {
				if (key.equals("conditions_this_may_prevent") || key.equals("used_to_treat")) {
					object = pair.replace("_" + key, "");
					subject = item;
				}
				else {
					subject = pair.replace("_" + key, "");
					object = item;
				}
				relation = search(key);
				break;
			}
		}
		if (subject == null)
			System.out.println("wrong");
		return relation + "\t" + object + "\t" + subject;
	}

	public static String search(String relation) {
		String rst = null;
		switch (relation) {
		case "conditions_this_may_prevent":
			rst = "condition_this_may_prevent";
			break;
		case "side_effects":
			rst = "side_effect_of";
			break;
		case "used_to_treat":
			rst = "used_to_treat";
			break;
		case "treatments":
			rst = "used_to_treat";
			break;
		case "symptoms":
			rst = "symptom_of";
			break;
		case "risk_factors":
			rst = "disease_with_this_risk_factor";
			break;
		case "causes":
			rst = "disease_or_condition_caused";
			break;
		case "prevention_factors":
			rst = "condition_this_may_prevent";
			break;
		default:
			System.out.println("worngs");
		}
		return rst;
	}
}
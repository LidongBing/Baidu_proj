package buildUpNLMXml;

public class StringUtil {
	// for remove number before title
	public static String getOriginalTitle(String name) {
		int i = 0;
		String tmp = name.trim();
		while (i < tmp.length()) {
			if (Character.isLetter(tmp.charAt(i))) {
				break;
			}
			else {
				i++;
			}
		}
		return tmp.substring(i).replaceAll("\\s+", " ").trim();
	}
	
	//parse title with ' ' to '_' and remove other unnecessary character.
	public static String getParseTitle(String name) {
		String tmp = name.toLowerCase().trim();
		// int index=0;
		// if((index=tmp.indexOf(":"))!=-1
		// || (index=tmp.indexOf(","))!=-1
		// || (index=tmp.indexOf(";"))!=-1
		// || (index=tmp.indexOf("("))!=-1
		// || (index=tmp.indexOf("["))!=-1
		// || (index=tmp.indexOf("."))!=-1
		// || (index=tmp.indexOf("{"))!=-1
		// || (index=tmp.indexOf("\""))!=-1
		// || (index=tmp.indexOf("\'"))!=-1
		// || (index=tmp.indexOf(" - "))!=-1
		// ){
		// tmp=tmp.substring(0,index);
		// }
		tmp = tmp.split("[^\\w\\s-]|_", 2)[0];
		return tmp.replaceAll("[^a-zA-Z]+", " ").trim().replaceAll(" ", "_");
	}

	public static String trimText(String input) {
		String result= input.replaceAll("\u00A0"," ").replaceAll("\\s+", " ").trim();
		if(result.matches(".*?[.?!]$")){
			result+="\n";
		}
		return result;
	}
	public static String multipleTrmText(String input){
		String[] sentences = input.replaceAll("\u00A0"," ").trim().split("\n");
		String result = "";
		for (String sentence : sentences) {
			String trim = sentence.trim();
			if (!trim.equals("")) {
				result += trim;
				if (trim.matches(".*?[,.?!]$")) {
					result += "\n";
				}
				else {
					result += " ";
				}
			}
		}
		return result.trim().replaceAll("\\s+(?=[^\\[\\]]*\\])", " ").replaceAll("\\s+(?=[^()]*\\))", " ").trim();

	}
}

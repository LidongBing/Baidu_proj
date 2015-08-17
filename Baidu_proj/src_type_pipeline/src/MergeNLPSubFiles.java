import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/*
 * Merge the subfiles from NLP pipeline.
 * 
 * Remove the repeated sentences. Although the files are processed to remove duplicated drugs,
 * there are still repeated sentences.
 * 
 * In fact this should be done before the NLP pipeline. However, if NLP is already done, 
 * just filtering the NLP output in the format:
 1	FILENAME	FILENAME	B-NP	CD	O	3	NMOD
 2	:	:	O	:	O	1	P
 3	c4bb08f5-a0dd-4302-b886-cc1cd04ca984.xml	c4bb08f5-a0dd-4302-b886-cc1cd04ca984.xml	B-NP	NN	O	0	ROOT

 1	Warnings	Warning	B-NP	NNS	O	8	NMOD
 2	and	and	I-NP	CC	O	3	NMOD
 3	Precautions	Precaution	I-NP	NNS	O	8	NMOD
 .....
 1	Valacyclovir	Valacyclovir	B-NP	NN	O	4	NMOD
 2	hydrochloride	hydrochloride	I-NP	NN	O	4	NMOD
 */

public class MergeNLPSubFiles {

	public static HashSet<Long> uniqHashCode = new HashSet<Long>();
	public static boolean finished = false;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out
					.println("ERROR: please give 3 params: subFolderDir, subFolderNo, and outputFile");
			System.exit(0);
		}

		String indir = args[0];
		// String dir = "";
		int subFileNo = Integer.parseInt(args[1]);
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[2]));

		String infile = "";
		for (int i = 0; i < subFileNo; i++) {
			int fileId = i + 1;
			infile = indir + "/" + fileId
					+ "/paragraph_ss_filter_parse.txt.part" + fileId;
			processOneFile(infile, bw);
		}
		bw.close();

	}

	public static void processOneFile(String infile, BufferedWriter bw)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));
		finished = false;

		while (!finished) {
			String blockSent = getOneBlockSent(br);
			String sentFirstHalf = blockSent.substring(0,
					blockSent.length() / 2);
			String sentSecondHalf = blockSent.substring(blockSent.length() / 2,
					blockSent.length());

			long sentCode = sentFirstHalf.hashCode();
			sentCode = (sentCode << Integer.SIZE);
			sentCode += sentSecondHalf.hashCode();
			if (!uniqHashCode.contains(sentCode)) {
				uniqHashCode.add(sentCode);
				bw.write(blockSent);
				bw.newLine();
			}
		}
		br.close();
	}

	public static String getOneBlockSent(BufferedReader br) throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = br.readLine();

		while (line != null && line.length() != 0) {
			sb.append(line);
			sb.append("\n");
			line = br.readLine();
		}

		if (line == null) {
			finished = true;
		}

		return sb.toString();
	}
}

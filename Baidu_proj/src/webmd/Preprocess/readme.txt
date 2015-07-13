1. remove the enters in each paragraph from
   input file: webmd_export-2015-06-08T19-34-11.xml
   get output: webmd_export-2015-06-08T19-34-11_re.xml
   Code: webmd.Preprocess.RemoveEnterInPara

2. clear the pattern [anchor](URL) to anchor, and save a file for "anchor <TAB> URL"
   input file: webmd_export-2015-06-08T19-34-11_re.xml
   get output: webmd_export-2015-06-08T19-34-11_re_clean.xml and webmd_export-2015-06-08T19-34-11_anchor_URL.txt
   Code: webmd.Preprocess.ParaCleaner

3. split the sentences in webmd_export-2015-06-08T19-34-11_re_clean.xml with Genia SS.
   input file: webmd_export-2015-06-08T19-34-11_re_clean.xml
   get output: webmd_export-2015-06-08T19-34-11_re_clean_ss.xml
   Code: in folder /remote/curtis/baidu/package/genia-nlp/geniass RUN ./geniass ../../../webmd-processed/webmd_export-2015-06-08T19-34-11_re_clean.xml ../../../webmd-processed/webmd_export-2015-06-08T19-34-11_re_clean_ss.xml

4. give each sentence a unique code, so as to avoiding processing one sentence many times with the GDep.
   E.G.:this sentence "Do not start, stop, or change the dosage of anymedicines without your doctor's approval." occurs 5358 times
   input file: webmd_export-2015-06-08T19-34-11_re_clean_ss.xml
   get output: webmd_export-2015-06-08T19-34-11_re_clean_ss_code.xml webmd_export-2015-06-08T19-34-11_sent_code.txt
   Code: java webmd.Preprocess.UniqueSentence webmd_export-2015-06-08T19-34-11_re_clean_ss.xml webmd_export-2015-06-08T19-34-11_re_clean_ss_code.xml webmd_export-2015-06-08T19-34-11_sent_code.txt

5. conduct dependency parsing with Genia Dependency Parser
   input file: webmd_export-2015-06-08T19-34-11_sent_code.txt
   get output: webmd_export-2015-06-08T19-34-11_sent_code_parsed.txt
   Code: in folder /remote/curtis/baidu/package/genia-nlp/gdep-beta2/ RUN ./gdep $current_path/webmd_export-2015-06-08T19-34-11_sent_code.txt > $current_path/webmd_export-2015-06-08T19-34-11_sent_code_parsed.txt
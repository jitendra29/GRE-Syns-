import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;



public class Main {

	public static void main(String args[]) throws IOException
	{
		String wnhome = "res" + File . separator + "wn3.1.dict";
		String path = wnhome + File . separator + "dict";
		URL url = new URL("file", null , path );

		BufferedReader br = null;
		FileWriter fw_default = null;
		FileWriter fw_list = null;
		FileWriter fw_gloss = null;
		BufferedWriter bw_default = null;
		BufferedWriter bw_list= null;
		BufferedWriter bw_gloss = null;
		
		String word[]=null;
		POS pos=null;
		String sid=null;
		
		try {

			String query_default = null;
			String query_list = null;
			String query_gloss = null;
			List<String> sid_list = new ArrayList<String>();
			List<String> word_list = new ArrayList<String>();
			
			File file_default = new File("wordindex.sql");
			File file_list = new File("list.sql");
			File file_gloss = new File("gloss.sql");

			br = new BufferedReader(new FileReader("res/wordlist.txt"));
			String sCurrentLine;

			fw_default = new FileWriter(file_default.getAbsoluteFile());
			bw_default = new BufferedWriter(fw_default);
			fw_list = new FileWriter(file_list.getAbsoluteFile());
			bw_list = new BufferedWriter(fw_list);
			fw_gloss = new FileWriter(file_gloss.getAbsoluteFile());
			bw_gloss = new BufferedWriter(fw_gloss);

			IDictionary dict = new Dictionary(url);
			dict.open();
			
			int max=0;

			int c=0;
			int sidGRE=16000000;
			while((sCurrentLine = br.readLine()) != null)
			{
				word = sCurrentLine.split("_");
				word[0]=word[0].trim();
				if(word_list.indexOf(word[0])>=0)
				{
					continue;
				}
				word_list.add(word[0]);
				switch(Integer.parseInt(word[1]))
				{
				case 0: pos=POS.NOUN; break;
				case 1: pos=POS.VERB; break;
				case 2: pos=POS.ADJECTIVE; break;
				}
				
				IIndexWord iword = dict . getIndexWord (word[0], pos);
				if(iword!=null)
				{
					c++;
					query_default = "INSERT INTO wordindex VALUES("+c+",'"+word[0]+"', '"+word[2].replace("'","''")+"', 0);";
					bw_default.write(query_default);
					bw_default.newLine();

					for(IWordID idlist : iword.getWordIDs())
					{
						sid = idlist.getSynsetID().toString().split("-")[1];
						
						if(Integer.parseInt(sid)>max)
							max=Integer.parseInt(sid);
						
						query_list = "INSERT INTO lists VALUES("+c+","+sid+", 1);";
						bw_list.write(query_list);
						bw_list.newLine();

						if(!sid_list.contains(sid))
						{
							String gloss = dict.getSynset(idlist.getSynsetID()).getGloss();
							gloss=gloss.replace("'","''");
							query_gloss = "INSERT INTO gloss VALUES("+sid+",'"+gloss+"', "+word[1]+");";
							bw_gloss.write(query_gloss);
							bw_gloss.newLine();
							sid_list.add(sid);
						}
							
					}
				}
				else
				{
					c++;
					sidGRE++;
					query_default = "INSERT INTO wordindex VALUES("+c+",'"+word[0]+"', '"+word[2].replace("'","''")+"', 0);";
					bw_default.write(query_default);
					bw_default.newLine();

					query_list = "INSERT INTO lists VALUES("+c+","+sidGRE+",1);";
					bw_list.write(query_list);
					bw_list.newLine();

					String gloss = word[2];
					gloss=gloss.replace("'","''");
					query_gloss = "INSERT INTO gloss VALUES("+sidGRE+",'"+gloss+"', "+word[1]+");";
					bw_gloss.write(query_gloss);
					bw_gloss.newLine();
					sid_list.add(sid);
				}
				

			}
			System.out.println(c);
		
			bw_default.close();
			fw_default.close();
			bw_list.close();
			fw_list.close();
			bw_gloss.close();
			fw_gloss.close();
			
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

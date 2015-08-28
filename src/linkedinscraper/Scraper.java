package linkedinscraper;

/*
 * This is the main class for the linkedin scraper. It finds linkedin accounts 
 * by restricting a google search to results from the linkedin website. It 
 * parses out linkedin profiles and creates a Candidate object from each match.
 * 
 * by Weston Jackson
 * 
 */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Scraper{
	String company;               //search terms
	String keyWords;
	ArrayList<Candidate> namesFinal;
	ArrayList<Candidate> names;  //matched candidates
	int number;					 //number of google pages to search
	
	public Scraper(String company, String keyWords, int number) throws IOException{
		this.company = company;    //find search string
		this.keyWords = keyWords;
		this.number = number;    //google pages
		run();                   //run program
	}
	
	private void run() throws IOException,FailingHttpStatusCodeException,MalformedURLException,IOException{

		names = new ArrayList<Candidate>();
		
		//suppress warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		//open web client
		final WebClient wc = new WebClient();
		wc.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage page = wc.getPage("http://www.google.com"); 

		//get google search box
		final HtmlForm googleform = page.getFormByName("f");
		final HtmlTextInput hti = googleform.getInputByName("q");

		//enter search terms
		hti.setValueAttribute("((site:www.linkedin.com/pub | site:www.linkedin.com/in) -inurl:pub/dir) " + "\"" + company + "\"" + " " + keyWords);
		final HtmlSubmitInput button = googleform.getInputByValue("Google Search");
		
		/*
		 * program must wait random intervals between searches so not detected 
		 * by google security measures
		 */
		int random = (int) (Math.random() * 15000); 
		try {
			Thread.sleep(random);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
		
		//go to search page
		HtmlPage page2 = button.click();
		getContacts(names,page2);
		
		//wait for javascript to load
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		HtmlAnchor anchor = page2.getAnchorByText("Next");
		
		//cycle through rest of pages
		for(int i=1;i<number;i++){
			random = (int) (Math.random() * 15000);
			
			try {
				Thread.sleep(random);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
			}
			
			HtmlPage nextPage = anchor.click();
			getContacts(names,nextPage);
			anchor = nextPage.getAnchorByText("Next");
		}
		
		namesFinal = new ArrayList<Candidate>();
		
		for(int i=0;i<names.size();i++){
			if(names.get(i).getName().contains("profiles")){
				names.remove(i);
				i--;
			}
		}
		
		
		/*
		//first add candidates not working at the company
		for(int i=0;i<names.size();i++){
			Candidate current = names.get(i);
			
			
			if(current.getJob() == null){
				current.setJob("Not Specified");
				current.setLocation("Not Specified");
				namesFinal.add(0,current);
				names.remove(i);
				i--;
			}
			else if((!current.getJob().toLowerCase().contains(company.toLowerCase())) && (!current.getJob().toLowerCase().contains(keyWords.toLowerCase()))){
				namesFinal.add(0,current);
				names.remove(i);
				i--;
			}
		}
		
		//add no keywords
		for(int i = 0; i<names.size();i++){
			Candidate current = names.get(i);
			if(current.getJob().toLowerCase().contains(company.toLowerCase()));
			else{
				namesFinal.add(0,names.get(i));
				names.remove(i);
				i--;
			}
		}
		
		int middle = names.size();
		
		//add titles
		check("analyst");
		check("associate");
		check("vice president");
		check("vp");
		check("svp");
		check("senior vice president");
		check("director");
		check("managing director");
		check("head of");
		
		for(int i = 0; i<names.size();i++){
			namesFinal.add(namesFinal.size() - middle,names.get(i));
		}
		
		*/
		
        FileWriter writer= new FileWriter("Candidates.xls",true);
        for(int i = 0; i<names.size();i++){
        	Candidate current = names.get(i);
        	
        	//separate columns with tabs
        	writer.write(current.getName() + "\t" + current.getJob() + "\t" + current.getLocation() + "\t" + current.getUrl() + "\n");
        }
        writer.close();


	}
	
	/*
	private void check(String word){
		for(int i = 0; i<names.size();i++){
			Candidate current = names.get(i);
			if(current.getJob().toLowerCase().contains(word)){
				namesFinal.add(0,names.get(i));
				names.remove(i);
				i--;
			}
		}
		
	}
	*/
	
	/*
	 * method that parses contacts from html file
	 */
	private static void getContacts(ArrayList<Candidate> names, HtmlPage page2) throws IOException {
		
		//get html file of google search page, this code found on stackexchange
		URL url = page2.getUrl();
		URLConnection conn =  url.openConnection();
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String str;
        
        //read line by line
        while ((str = in.readLine()) != null) {
        		int endindex = 0;
        		
        		//signifies a linkedin profile link on google search page
        		endindex = str.indexOf("| LinkedIn</a>",endindex);
        		
        		if(endindex != -1){
        			int beginIndex = endindex;
        			
        			//traverse string to find beginning of name
        			while(str.charAt(beginIndex) != '>')
        			{
        				beginIndex--;
        				if(endindex-beginIndex == 50)
        					break;
        			}
        			
        			//CANDIDATE FOUND
        			if(str.charAt(beginIndex) == '>' && str.charAt(beginIndex+1) != ' '){
        				Candidate x = new Candidate();
        				String name = str.substring(beginIndex+1,endindex);
        				x.setName(name); //enter name
        				str = str.substring(beginIndex+1,str.length());
        				//parse out linkedin url
        				int urlIndex = str.indexOf("</cite>");
        				int beginUrlIndex = urlIndex;
        				if(urlIndex != -1){
	        				while(str.charAt(beginUrlIndex) != '>')
	            			{
	        					beginUrlIndex--;
	            				if(urlIndex-beginUrlIndex == 100)
	            					break;
	            			}
	        				
	        				if(str.charAt(beginUrlIndex) == '>')
	        					x.setUrl(str.substring(beginUrlIndex+1,urlIndex));
        				}
        				
        				//parse out job location
        				int locationIndex = str.indexOf("<div class=\"f slp\">");
        				int endlocationIndex = locationIndex + 1;
        				if(locationIndex != -1){
        					while(str.charAt(endlocationIndex) != '-')
        					{
        						endlocationIndex++;
        						if(endlocationIndex == str.length()-1)
	            					break;
        					}
        					if(str.charAt(endlocationIndex) == '-'){
        						String location = str.substring(locationIndex+19,endlocationIndex);
        						location = location.replaceAll("&nbsp;", "");
	        					x.setLocation(location);
        					}
        					
        					int jobIndex = endlocationIndex + 1;
        					int endjobIndex = jobIndex + 1;
        					while(str.charAt(endjobIndex) != '<')
        					{
        						endjobIndex++;
        						if(endjobIndex == str.length()-1)
	            					break;
        					}
        					if(str.charAt(endjobIndex) == '<'){
        						String job = str.substring(jobIndex,endjobIndex);
        						job = job.replaceAll("&nbsp;", "");
	        					x.setJob(job);
        					}
        					
        				}

        				names.add(x);
        			}
        		}
        }
        in.close();
	}
}

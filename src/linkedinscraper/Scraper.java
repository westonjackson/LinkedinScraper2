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
import javax.swing.JTextPane;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Scraper{
	String search;               //search terms
	ArrayList<Candidate> names;  //matched candidates
	int number;					 //number of google pages to search
	
	public Scraper(String search, int number) throws IOException{
		this.search = search;    //find search string
		this.number = number;    //google pages
		run();                   //run program
	}
	
	private void run() throws IOException{
		try{

	
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
		hti.setValueAttribute("site:linkedin.com profile " + search);
		final HtmlSubmitInput button = googleform.getInputByValue("Google Search");
		
		/*
		 * program must wait random intervals between searches so not detected 
		 * by google security measures
		 */
		int random = (int) (Math.random() * 15000); 
		try {
			Thread.sleep(random);
		} catch (InterruptedException e) {
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
        
		//write to excel file
        FileWriter writer= new FileWriter("Candidates.xls",true);
        for(int i = 0; i<names.size();i++){
        	Candidate current = names.get(i);
        	
        	//separate columns with tabs
        	writer.write(current.getName() + "\t" + current.getUrl() + "\t" + current.getJob() + "\n");
        }
        writer.close();
        
        } 
		
		//catch errors, and still write to the file
		catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
			FileWriter writer= new FileWriter("Candidates.xls",true);
			for(int i = 0; i<names.size();i++){
	        	Candidate current = names.get(i);
	        	writer.write(current.getName() + "\t" + current.getUrl() + "\t" + current.getJob() + "\n");
	        }
			String result = "Google not available";
			writer.write(result);
	        writer.close();
			
		} catch (MalformedURLException e1) {
			FileWriter writer= new FileWriter("Candidates.xls",true);
			for(int i = 0; i<names.size();i++){
	        	Candidate current = names.get(i);
	        	writer.write(current.getName() + "\t" + current.getUrl() + "\t" + current.getJob() + "\n");
	        }
			String result = "Bad URL";
			writer.write(result);
	        writer.close();
		} catch (IOException e1) {
			FileWriter writer= new FileWriter("Candidates.xls",true);
			for(int i = 0; i<names.size();i++){
	        	Candidate current = names.get(i);
	        	writer.write(current.getName() + "\t" + current.getUrl() + "\t" + current.getJob() + "\n");
	        }
			String result = "Input/Output Error";
			writer.write(result);
	        writer.close();
		}
	}

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
        				
        				//parse out job title
        				int jobIndex = str.indexOf("<div class=\"f slp\">");
        				int endJobIndex = jobIndex + 1;
        				if(jobIndex != -1){
        					while(str.charAt(endJobIndex) != '<')
        					{
        						endJobIndex++;
        						if(endJobIndex == str.length()-1)
	            					break;
        					}
        					if(str.charAt(endJobIndex) == '<'){
        						String job = str.substring(jobIndex+19,endJobIndex);
        						job = job.replaceAll("&nbsp;", " ");
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

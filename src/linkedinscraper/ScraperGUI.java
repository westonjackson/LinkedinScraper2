package linkedinscraper;

/*
 * UI for the Linkedin Scraper. A search can take up to 5 minutes 
 * due the random intervals between google requests. Excel file
 * will be in same directory as the program (it appends to end of file).
 * 
 * by Weston Jackson
 */


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class ScraperGUI {
public static void main(String args[]){
		
		JTextField company = new JTextField("Goldman Sachs");
		JTextField keyWords = new JTextField("Asset Management");
		//create centered text pane
		JTextPane textpane = new JTextPane();
		textpane.setText("\n\nDirections:\n1. Select number of profiles to receive\n"
				+ "2. Type in company and keywords for search\n"
				+ "3. Click submit and open Candidates.xls\n"
				+ "\n\t****Profiles received approximately equals seconds of runtime!****\t\n\n");
		StyledDocument doc = textpane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		//page search options
		String[] options = {"10","20","30","40","50","100","150","200"};
		
		JComboBox box = new JComboBox(options);
		
		JButton submit = new JButton("Submit");
		submit.addActionListener(new scrapeListener(company,keyWords,box));
		
		JFrame frame = new JFrame("LinkedIn Scraper");
		
		//add to panel
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(box);
		panel.add(company);
		panel.add(keyWords);
		panel.add(submit);
		
		//create frame
		frame.setLayout(new BorderLayout());
		frame.add(panel,BorderLayout.CENTER);
		frame.add(textpane,BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		}
		
		//Action listener subclass for Scraper buttons
		public static class scrapeListener implements ActionListener{
			
			private JTextField search;
			private JTextField keyWords;
			private JComboBox box;
			
			//Initialize variables
			public scrapeListener(JTextField search, JTextField keyWords, JComboBox box)
			{
				this.search = search;
				this.keyWords = keyWords;
				this.box = box;
				
			}
			
			
			public void actionPerformed(ActionEvent ae){
				
				//Displays shortest path
				if(ae.getActionCommand().equals("Submit")){
					try{
					String number = (String)box.getSelectedItem();
					
					if(number != "(# Profiles)"){
						int num = Integer.parseInt((String)box.getSelectedItem())/10;
					    Scraper scraper = new Scraper(search.getText(), keyWords.getText(), num);
					}
					
					}
					catch(IOException e1){
						search.setText("ERROR!");
						keyWords.setText("There was an input/output error");
					}
					
					catch (FailingHttpStatusCodeException e1) {
						search.setText("ERROR!");
						keyWords.setText("Google caught the program! Try again in an hour or tomorrow.");
					} 
				}
			}
		
		}
}

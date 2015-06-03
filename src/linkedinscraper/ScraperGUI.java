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

public class ScraperGUI {
public static void main(String args[]){
		
		JTextField search = new JTextField("Type in what you want to search for!");
		
		//create centered text pane
		JTextPane textpane = new JTextPane();
		textpane.setText("Directions:\n1. Select number of Google pages to search\n2. Type in qualifications for search\n3. Click submit and open Candidates.xls\n");
		StyledDocument doc = textpane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		//page search options
		String[] options = {"(# Pages)","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
		
		JComboBox box = new JComboBox(options);
		
		JButton submit = new JButton("Submit");
		submit.addActionListener(new jibeListener(search,textpane,box));
		
		JFrame frame = new JFrame("LinkedIn Scraper");
		
		//add to panel
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(box);
		panel.add(search);
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
		public static class jibeListener implements ActionListener{
			
			private JTextField search;
			private JTextPane area;
			private JComboBox box;
			
			//Initialize variables
			public jibeListener(JTextField search,JTextPane area,JComboBox box)
			{
				this.search = search;
				this.area = area;
				this.box = box;
				
			}
			
			
			public void actionPerformed(ActionEvent ae){
				
				//Displays shortest path
				if(ae.getActionCommand().equals("Submit")){
					try{
					String number = (String)box.getSelectedItem();
					
					if(number != "(# Pages)"){
						int num = Integer.parseInt((String)box.getSelectedItem());
					    Scraper scraper = new Scraper(search.getText(), num);
					}
					
					}
					catch(IOException e1){
						search.setText("There was an input/output error");
					}
				}
			}
		
		}
}

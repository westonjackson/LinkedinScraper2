# LinkedinScraper2

By Weston Jackson

westonjackson2106@gmail.com

LinkedinScraper allows a user to input candidate key words and receive an excel sheet of candidates, linkedin profiles, and current job titles. It bypases the Linkedin security measures by searching through google, and bypasses google security measures by making requests at random intervals between 1-15 seconds. When using the GUI, a user can also specify the number of pages of Google to search for candidates.

<h3>Required External Libraries:</h3>

htmlunit-2.16

selenium-2.45.0

<h3>Classes:</h3>

ScraperGUI.java - UI for the Linkedin scraper, contains the main method. Has subclass for the actionlistener submit button.
Scraper.java - Contains the bulk of the program, parses through google search results to get linkedin profiles of candidates.
Candidate.java - Candidate object contains name, url, and job.

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * Created by ebricco on 9/9/17.
 */
public class Main {
    public static void main(String[] args) {

        //suppress annoying htmlunit console output
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

        System.out.println("WELCOME TO 20Q!");
        System.out.println("PREPARE TO GET CRUSHED BY MY HIGHLY CAPABLE AND STOLEN ALGORITHM");
        System.out.println("\nloading first question.....\n");

        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        HtmlPage page = null;

        //connect to the webpage
        try {
            page = webClient.getPage("http://y.20q.net/gsq-en");
        } catch (IOException e) {
            System.out.println("error connecting to webpage: " + e.toString());
            System.exit(1);
        }

        //get frame
        page = (HtmlPage) page.getFrameByName("mainFrame").getEnclosedPage();

        //get submit button of info page
        HtmlInput button = page.querySelector("body > table > tbody > tr:nth-child(1) > td:nth-child(1) > center > form > table > tbody > tr:nth-child(4) > td:nth-child(2) > input[type=\"submit\"]");


        //navigate to the page of with first question
        try {
            page = button.click();
        } catch(IOException e) {
            System.out.println("that button didn't work" + e.toString());
            System.exit(1);
        }

        Scanner in = new Scanner(System.in);
        boolean done = false;

        while(!done) {

            //check for 20Q win
            DomElement win = page.querySelector("body > table > tbody > tr:nth-child(1) > td:nth-child(1) > table > tbody > tr > td > h2:nth-child(1)");

            if(win != null) {
                System.out.println(win.getTextContent());
                System.out.println("Thanks for playing!");
                System.exit(0);
            }

            //retrieve and display the question
            String question = page.querySelector("body > table > tbody > tr:nth-child(1) > td:nth-child(1) > table > tbody > tr > td > big > b").getTextContent();
            System.out.println(question.substring(0, question.indexOf("?") + 1) + "\n");

            //parse answers
            List<String> choices = Arrays.asList(question.substring(question.indexOf("?") + 2).split(","));

            //display answers
            System.out.println("Please choose one of the following:");
            IntStream.range(0, choices.size()).forEach(i -> {
                choices.set(i, choices.get(i).replaceAll(" ", "").replaceAll("\n", "").replace(" ", "").replace(" ", "")); //note second space is different than first
                System.out.println(Integer.toString(i + 1) + ". " + choices.get(i));
            });

            boolean hasValidChoice = false;
            int selection = -1;

            //get valid user input
            while(!hasValidChoice) {
                System.out.println("enter choice and hit enter: ");

                //parse user selection
                try {
                    selection = Integer.parseInt(in.nextLine());

                    if (selection < 1 || selection > choices.size()) {
                        System.out.println("please enter number between 1 and " + choices.size() + "!!");
                    } else {
                        hasValidChoice = true;
                    }
                } catch(NumberFormatException e) {
                    System.out.println("please only enter numeric characters");
                }
            }

            //navigate to next question
            HtmlAnchor anchor = page.querySelector("body > table > tbody > tr:nth-child(1) > td:nth-child(1) > table > tbody > tr > td > big > b > nobr > a:nth-child(" + selection + ")");

            try {
                page = anchor.click();
            } catch(IOException e) {
                System.out.println("unexpected error navigating to next page: " + e.toString());
            }

            System.out.flush();
        }
    }
}

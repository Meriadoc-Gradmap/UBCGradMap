package org.DataCollection;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebScraper {
    
    public static void main(String[] args) {
        // initializing the HTML Document page variable 
        Document doc; 
        
        try { 
            // fetching the target website 
            doc = Jsoup.connect("https://www.scrapingcourse.com/ecommerce/").get(); 
        } catch (IOException e) { 
            throw new RuntimeException(e); 
        }
    }
}

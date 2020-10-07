package txtparsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TXTParsing {


    public static String fileToString(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        scanner.useDelimiter("\\A");
        return scanner.next();
    }

    //Parses documents.txt and returns documents as a list of MyDoc objects
    public static List<MyDoc> parse(String filename){
        try{
            String txt_file = fileToString(filename);
            String docs[] = txt_file.split("///");
            System.out.println("Read: " + docs.length + " docs");


            List<MyDoc> parsed_docs = new ArrayList<MyDoc>();
            for(String doc:docs){
                doc = doc.trim();
                int firstBreak = doc.indexOf("\n");
                int secondBreak = doc.indexOf(":");
                MyDoc mydoc = new MyDoc(doc.substring(0, firstBreak).trim(), doc.substring(firstBreak, secondBreak).trim(), doc.substring(secondBreak+1).trim());
                parsed_docs.add(mydoc);
            }
            return parsed_docs;

        } catch(Throwable err) {
            err.printStackTrace();
            return null;
        }
    }

    //Creates a new WordNet file that doesn't include verbs
    public static void editWN (String filename) throws Exception {

        String wn_text=fileToString(filename);
        String terms[] = wn_text.split("\n");
        System.out.println("Editing WordNet file..." );
        System.out.println("Terms read: "+terms.length);


        StringBuilder editedWN = new StringBuilder();

        for(String term:terms){
            term=term.trim();
            String[] termAr = term.split("\\,");

            if(!termAr[3].trim().equals("v")){
                editedWN.append(term).append("\n");
            }
        }
        FileWriter writer = new FileWriter("src/wn_s_noverbs.pl");
        writer.write(editedWN.toString());
        writer.close();

        System.out.println("New WordNet file created.");


    }
    public static void main(String[] args){
        try{
            editWN("src/wn_s.pl");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}

package txtparsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TXTParsing {


    public static String fileToString(String filename) throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(filename));
        scanner.useDelimiter("\\A");
        return scanner.next();
    }

    public static List<MyDoc> parse(String filename) throws Exception{
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

}

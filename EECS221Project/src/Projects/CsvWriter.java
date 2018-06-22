package Projects;

import java.io.*;
import java.util.*;

public class CsvWriter { 

    private final String CRLF = "\r\n"; 
    private String delimiter = ""; 

    public void setDelimiter(String delimiter) { 
        this.delimiter = delimiter; 
    } 

    public void exportCsv(List<String[]> twoDimensionalData, String filename) { 
        try { 
            FileWriter writer = new FileWriter(filename); 

            for (int i = 0; i < twoDimensionalData.size(); i++) { 
                for (int j = 0; j < twoDimensionalData.get(i).length; j++) { 
                    writer.append( 
                            //NOTE: for demonstration purposes we use the toString() method 
                            twoDimensionalData.get(i)[j]
                            //use an alternative to toString() if it is not implemented as needed. 
                            ); 
                 
                    //Don't forget the delimiter 
                    if (j < twoDimensionalData.get(i).length - 1) { 
                        writer.append(delimiter); 
                    } 
                } 
                //Add delimiter and end of the line 
                if (i < twoDimensionalData.size() - 1) { 
                    writer.append(delimiter + CRLF);
                    //writer.append(CRLF);
                    //writer.append(CRLF);
                } 
            } 

            writer.flush(); 
            writer.close(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
}

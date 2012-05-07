/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 *
 * @author gabrielpoca
 */
public class Converter {
    /** Converts an object to an array of bytes */
    public static byte[] objectToBytes(Object object) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
        oos.writeObject(object);

        return baos.toByteArray();
    }

    /** Converts an array of bytes back to its constituent object. */
    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object object = null;
	

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
	object = in.readObject();
	in.close();
	
        return object;
    }

    public static byte[] filetoBytes(String datapath) throws FileNotFoundException, IOException{
        byte[] bytes = null;
        File file = new File(datapath);

        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        
        for (int readNum; (readNum = fis.read(buf)) != -1;) 
            bos.write(buf, 0, readNum); 

        bytes = bos.toByteArray();
        return bytes;
    }

    public static File bytestoFile(byte[] bytes, String fileName) throws
            FileNotFoundException, IOException{
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
            
        fos.write(bytes);
        fos.flush();
        fos.close();

        return file;
    }
    
}

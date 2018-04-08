package me.iologic.apps.dtn;

import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageData {

    public byte[] ImageToBytes(Uri filePath) {
        File file = new File(filePath.toString());
        //init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis;

        try {
            fis = new FileInputStream(file);
            try {
                fis.read(bytesArray); //read file into bytes[]
                fis.close();
            } catch (IOException er) {

            }

        } catch (FileNotFoundException io) {

        }

        return bytesArray;
    }

    /**
     * Write an array of bytes to a file. Presumably this is binary data; for plain text
     * use the writeFile method.
     */
    public void writeFileAsBytes(Uri filePath, byte[] bytes) throws IOException {
        String imageFileName = filePath.toString() + Constants.FileNames.receivedImageFileName;
        OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imageFileName));
        InputStream inputStream = new ByteArrayInputStream(bytes);
        int token = -1;

        while ((token = inputStream.read()) != -1) {
            bufferedOutputStream.write(token);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        inputStream.close();
    }
}

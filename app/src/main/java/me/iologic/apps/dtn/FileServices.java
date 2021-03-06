package me.iologic.apps.dtn;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Created by vinee on 16-01-2018.
 */

public class FileServices {

    private File file;
    private File dataFile;
    private FileOutputStream outputStream;
    private Context ctx;

    byte[] readData;
    String dataUUID;

    public FileServices(Context context, String receivedUUID)
    {
        ctx = context;
        dataUUID = receivedUUID;
    }

    public File createTemporaryFile(String ReceivedFileName) {

        file = new File(ctx.getFilesDir(), ReceivedFileName);
        return file;
    }

    public boolean checkFileExists(String ReceivedFileName){
        File file = new File(ctx.getFilesDir() + File.separator + ReceivedFileName);
        if(file.exists())
        {
            Log.i(Constants.TAG, "File is found: " + file.getName());
            return true;
        }
            Log.e(Constants.TAG, "File not found");
            return false;
    }

    public File returnFile(String ReceivedFileName){
        File file = new File(ctx.getFilesDir(), ReceivedFileName);
        if(file.exists()){
            return file;
        } else {
            return null;
        }

    }



    public void fillTempFile(File ReceivedTempFileObj)
    {
        try {
            RandomAccessFile f = new RandomAccessFile(ReceivedTempFileObj, "rw");
            f.setLength(1024 * 1024);
        } catch (IOException FileError){
            Log.i(Constants.TAG, "Could Not Read File");
        }

    }

    public byte[] readTempFile(File ReceivedFileObj){
        byte [] data = new byte[ (int) ReceivedFileObj.length() ];
        readData = new byte[(int) ReceivedFileObj.length()];
        try {
            FileInputStream fin = new FileInputStream(ReceivedFileObj);
            int n = 0;
            while ( (n = fin.read(data, n, data.length - n) ) > 0);

        }
        catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "File not found (from read() file): " + e.toString());
        } catch (IOException e) {
            Log.e(Constants.TAG, "Can not read file: " + e.toString());
        }

        readData = data;

        // Log.i(Constants.TAG, "File Size Read:" + readData.length);

        return readData;
    }

    public long getFileSize(){
        return readData.length;
    }

    static String createString(long size){
        StringBuilder o=new StringBuilder();
        for(int i=0;i<size;i++){
            o.append("*");
        }
        return o.toString();
    }

    // Save Bandwidth Data To File
    public void saveBWData(String ReceivedFileName, String ReceivedBandwidth){
        String saveFileName = ReceivedFileName + "--" + dataUUID + ".txt";
            dataFile = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), saveFileName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(dataFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(ReceivedBandwidth + "\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // Save Message Sending Delay Data To File
    public void saveDelayData(String ReceivedFileName, float ReceivedDelay) {
        String saveFileName = ReceivedFileName + "--" + dataUUID + ".txt";
        dataFile = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), saveFileName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(dataFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(ReceivedDelay + " ms" + "\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        // Save Message Sending Delay Data To File
    public void savePairingData(String ReceivedFileName, String currentStatus, int ReceivedDelay){
        String saveFileName = ReceivedFileName + "--" + dataUUID + ".txt";
        dataFile = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), saveFileName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(dataFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(ReceivedDelay + " ms" + "\r\n" + currentStatus);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // Save Message Sending Delay Data To File
    public void savePacketLossData(String ReceivedFileName, double ReceivedPacketLoss) {
        String saveFileName = ReceivedFileName + "--" + dataUUID + ".txt";
        dataFile = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), saveFileName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(dataFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(ReceivedPacketLoss + " %" + "\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File saveBWReceivedData(String ReceivedFileName, Object ReceivedBWData) { // Only For One Scenario
        String saveFileName = ReceivedFileName + "--" + dataUUID;
        File BWFile = new File(ctx.getFilesDir(), saveFileName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(BWFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(ReceivedBWData.toString());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BWFile;
    }

    public byte[] readBWReceivedFile (File ReceivedFileObj){
        byte [] data = new byte[ (int) ReceivedFileObj.length() ];
        readData = new byte[(int) ReceivedFileObj.length()];
        try {
            FileInputStream fin = new FileInputStream(ReceivedFileObj);
            int n = 0;
            while ( (n = fin.read(data, n, data.length - n) ) > 0);

        }
        catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "File not found (from read() file): " + e.toString());
        } catch (IOException e) {
            Log.e(Constants.TAG, "Can not read file: " + e.toString());
        }

        readData = data;

        Log.i(Constants.TAG, "File Size Read:" + readData.length);

        return readData;
    }





    public void deleteFile(){
          if(file.delete() == true)
          {
            Log.i(Constants.TAG, "File " + file.getName() + " is deleted.");
          } else {
              Log.e(Constants.TAG, "File " + file.getName() + " could not be deleted.");
          }
    }
}

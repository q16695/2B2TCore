package me.miku.main;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {
    public static ArrayList<String> getFileAllContent(File file) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String content = "";
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "gbk");
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        while ((content = bufferedReader.readLine()) != null) {
            list.add(content);
        }
        return list;
    }

    public static boolean containsString(File file, String string) throws IOException {
        for(String v : getFileAllContent(file)) {
            if(v.startsWith(string)) {
                return true;
            }
        }
        return false;
    }

    public static void writeLastLine(File file, String message) {
        try {
            ArrayList<String> cache = getFileAllContent(file);
            file.deleteOnExit();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            for(String v : cache) {
                fileWriter.write(v + "\n");
            }
            fileWriter.write(message);
            fileWriter.close();
        } catch (Exception e) {
            //
        }

    }
}

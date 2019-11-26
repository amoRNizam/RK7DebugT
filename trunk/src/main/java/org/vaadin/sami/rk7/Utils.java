package org.vaadin.sami.rk7;

import org.vaadin.sami.javaday.TetrisUI;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.vaadin.sami.javaday.TetrisUI.basepath;

public class Utils {

    public static Map<String, String> getProperty() {
        String location = basepath + "\\WEB-INF\\settings.ini";
        Map<String, String> properties = new HashMap<>();
        Properties props = new Properties();
        if (location != null) {
            try {
                props.load(new FileInputStream(new File(location)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            props.forEach((key, value) -> properties.put(key.toString(), value.toString()));
        }
        props.clear();
        return properties;
    }

    public static void reReference() throws IOException {

        for (Map.Entry<String, String> img : TetrisUI.ERROR_DIFF_IMG.entrySet()) {
            String erImg = img.getValue().substring(img.getValue().lastIndexOf("\\") + 1).trim();
            File from = new File(String.format(TetrisUI.pathProject.getValue() + "\\%s\\%s", img.getKey(), erImg
                    .replace(Config.PREFIX_ERROR_IMG, "").trim()
                    .replace(Config.F_ERROR_EXT, Config.F_REFERENCE_EXT)));

//            System.out.println(from.getAbsolutePath());

            File to = new File(img.getValue()
                    .replace(Config.PREFIX_ERROR_IMG, "").trim()
                    .replace(Config.F_ERROR_EXT, Config.F_REFERENCE_EXT));
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;


            System.out.println(from.getAbsolutePath());
            System.out.println(to.getAbsolutePath());
            try {
                sourceChannel = new FileInputStream(to).getChannel();
                destChannel = new FileOutputStream(from).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            }
            System.out.println("Replace");
        }
    }

    public static void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles())
            if (myFile.isFile()) myFile.delete();
    }
}

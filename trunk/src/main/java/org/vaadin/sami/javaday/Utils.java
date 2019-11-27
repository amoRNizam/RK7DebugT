package org.vaadin.sami.javaday;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import org.apache.commons.io.FileUtils;
import org.vaadin.sami.rk7.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

import static org.vaadin.sami.javaday.DebugUI.*;
import static org.vaadin.sami.rk7.Config.*;

public class Utils {

    public static Map<String, String> getProperty() {
        String location = basepath + "\\Config\\settings.ini";
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

    /**
     * Метод замены скриншотов
     * @throws IOException
     */
    public static void reReference() throws IOException {

        for (Map.Entry<String, String> img : DebugUI.ERROR_DIFF_IMG.entrySet()) {

            String erImg = img.getValue().substring(img.getValue().lastIndexOf("\\") + 1).trim();
            // Эталон в проекте (что заменяем)
            File from = new File(String.format(PATH_PROJECT + "\\%s\\%s", img.getKey(), erImg
                    .replace(Config.PREFIX_ERROR_IMG, "").trim()
                    .replace(Config.F_ERROR_EXT, Config.F_REFERENCE_EXT)));

            // Скрин сделанный кассой (чем заменяем)
            File to = new File(img.getValue()
                    .replace(Config.PREFIX_ERROR_IMG, "").trim()
                    .replace(Config.F_ERROR_EXT, Config.F_REFERENCE_EXT));
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;

            try {
                sourceChannel = new FileInputStream(to).getChannel(); // чем заменяем
                destChannel = new FileOutputStream(from).getChannel(); // что заменяем

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
        }
    }

    /**
     * Удаление всех файлов в директории
     * @param path - путь к директории
     */
    public static void deleteAllFilesFolder(String path) {
        try {
            File folder = new File(path);
            for (File myFile : folder.listFiles())
                if (myFile.isFile()) myFile.delete();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static List<Resource> getResForImageViewer() {
        BufferedImage buffImag = null;
        List<Resource> img = new ArrayList<Resource>();

        //--------------------- 1
        try {
            buffImag = ImageIO.read(new File(ALL_IMG_IN_FAIL_TEST.get(SELECTED_ER_T_IN_ALL.trim()).get(3)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(buffImag, "png",
                    new File(basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_temp.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath_3 = basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_temp.png";
        Resource res_3 = new FileResource(
                new File(filePath_3));
        img.add(res_3);
        //--------------------- 2
        try {
            buffImag = ImageIO.read(new File(ALL_IMG_IN_FAIL_TEST.get(SELECTED_ER_T_IN_ALL.trim()).get(1)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(buffImag, "png",
                    new File(basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_dif.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath_1 = basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_dif.png";
        Resource res_1 = new FileResource(
                new File(filePath_1));
        img.add(res_1);
        //--------------------- 3
        try {
            buffImag = ImageIO.read(new File(ALL_IMG_IN_FAIL_TEST.get(SELECTED_ER_T_IN_ALL.trim()).get(2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(buffImag, "png",
                    new File(basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_cash.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath_2 = basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + "_cash.png";
        Resource res_2 = new FileResource(
                new File(filePath_2));
        img.add(res_2);

        return img;
    }

    public static Set<String> getResultDir() {
        LIST_RESULT_DIR.clear();
        System.out.println(PATH_RESULT);
        File file = new File(PATH_RESULT);
        File root = file;
        System.out.println(file.getAbsolutePath());
        try {
            boolean recursive = true;

            if (file.isDirectory()) {
                System.out.println("1*******");
                Collection files = FileUtils.listFiles(root, null, recursive);
                for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                    System.out.println("2*******");
                    File dirResult = (File) iterator.next();
                    if (dirResult.getName().contains(PREFIX_RESULT_DIR)) {
                        System.out.println("3*******");
                        System.out.println(dirResult);
                        LIST_RESULT_DIR.add(dirResult.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        for (String img :
//                difImg) {
//            System.out.println(img);
//        }
//        for (Map.Entry<String, Map<Integer, String>> img : ALL_IMG_IN_FAIL_TEST.entrySet()) {
//            System.out.println("Тест: " + img.getKey() + "\n" +
//                    "Различие: " + img.getValue().get(1) + "\n" +
//                    "Скрин кассы: " + img.getValue().get(2) + "\n" +
//                    "Эталон: " + img.getValue().get(3)) ;
//        }
        return LIST_RESULT_DIR;
    }
}

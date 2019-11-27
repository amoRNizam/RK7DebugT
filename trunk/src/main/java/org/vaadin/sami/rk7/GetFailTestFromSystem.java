package org.vaadin.sami.rk7;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.util.*;

import static org.vaadin.sami.javaday.DebugUI.*;
import static org.vaadin.sami.rk7.Config.*;

public class GetFailTestFromSystem {
    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels.
     */
    public static void showChildrenRes(String pathResult) {
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = new File(pathResult);
                if (file.isDirectory()) {
                    file = new File(pathResult);
                    File[] files = fileSystemView.getFiles(file, true);
                    //!!
//                    if (node.isLeaf()) {
//                        for (File child : files) {
//                            if (child.isDirectory()) {
//                                publish(child);
//                            }
//                        }
//                    }
                    System.out.println("Заполнение списка файлов!");
                    listFile.clear();
                    ArrayList<File> sList = new ArrayList<>(Arrays.asList(files));
                    sList.forEach(x -> {
                        if (x.isDirectory())listFile.add(x);});
                }
                return null;
            }
        };
        getFailDiffImg(new File(pathResult));
    }

    /**
     * Метод получения изображений из упавших тестов
     * @param file - директория теста
     */
    public static void getFailDiffImg(File file) {

        File root = file;
        try {
            boolean recursive = true;

            if (file.isDirectory()) {
                Collection files = FileUtils.listFiles(root, null, recursive);
                for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                    File fileDiff = (File) iterator.next();
                    if (fileDiff.getName().contains(PREFIX_ERROR_IMG)) {
                        Map<Integer, String> imgInTest = new HashMap<>();
                        try {
                            imgInTest.put(1, fileDiff.getAbsolutePath().trim());
                            imgInTest.put(2, fileDiff.getParentFile() + "\\" + fileDiff.getName()
                                    .replace(PREFIX_ERROR_IMG, "")
                                    .replace(F_ERROR_EXT, F_REFERENCE_EXT));
                            imgInTest.put(3,  fileDiff.getParentFile() + "\\" + fileDiff.getName()
                                    .replace(PREFIX_ERROR_IMG, PREFIX_TEMPLATE_IMG));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        difImg.put(fileDiff.getParentFile().getParentFile().getName(), fileDiff.getAbsolutePath().trim());
                        ERROR_TEST.put(fileDiff.getParentFile().getParentFile().getName(), fileDiff.getParentFile().getParentFile().getAbsolutePath());
                        ALL_IMG_IN_FAIL_TEST.put(fileDiff.getParentFile().getParentFile().getName(), imgInTest);
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
        for (Map.Entry<String, Map<Integer, String>> img : ALL_IMG_IN_FAIL_TEST.entrySet()) {
            System.out.println("Тест: " + img.getKey() + "\n" +
                    "Различие: " + img.getValue().get(1) + "\n" +
                    "Скрин кассы: " + img.getValue().get(2) + "\n" +
                    "Эталон: " + img.getValue().get(3)) ;
        }
    }
}

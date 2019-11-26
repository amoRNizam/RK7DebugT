package org.vaadin.sami.rk7;

import com.sun.org.apache.xerces.internal.xs.StringList;
import com.vaadin.server.VaadinService;
import org.apache.commons.io.FileUtils;
import org.vaadin.sami.javaday.TetrisUI;

import javax.swing.*;
import java.io.File;
import java.util.*;

import static org.vaadin.sami.javaday.TetrisUI.*;
import static org.vaadin.sami.rk7.Config.PREFIX_ERROR_IMG;

public class GetFailTestFromSystem {
    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels.
     */
    public static void showChildrenRes(String pathResult) {
//        tree.setEnabled(false);
//        progressBar.setVisible(true);
//        progressBar.setIndeterminate(true);
        System.out.println("Метод showChildrenRes");
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
//        worker.execute();
    }

    public static void getFailDiffImg(File file) {

        File root = file;
        try {
            boolean recursive = true;

            if (file.isDirectory()) {
                Collection files = FileUtils.listFiles(root, null, recursive);
                for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                    File file1 = (File) iterator.next();
//                    System.out.println(file1.getAbsolutePath());
//                    System.out.println(PREFIX_ERROR_IMG);
                    if (file1.getName().contains(PREFIX_ERROR_IMG)) {
//                        System.out.println(file1.getAbsolutePath());
//                        System.out.println(file1.getParentFile().getParentFile().getName());
//                        System.out.println(file1.getParentFile().getParentFile().getAbsolutePath());
                        difImg.put(file1.getParentFile().getParentFile().getName(), file1.getAbsolutePath().trim());
                        ERROR_TEST.put(file1.getParentFile().getParentFile().getName(), file1.getParentFile().getParentFile().getAbsolutePath());
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
        for (Map.Entry<String, String> img : difImg.entrySet()) {
            System.out.println(img.getKey() + " - " + img.getValue());
        }

    }
}

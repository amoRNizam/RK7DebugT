package org.vaadin.sami.rk7;

import org.vaadin.sami.javaday.Utils;

public class Config {
    public static String PREFIX_ERROR_IMG;
    public static String PREFIX_TEMPLATE_IMG;
    public static String PREFIX_RESULT_DIR;
    public static String F_ERROR_EXT;
    public static String F_REFERENCE_EXT;
    public static String PATH_RESULT;
    public static String PATH_PROJECT;

    public Config() {
        PREFIX_ERROR_IMG = Utils.getProperty().get("prefixErDiffImg");
        PREFIX_TEMPLATE_IMG = Utils.getProperty().get("prefixTemplateImg");
        PREFIX_RESULT_DIR = Utils.getProperty().get("prefixResultDir");
        F_ERROR_EXT = Utils.getProperty().get("errorFileExtension");
        F_REFERENCE_EXT = Utils.getProperty().get("referenceFileExtension");
        PATH_RESULT = Utils.getProperty().get("pathResult");
        PATH_PROJECT = Utils.getProperty().get("pathProject");
    }
}

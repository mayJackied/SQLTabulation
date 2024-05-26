package com.jackied;

import java.io.*;
import java.util.*;

public class staticValue {
    public static Properties recodeMap = new Properties();
    public static Properties loginMap = new Properties();

    public static int maxX;
    public static int maxY;

    public static boolean isLogInWithTheLastIP;
    public static boolean isBuildInDatabases;

    public static Set<String> dbSet = new HashSet<>();
    public static Map<String,HashSet<String>> tbMap = new HashMap<>();

    public static final String[] buildInDatabasesArr = {"information_schema","mysql","performance_schema","sys"};

    public static Map<String, Map<String, String>> query;
    public static Map<String,String> DetailedInformation = new HashMap<>();

    public static Map<Integer,String> getHeaderFormX = new HashMap<>();
    public static Map<String, Integer> getMaxLengthFromHeader = new HashMap<>();

    public static Map<String,String> selectIDFormY = new HashMap<>();

    public static int keyVertical;

    public static String keyColumn;

    public static String selectedDb;
    public static String selectedTb;
    public static boolean isClickedDb = false;
    public static boolean isClickedRoot = false;

    public static String choseDb;


    public final static String onStyle = "-fx-background-color: white;";
    public final static String leaveStyle = "-fx-background-color: Gainsboro;";
    public final static String norStyle = "-fx-border-width: 0.3;-fx-border-color: black;";


    public staticValue() {

    }
    static {
        loginInput();
    }

    /**
     * login recode
    */

    public static void loginOutput(){
        try {
            OutputStreamWriter loginRecodeWriter = new OutputStreamWriter(new FileOutputStream("src/main/resources/PropertiesRecode/login.properties"));
            loginMap.store(loginRecodeWriter,null);
            loginRecodeWriter.close();
        }catch (IOException ignored){}
    }

    private static void loginInput(){
        try {
            InputStreamReader loginRecodeReader = new InputStreamReader(new FileInputStream("src/main/resources/PropertiesRecode/login.properties"));
            loginMap.load(loginRecodeReader);
            loginRecodeReader.close();
            isLogInWithTheLastIP = loginMap.get("isLogInWithTheLastIP").equals("1");
            isBuildInDatabases = loginMap.get("isBuildInDatabases").equals("1");
        }catch (IOException ignored){}
    }

    /**
     * sql recode
    */

    /*public static void output(){
        try {
            OutputStreamWriter recodeWriter = new OutputStreamWriter(new FileOutputStream("src/main/resources/PropertiesRecode/recode.properties"));
            recodeMap.store(recodeWriter,null);
            recodeWriter.close();
        }catch (IOException ignored){}
    }

    private static void input(){
        try {
            InputStreamReader recodeReader = new InputStreamReader(new FileInputStream("src/main/resources/PropertiesRecode/recode.properties"));
            recodeMap.load(recodeReader);
            recodeReader.close();
        }catch (IOException ignored){}
    }*/
}

package com.jackied.frame.panes;

import com.jackied.backInterface.EventListener;
import com.jackied.frame.ui.MyMenuBar;
import com.jackied.staticValue;
import javafx.scene.layout.BorderPane;

import java.util.Map;
import java.util.Set;


public class MainPane extends BorderPane implements EventListener {
    public MainPane() {
        this.setLeft(new TreePane(this));
        this.setTop(new MyMenuBar());
    }

    @Override
    public void onEventOccurred() {
        Map<String, Map<String, String>> map = staticValue.query;
        int verticalListSize = 0;
        int crossListSize;
        Set<String> crossSet = map.keySet();
        crossListSize = crossSet.size() + 1;
        staticValue.recodeMap.clear();
        staticValue.DetailedInformation.clear();
        int x1 = 0;
        for (String key : crossSet) {
            if (map.get(key).get("isKey").equals("1")) {
                staticValue.recodeMap.put("key", "0," + x1);
                staticValue.keyVertical = x1;
            }
            staticValue.recodeMap.put(x1 + "," + 0, key);
            Set<String> content = map.get(key).keySet();
            if (x1 == 0) verticalListSize = content.size() + 1;
            for (String s : content) {
                if (Character.isDigit(s.charAt(0))) {
                    staticValue.recodeMap.put(x1 + "," + s, map.get(key).get(s)==null?"":map.get(key).get(s));
                }
            }

            String columnTypeName = map.get(key).get("ColumnTypeName");
            String columnPrecision = map.get(key).get("ColumnPrecision");
            String add = "(" + map.get(key).get("ColumnPrecision") + ")";
            if (columnTypeName.equals("CHAR") || columnPrecision.equals("")) {
                add = "";
            }
            staticValue.DetailedInformation.put(key, columnTypeName + add);
            staticValue.getHeaderFormX.put(x1, key);

            x1++;
        }
        staticValue.maxX = verticalListSize;
        staticValue.maxY = crossListSize;
        //staticValue.output();
        this.setCenter(new ExcelPane(verticalListSize, crossListSize));
    }

}

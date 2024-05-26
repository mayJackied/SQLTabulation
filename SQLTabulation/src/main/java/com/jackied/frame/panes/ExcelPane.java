package com.jackied.frame.panes;

import com.jackied.backInterface.Increased;
import com.jackied.frame.ui.ExcelTextFiled;
import com.jackied.staticValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ConcurrentModificationException;

public class ExcelPane extends BorderPane implements Increased{

    public ExcelPane(int verticalListSize,int crossListSize){
        staticValue.maxX = verticalListSize;
        staticValue.maxY = crossListSize;
        for (int y = 0; y < verticalListSize; y++) {
            HBox hBox = new HBox();
            for (int x = 0; x < crossListSize; x++) {
                ExcelTextFiled tf = new ExcelTextFiled(x, y,this);
                hBox.getChildren().add(tf);
            }
            vBox.getChildren().add(hBox);
        }
        this.setCenter(centerPane());
        initMaxLength();
    }

    ScrollPane scrollPane = new ScrollPane();
    VBox vBox = new VBox();

    private ScrollPane centerPane() {
        scrollPane.setContent(vBox);
        return scrollPane;
    }

    public void createVertical() {
        ObservableList<Node> hBoxes = vBox.getChildren();

        int x = ((HBox) hBoxes.get(0)).getChildren().size();
        for (int y = 0; y < hBoxes.size(); y++) {
            ExcelTextFiled tf = new ExcelTextFiled(x, y,this);
            ((HBox)hBoxes.get(y)).getChildren().add(tf);
        }
    }

    public void createCross(){
        ObservableList<Node> hBoxes = vBox.getChildren();
        int size = ((HBox) hBoxes.get(0)).getChildren().size();
        int y = hBoxes.size();
        HBox hBox = new HBox();
        for (int x = 0; x < size; x++) {
            ExcelTextFiled tf = new ExcelTextFiled(x, y,this);
            hBox.getChildren().add(tf);
        }
        vBox.getChildren().add(hBox);
    }

    private boolean getCrossText(int y){
        boolean isAllNull = true;
        for (Node excelTextFiled : ((HBox) (vBox.getChildren().get(y))).getChildren()) {
            if (!(((ExcelTextFiled)excelTextFiled).getText() == null)){
                if (!(((ExcelTextFiled)excelTextFiled).getText().equals(""))){
                    isAllNull = false;
                }
            }
        }
        return isAllNull;
    }

    private void initMaxLength(){
        for (String s : staticValue.query.keySet()) {
            if (staticValue.query.get(s).get("ColumnTypeName").equals("CHAR")) {
                staticValue.getMaxLengthFromHeader.put(s,1);
            } else {
                String columnPrecision = staticValue.query.get(s).get("ColumnPrecision");
                if (columnPrecision == null || columnPrecision.equals("")) staticValue.getMaxLengthFromHeader.put(s,-1);
                else staticValue.getMaxLengthFromHeader.put(s,Integer.parseInt(columnPrecision));
            }
        }
    }

    private void setKeyText(int y, String text){
        HBox hBox = (HBox) (vBox.getChildren().get(y));
        String key = (String) (staticValue.recodeMap.get("key"));
        String x = key.split(",")[0];
        ExcelTextFiled node = (ExcelTextFiled) hBox.getChildren().get(Integer.parseInt(x));
        node.setText(text);
    }

    private void deleteCross(int y){
        HBox hBox = (HBox) (vBox.getChildren().get(y));
        try {
            for (Node node : hBox.getChildren()) {
                ((ExcelTextFiled)node).setText("");
            }
        } catch (ConcurrentModificationException ignored) {}
    }

    private void deleteVertical(int x) {
        try {
            for (Node hBox : vBox.getChildren()) {
                Node node = ((HBox) hBox).getChildren().get(x);
                ((ExcelTextFiled)node).setText("");
            }
        } catch (ConcurrentModificationException ignored) {}
    }

    public static final int CREATE_VERTICAL = 0;
    public static final int CREATE_CROSS = 1;
    public static final int GET_CROSS_TEXT = 2;
    public static final int SET_KET_TEXT = 3;
    public static final int DELETE_CROSS = 4;
    public static final int DELETE_VERTICAL = 5;
    @Override
    public void Increase(int i) {
        switch (i){
            case CREATE_VERTICAL -> createVertical();
            case CREATE_CROSS -> createCross();
        }
    }

    @Override
    public String returnValue(int i,String... value) {
        String returnValue = "";
        switch (i){
            case GET_CROSS_TEXT -> returnValue = getCrossText(Integer.parseInt(value[0]))?"1":"0";
            case SET_KET_TEXT -> setKeyText(Integer.parseInt(value[0]),value[1]);
            case DELETE_CROSS -> deleteCross(Integer.parseInt(value[0]));
            case DELETE_VERTICAL -> deleteVertical(Integer.parseInt(value[0]));
        }
        return returnValue;
    }
}

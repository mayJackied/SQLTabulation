package com.jackied.frame.ui;

import com.jackied.frame.panes.TreePane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicBoolean;

public class NewTablePane extends VBox {
    public final byte NAME_INDEX = 0;
    public final byte TYPE_INDEX = 1;
    public final byte LENGTH_INDEX = 2;
    public final byte KEY_INDEX = 3;

    public final HBox centerHBox = new HBox();
    public final TextField name;
    public final Button button;

    private static byte HBoxLength = 8;

    String[] dataType = {"INT", "DOUBLE", "CHAR", "VARCHAR"};

    public NewTablePane() {
        super(10);
        name = TreePane.getTextFiled();
        name.setMaxWidth(280);
        button = TreePane.getButton("确      认");
        initCenterHBox();
        this.getChildren().addAll(name, centerHBox, button);
        this.setAlignment(Pos.CENTER);
    }

    private void initCenterHBox() {
        for (byte i = 0; i < HBoxLength; i++) {
            centerHBox.getChildren().add(creatCenterVBox(i));
        }
    }

    private VBox creatCenterVBox(int x) {
        TextField name = getTextFiled("数据名");

        name.textProperty().addListener((ObservableValue<? extends String> observableValue, String s, String t1) -> {
            if (x == HBoxLength - 1) {
                HBoxLength++;
                centerHBox.getChildren().add(creatCenterVBox(HBoxLength - 1));
            }
        });
        ComboBox<String> cb = new ComboBox<>();
        for (String s : dataType) {
            cb.getItems().add(s);
        }
        cb.setEditable(true);
        cb.setMaxWidth(105);

        CheckBox checkBox = new CheckBox("是否为key");
        if (x == 0) checkBox.setSelected(true);
        checkBox.setOnAction(event -> {
            ObservableList<Node> vBoxes = centerHBox.getChildren();
            for (Node vBox : vBoxes) {
                CheckBox box = (CheckBox) ((VBox) vBox).getChildren().get(3);
                box.setSelected(false);
            }
            checkBox.setSelected(true);
        });

        return new VBox(5, name, cb, getTextFiled("最大长度"), checkBox);
    }

    private TextField getTextFiled(String prompt) {
        TextField tf = new TextField();
        tf.setFocusTraversable(false);
        tf.setMaxSize(105, 27);
        tf.setMinSize(105, 27);

        tf.setPromptText(prompt);

        if (prompt.equals("最大长度")) {
            tf.textProperty().addListener((ObservableValue<? extends String> ob, String s, String t1) -> {
                try {
                    if (!Character.isDigit(t1.charAt(0))) tf.setText(s);
                } catch (StringIndexOutOfBoundsException ignored) {}
            });
        }


        tf.setStyle("-fx-border-color: black;\n" +
                "    -fx-border-width: 0.1;\n" +
                "    -fx-background-color: white;\n" +
                "    -fx-padding: 2;" +
                "-fx-background-radius: 0;\n" +
                "    -fx-border-radius: 0;");
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tf.setStyle("-fx-border-width: 1.5;" +
                        "-fx-border-color: DarkGreen");
            } else {
                tf.setStyle("-fx-border-color: black;\n" +
                        "    -fx-border-width: 0.1;\n" +
                        "    -fx-background-color: white;\n" +
                        "    -fx-padding: 2;" +
                        "-fx-background-radius: 0;\n" +
                        "    -fx-border-radius: 0;");
            }
        });
        return tf;
    }
}

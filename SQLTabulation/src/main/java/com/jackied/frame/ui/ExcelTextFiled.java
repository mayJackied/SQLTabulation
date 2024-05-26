package com.jackied.frame.ui;

import com.jackied.backInterface.Increased;
import com.jackied.frame.panes.ExcelPane;
import com.jackied.jdbc.sql;
import com.jackied.staticValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class ExcelTextFiled extends TextField {
    final int x;
    final int y;
    boolean extendVerticalHope = false;
    boolean extendCrossHope = false;
    private final Increased increased;
    private String onText = "";

    public ExcelTextFiled(int x, int y, Increased increased) {
        this.x = x;
        this.y = y;
        this.increased = increased;
        String key = x + "," + y;
        this.setText(staticValue.recodeMap.getProperty(key));

        if (staticValue.keyVertical == x) {
            staticValue.selectIDFormY.put(String.valueOf(y), this.getText());
            this.setEditable(false);
        }

        this.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            int maxLength = 1;
            if (staticValue.getMaxLengthFromHeader.get(staticValue.getHeaderFormX.get(x)) != null) {
                maxLength = staticValue.getMaxLengthFromHeader.get(staticValue.getHeaderFormX.get(x));
            }
            if (newValue != null && maxLength > 0 && y > 0) if (newValue.length() > maxLength) setText(oldValue);
        });

        if (x + 1 == staticValue.maxY) extendVerticalHope = true;

        if (y + 1 == staticValue.maxX) extendCrossHope = true;


        if (y == 0) {
            addTips();
            this.textProperty().addListener((ObservableValue<? extends String> ob, String s, String t1) -> addTips());
            this.setOnKeyReleased(event -> {
                if (event.getCode().toString().equals("DELETE")) {
                    sql.deleteVertical(staticValue.selectedDb, staticValue.selectedTb, this.getText());
                    increased.returnValue(ExcelPane.DELETE_VERTICAL, String.valueOf(x));
                }
            });
        } else {
            this.setOnKeyReleased(event -> {
                if (event.getCode().toString().equals("DELETE")) {
                    sql.deleteCross(staticValue.selectedDb, staticValue.selectedTb, staticValue.keyColumn, Integer.parseInt(staticValue.selectIDFormY.get(String.valueOf(y))));
                    increased.returnValue(ExcelPane.DELETE_CROSS, String.valueOf(y));
                }
            });
        }

        if (staticValue.recodeMap.get("key").equals(key)) {
            staticValue.keyColumn = this.getText();
            isKeyStyle();
        } else noKeyStyle();
        this.setMaxSize(105, 27);
        this.setMinSize(105, 27);

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) onText = this.getText() == null ? "" : this.getText();
            if (oldValue) {
                if (!(onText.equals(this.getText()))) {
                    if (y > 0 && !this.getText().equals("")) {
                        String s = staticValue.DetailedInformation.get(staticValue.getHeaderFormX.get(x));
                        if (s == null) s = "";
                        sql.addContent(staticValue.selectedDb, staticValue.selectedTb, staticValue.getHeaderFormX.get(x), staticValue.selectIDFormY.get(String.valueOf(y)), this.getText(), s.startsWith("I"), staticValue.selectIDFormY.get("0"));
                    } else if (y == 0 && !onText.equals("") && !this.getText().equals("")) {
                        String information = staticValue.DetailedInformation.get(onText);
                        staticValue.DetailedInformation.remove(onText);
                        staticValue.DetailedInformation.put(this.getText(), information);
                        sql.renameHeader(staticValue.selectedDb, staticValue.selectedTb, onText, this.getText(), staticValue.DetailedInformation.get(this.getText()));
                        addTips();
                    } else {
                        if (!this.getText().equals("")) {
                            String text = this.getText();
                            String[] split = text.split("/");
                            if (split.length > 1) {
                                StringBuilder name = new StringBuilder();
                                for (int i = 0; i < split.length - 1; i++) {
                                    name.append(split[i]).append("/");
                                }
                                name.deleteCharAt(name.length() - 1);
                                String information = split[split.length - 1];
                                staticValue.DetailedInformation.put(name.toString(), information);
                                addTips();
                                this.setText(name.toString());
                                sql.addVertical(staticValue.selectedDb, staticValue.selectedTb, name.toString(), information);
                            } else {
                                staticValue.DetailedInformation.put(text, "varchar(255)");
                                addTips();
                                sql.addVertical(staticValue.selectedDb, staticValue.selectedTb, text, "varchar(255)");
                            }
                        }
                    }
                }
            }
        });

        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && increased.returnValue(ExcelPane.GET_CROSS_TEXT, String.valueOf(y)).equals("1")) {
                String s = sql.addCross(staticValue.selectedDb, staticValue.selectedTb);
                staticValue.selectIDFormY.put(String.valueOf(y), s);
                increased.returnValue(ExcelPane.SET_KET_TEXT, String.valueOf(y), s);
            }
        });

        //this.textProperty().addListener((ObservableValue<? extends String> observableValue, String str, String t1) -> {
        //    String text = this.getText();
        //    if (text == null) text = "";
        //    staticValue.recodeMap.put(key, text);
        //});

        if (extendCrossHope) {
            extendCross();
        }

        if (extendVerticalHope) {
            extendVertical();
        }
    }

    private void isKeyStyle() {
        this.setStyle("-fx-border-color: black;" +
                "    -fx-border-width: 0.1;" +
                "    -fx-background-color: MediumVioletRed;" +
                "    -fx-padding: 2;" +
                "-fx-background-radius: 0;" +
                "    -fx-border-radius: 0;");
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.setStyle("-fx-border-width: 1.5;" +
                        "-fx-border-color: DarkGreen;" +
                        "-fx-background-color: Turquoise;");
            } else {
                this.setStyle("-fx-border-color: black;" +
                        "    -fx-border-width: 0.1;" +
                        "    -fx-background-color: MediumVioletRed;" +
                        "    -fx-padding: 2;" +
                        "-fx-background-radius: 0;" +
                        "    -fx-border-radius: 0;");
            }
        });
    }

    private void addTips() {
        String s = staticValue.DetailedInformation.get(this.getText());
        Tooltip tooltip = new Tooltip(s);
        this.setTooltip(tooltip);
    }

    private void noKeyStyle() {
        if (y == 0 || x == 0) {
            this.setStyle("-fx-border-color: black;" +
                    "    -fx-border-width: 0.1;" +
                    "    -fx-background-color: Gainsboro;" +
                    "    -fx-padding: 2;" +
                    "-fx-background-radius: 0;" +
                    "    -fx-border-radius: 0;");
        } else {
            this.setStyle("-fx-border-color: black;" +
                    "    -fx-border-width: 0.1;" +
                    "    -fx-background-color: white;" +
                    "    -fx-padding: 2;" +
                    "-fx-background-radius: 0;" +
                    "    -fx-border-radius: 0;");
        }

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.setStyle("-fx-border-width: 1.5;" +
                        "-fx-border-color: DarkGreen");
            } else {
                if (y == 0 || x == 0) {
                    this.setStyle("-fx-border-color: black;" +
                            "    -fx-border-width: 0.1;" +
                            "    -fx-background-color: Gainsboro;" +
                            "    -fx-padding: 2;" +
                            "-fx-background-radius: 0;" +
                            "    -fx-border-radius: 0;");
                } else {
                    this.setStyle("-fx-border-color: black;" +
                            "    -fx-border-width: 0.1;" +
                            "    -fx-background-color: white;" +
                            "    -fx-padding: 2;" +
                            "-fx-background-radius: 0;" +
                            "    -fx-border-radius: 0;");
                }
            }
        });
    }

    private void extendCross() {
        this.textProperty().addListener((ObservableValue<? extends String> observableValue, String str, String t1) -> {
            if (extendCrossHope) {
                staticValue.maxX++;
                extendCrossHope = false;
                increased.Increase(ExcelPane.CREATE_CROSS);
            }
        });
    }

    private void extendVertical() {
        this.textProperty().addListener((ObservableValue<? extends String> observableValue, String str, String t1) -> {
            if (extendVerticalHope) {
                staticValue.maxY++;
                extendVerticalHope = false;
                increased.Increase(ExcelPane.CREATE_VERTICAL);
            }
        });
    }
}

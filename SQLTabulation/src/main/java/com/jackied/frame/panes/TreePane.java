package com.jackied.frame.panes;

import com.jackied.backInterface.EventListener;
import com.jackied.frame.App;
import com.jackied.frame.ui.NewTablePane;
import com.jackied.jdbc.sql;
import com.jackied.staticValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TreePane extends AnchorPane {

    private final MenuItem menuItem1 = new MenuItem("新建");
    private final MenuItem menuItem2 = new MenuItem("删除");
    private final MenuItem menuItem3 = new MenuItem("重命名");
    private final MenuItem menuItem4 = new MenuItem("切换登录");

    private final TreeView<String> view = new TreeView<>();

    EventListener listener;

    public TreePane(EventListener listener) {
        this.listener = listener;
        ContextMenu contextMenu = new ContextMenu();

        TreeItem<String> root = new TreeItem<>(staticValue.loginMap.getProperty("lastIP") + ":" + staticValue.loginMap.getProperty("lastPort"));
        for (String db : staticValue.dbSet) {
            TreeItem<String> item = new TreeItem<>(db);
            for (String tb : staticValue.tbMap.get(db)) {
                TreeItem<String> tbItem = new TreeItem<>(tb);
                item.getChildren().add(tbItem);
            }
            root.getChildren().add(item);
        }

        view.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode().toString().equals("DELETE")) {
                TreeItem<String> selectedItem = view.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    if (staticValue.isClickedDb) deleteStage(true, staticValue.choseDb);
                    if (!staticValue.isClickedDb && !staticValue.isClickedRoot)
                        deleteStage(false, staticValue.selectedTb);
                }
            }
        });

        view.setRoot(root);

        view.setOnMouseClicked(event -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });

        view.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<String>> ob, TreeItem<String> stringTreeItem, TreeItem<String> t1) -> {
            staticValue.isClickedRoot = true;
            staticValue.isClickedDb = false;
            for (String dbName : staticValue.tbMap.keySet()) {
                if (t1.getValue().equals(dbName)) {
                    staticValue.isClickedDb = true;
                    staticValue.isClickedRoot = false;
                    staticValue.choseDb = dbName;
                }
                for (String tbName : staticValue.tbMap.get(dbName)) {
                    if (t1.getValue().equals(tbName)) {
                        staticValue.choseDb = dbName;
                        staticValue.selectedTb = tbName;
                        staticValue.selectedDb = dbName;
                        staticValue.isClickedDb = false;
                        staticValue.isClickedRoot = false;
                        staticValue.query = sql.query(staticValue.selectedDb, staticValue.selectedTb);
                        listener.onEventOccurred();
                    }
                }
            }
        });

        this.getChildren().add(view);

        view.setOnContextMenuRequested(event -> {
            TreeItem<String> selectedItem = view.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                contextMenu.getItems().clear();
                int level = getTreeItemLevel(selectedItem);
                updateContextMenu(contextMenu, level);
                contextMenu.show(view, event.getScreenX(), event.getScreenY());
            }
        });

        menuItem1.setOnAction(event -> {
            TreeItem<String> selectedItem = view.getSelectionModel().getSelectedItem();
            if (staticValue.isClickedRoot) newDataStage(NEW_DATABASES, selectedItem.getValue());
            if (staticValue.isClickedDb) newDataStage(NEW_TABLE, selectedItem.getValue());
        });
        menuItem2.setOnAction(event -> {
            if (staticValue.isClickedDb) deleteStage(true, staticValue.choseDb);
            if (!staticValue.isClickedDb && !staticValue.isClickedRoot) deleteStage(false, staticValue.selectedTb);
        });
        menuItem3.setOnAction(event -> {
            if (staticValue.isClickedDb) renameStage(true, staticValue.choseDb);
            if (!staticValue.isClickedDb && !staticValue.isClickedRoot) renameStage(false, staticValue.selectedTb);
        });
        menuItem4.setOnAction(event -> {
            App.mainStage.close();
            new loginStage(true);
        });

    }

    private void updateContextMenu(ContextMenu contextMenu, int level) {
        switch (level) {
            case 0 -> contextMenu.getItems().addAll(menuItem1, menuItem4);
            case 1 -> contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);
            case 2 -> contextMenu.getItems().addAll(menuItem2, menuItem3);
        }
    }

    private int getTreeItemLevel(TreeItem<?> item) {
        int level = 0;
        TreeItem<?> parent = item.getParent();
        while (parent != null) {
            level++;
            parent = parent.getParent();
        }
        return level;
    }

    private void renameStage(boolean renameDb, String name) {
        Stage stage = new Stage();
        stage.setTitle("rename");
        TextField tf = getTextFiled();
        tf.setPromptText("输入你心仪的名字");
        Button button = getButton("好好好好了");
        button.setOnAction(event -> {
            if (renameDb) sql.renameDatabase(name, tf.getText());
            else sql.renameTable(staticValue.selectedDb, name, tf.getText());
            renameNodeByName(renameDb, name, tf.getText());
            stage.close();
        });
        VBox vBox = new VBox(10, tf, button);
        vBox.setAlignment(Pos.CENTER);
        AnchorPane pane = new AnchorPane(vBox);
        AnchorPane.setLeftAnchor(vBox, 10.0);
        AnchorPane.setTopAnchor(vBox, 10.0);
        AnchorPane.setRightAnchor(vBox, 10.0);
        AnchorPane.setBottomAnchor(vBox, 10.0);
        stage.setScene(new Scene(pane));
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
    }

    private void renameNodeByName(boolean renameDb, String oldName, String newName) {
        TreeItem<String> root = view.getRoot();
        if (renameDb) {
            for (TreeItem<String> one : root.getChildren()) {
                if (one.getValue().equals(oldName)) {
                    one.setValue(newName);
                    staticValue.dbSet.remove(oldName);
                    staticValue.dbSet.add(newName);
                    HashSet<String> set = staticValue.tbMap.get(oldName);
                    staticValue.tbMap.remove(oldName);
                    staticValue.tbMap.put(newName, set);
                    staticValue.selectedDb = newName;
                    break;
                }
            }
        } else {
            for (TreeItem<String> one : root.getChildren()) {
                for (TreeItem<String> two : one.getChildren()) {
                    if (two.getValue().equals(oldName)) {
                        two.setValue(newName);
                        staticValue.tbMap.get(staticValue.selectedDb).remove(oldName);
                        staticValue.tbMap.get(staticValue.selectedDb).add(newName);
                        staticValue.selectedTb = newName;
                        break;
                    }
                }
            }
        }
    }

    private void deleteDatabase() {
        sql.deleteDatabase(staticValue.choseDb);
        removeNodeByName(true, staticValue.choseDb);
    }

    private void deleteTable() {
        sql.deleteTable(staticValue.selectedDb, staticValue.selectedTb);
        removeNodeByName(false, staticValue.selectedTb);
    }

    private void deleteStage(boolean removeDb, String name) {
        Stage stage = new Stage();
        stage.setTitle("delete");
        Button yes = getButton("是的,我很确定");
        yes.setOnAction(event -> {
            if (removeDb) deleteDatabase();
            else deleteTable();
            stage.close();
        });
        Button no = getButton("不,我搞错了");
        no.setOnAction(event -> stage.close());
        Label label = new Label("真的要删除" + name + "吗???");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        VBox vBox = new VBox(10, label, yes, no);
        vBox.setAlignment(Pos.CENTER);
        AnchorPane pane = new AnchorPane(vBox);
        AnchorPane.setLeftAnchor(vBox, 10.0);
        AnchorPane.setTopAnchor(vBox, 10.0);
        AnchorPane.setRightAnchor(vBox, 10.0);
        AnchorPane.setBottomAnchor(vBox, 10.0);
        stage.setScene(new Scene(pane));
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
    }

    private void removeNodeByName(boolean removeDb, String name) {
        TreeItem<String> root = view.getRoot();
        if (removeDb) {
            for (TreeItem<String> one : root.getChildren()) {
                if (one.getValue().equals(name)) {
                    root.getChildren().remove(one);
                    break;
                }
            }
        } else {
            for (TreeItem<String> one : root.getChildren()) {
                for (TreeItem<String> two : one.getChildren()) {
                    if (two.getValue().equals(name)) {
                        one.getChildren().remove(two);
                        break;
                    }
                }
            }
        }
    }

    private final int NEW_DATABASES = 0;
    private final int NEW_TABLE = 1;

    private void newDataStage(int type, String dbName) {
        String s1 = "新建Database";
        Stage stage = new Stage();
        Label label = new Label(s1);
        TextField name = getTextFiled();
        Button button = getButton("确      认");

        button.setOnAction(event -> {
            newDatabase(name.getText());
            stage.close();
        });

        VBox vBox;
        Scene scene;
        if (type == NEW_DATABASES) {
            vBox = new VBox(10, label, name, button);
            vBox.setAlignment(Pos.CENTER);
            stage.setHeight(170);

            AnchorPane pane = new AnchorPane(vBox);
            pane.setPadding(new Insets(20));
            AnchorPane.setTopAnchor(vBox, 0.0);
            AnchorPane.setBottomAnchor(vBox, 0.0);
            AnchorPane.setLeftAnchor(vBox, 0.0);
            AnchorPane.setRightAnchor(vBox, 0.0);
            scene = new Scene(pane);
        } else {
            NewTablePane newTablePane = new NewTablePane();
            vBox = newTablePane;
            stage.setHeight(400);
            ScrollPane scrollPane = new ScrollPane(vBox);

            newTablePane.button.setOnAction(event -> {
                newTable(newTablePane.name.getText(), dbName, getTableInformation(newTablePane));
                stage.close();
            });

            scene = new Scene(scrollPane);

            newTablePane.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode().toString().equals("ENTER")) {
                    newTable(newTablePane.name.getText(), dbName, getTableInformation(newTablePane));
                    stage.close();
                }
            });
        }

        name.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")) {
                newDatabase(name.getText());
                stage.close();
            }
        });

        stage.setScene(scene);

        stage.show();
    }

    private LinkedHashMap<String, Map<String, String>> getTableInformation(NewTablePane newTablePane) {
        LinkedHashMap<String, Map<String, String>> map = new LinkedHashMap<>();
        ObservableList<Node> VBoxes = newTablePane.centerHBox.getChildren();
        for (Node vBox : VBoxes) {
            ObservableList<Node> children = ((VBox) vBox).getChildren();
            if (!(((TextField) children.get(newTablePane.NAME_INDEX)).getText() == null) && !((TextField) children.get(newTablePane.NAME_INDEX)).getText().equals("")) {
                Map<String, String> withinMap = new HashMap<>();
                withinMap.put("ColumnTypeName", ((ComboBox<String>) children.get(newTablePane.TYPE_INDEX)).getValue());
                withinMap.put("ColumnPrecision", ((TextField) children.get(newTablePane.LENGTH_INDEX)).getText());
                withinMap.put("isKey", ((CheckBox) children.get(newTablePane.KEY_INDEX)).isSelected() ? "1" : "0");
                map.put(((TextField) children.get(newTablePane.NAME_INDEX)).getText(), withinMap);
            }
        }
        return map;
    }

    private void newDatabase(String name) {
        TreeItem<String> ti = new TreeItem<>(name);
        ti.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            staticValue.isClickedDb = true;
            staticValue.isClickedRoot = false;
            staticValue.selectedDb = name;
        });
        staticValue.dbSet.add(name);
        staticValue.tbMap.put(name, new HashSet<>());
        view.getRoot().getChildren().add(ti);
        sql.newDatabases(name);
    }

    private void newTable(String name, String dbName, LinkedHashMap<String, Map<String, String>> map) {
        TreeItem<String> ti = new TreeItem<>(name);
        ObservableList<TreeItem<String>> children = view.getRoot().getChildren();
        for (TreeItem<String> child : children) {
            if (child.getValue().equals(dbName)) {
                child.getChildren().add(ti);
                break;
            }
        }
        sql.newTable(name, dbName, map);

        for (String s : staticValue.tbMap.keySet()) {
            if (s.equals(dbName)) staticValue.tbMap.get(dbName).add(name);
        }

        ti.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            staticValue.selectedTb = name;
            staticValue.selectedDb = dbName;
            staticValue.isClickedDb = false;
            staticValue.isClickedRoot = false;
            staticValue.query = sql.query(dbName, name);
        });
    }

    private final static String onStyle = "-fx-background-color: white;";
    private final static String leaveStyle = "-fx-background-color: Gainsboro;";
    private final static String norStyle = "-fx-border-width: 0.3;-fx-border-color: black;";

    public static TextField getTextFiled() {
        TextField tf = new TextField();
        tf.setFocusTraversable(false);
        tf.setStyle(leaveStyle);
        tf.focusedProperty().addListener((ObservableValue<? extends Boolean> ob, Boolean a, Boolean t) -> {
            if (t) {
                tf.setStyle(onStyle);
            } else {
                if (tf.getText() == null || tf.getText().equals("")) {
                    tf.setStyle(leaveStyle);
                }
            }
        });
        tf.setFont(new Font("Arial", 16));
        tf.setPromptText("name");
        return tf;
    }

    public static Button getButton(String s) {
        Button b = new Button(s);
        b.setStyle(onStyle + norStyle);
        b.setOnMouseEntered(mouseEvent -> b.setStyle(leaveStyle + norStyle));
        b.setOnMouseExited(mouseEvent -> b.setStyle(onStyle + norStyle));
        b.setMinWidth(120);
        return b;
    }
}

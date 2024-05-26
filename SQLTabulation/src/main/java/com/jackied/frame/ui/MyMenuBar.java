package com.jackied.frame.ui;

import com.jackied.frame.App;
import com.jackied.frame.panes.WarningStage;
import com.jackied.frame.panes.loginStage;
import com.jackied.staticValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MyMenuBar extends MenuBar {
    public MyMenuBar() {
        Menu feature = new Menu("功能");
        Menu about = new Menu("关于");
        this.getMenus().addAll(feature, about);
        MenuItem reLogin = new MenuItem("切换登录");
        MenuItem setting = new MenuItem("设置");

        feature.getItems().addAll(reLogin, setting);

        MenuItem specification = new MenuItem("说明书");
        MenuItem readme = new MenuItem("自述文件");

        about.getItems().addAll(specification, readme);

        reLogin.setOnAction(event -> {
            App.mainStage.close();
            new loginStage(true);
        });

        setting.setOnAction(this::settingsStage);

        specification.setOnAction(event -> openDocument((new File("src/main/resources/texts/specification.txt")).getAbsolutePath()));

        readme.setOnAction(event -> openDocument((new File("src/main/resources/texts/readme.txt")).getAbsolutePath()));
    }

    private void openDocument(String path){
        try {
            Runtime.getRuntime().exec("explorer.exe " + path);
        } catch (IOException e) {
            new WarningStage(e.toString());
        }
    }

    private void settingsStage(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("设置");
        stage.setWidth(400);
        stage.setHeight(300);
        CheckBox isBuildInDatabases = new CheckBox("不展示MySQL自带的数据库");
        isBuildInDatabases.setSelected(staticValue.isBuildInDatabases);
        isBuildInDatabases.setOnAction(event1 -> {
            staticValue.isBuildInDatabases = isBuildInDatabases.isSelected();
            staticValue.loginMap.put("isLogInWithTheLastIP", staticValue.isLogInWithTheLastIP ? "1" : "0");
            staticValue.loginOutput();
        });

        CheckBox isSelected = new CheckBox("不展示登录界面,以上一次的IP进行登录");
        isSelected.setSelected(staticValue.isLogInWithTheLastIP);
        isSelected.setOnAction(event1 -> {
            staticValue.isLogInWithTheLastIP = isSelected.isSelected();
            staticValue.loginMap.put("isLogInWithTheLastIP", staticValue.isLogInWithTheLastIP ? "1" : "0");
            staticValue.loginOutput();
        });

        VBox vBox = new VBox(10,isBuildInDatabases,isSelected);
        AnchorPane ap = new AnchorPane(vBox);
        AnchorPane.setLeftAnchor(vBox,10.0);
        AnchorPane.setTopAnchor(vBox,10.0);
        stage.setScene(new Scene(ap));
        stage.show();
    }
}

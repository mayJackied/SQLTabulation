package com.jackied.frame.panes;

import com.jackied.backInterface.Increased;
import com.jackied.frame.App;
import com.jackied.jdbc.sql;
import com.jackied.staticValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class LoginPane extends AnchorPane {
    private final int h = 36;
    private final int w = 280;
    private final int num = 110;

    TextField ip;
    TextField port;
    TextField username;
    PasswordField pwd;

    Increased listener;
    public LoginPane(Increased listener) {
        this.listener = listener;
        ip = getTextFiled("请输入IP地址", w - num, h);
        port = getTextFiled("请输入端口号", 100, h);
        username = getTextFiled("请输入用户名", w, h);
        pwd = getPasswordField("请输入密码");

        Label label = new Label(":");
        label.setMinWidth(10);
        label.setMinHeight(h);
        label.setFont(new Font(20));
        HBox hBox = new HBox(ip, label, port);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(20, hBox, username, pwd, getCheckBox(), getButton());
        vBox.setAlignment(Pos.CENTER);
        this.getChildren().add(vBox);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);

        Font warningFont = Font.font("Microsoft YaHei", FontWeight.BOLD, 18);

        text2.setFont(warningFont);
        text3.setFont(warningFont);
    }

    private TextField getTextFiled(String s, int w, int h) {
        String key;
        if (w == this.w - num) {
            key = "lastIP";
        } else if (w == this.w) {
            key = "lastUsername";
        } else {
            key = "lastPort";
        }
        TextField tf = new TextField();
        tf.setFocusTraversable(false);
        tf.setText((String) staticValue.loginMap.get(key));
        tf.setStyle(staticValue.leaveStyle);
        tf.focusedProperty().addListener((ObservableValue<? extends Boolean> ob, Boolean a, Boolean t) -> {
            if (t) {
                tf.setStyle(staticValue.onStyle);
            } else {
                if (tf.getText() == null || tf.getText().equals("")) {
                    tf.setStyle(staticValue.leaveStyle);
                }
            }
        });
        tf.setFont(new Font("Arial", 16));
        tf.setPromptText(s);
        tf.setPrefWidth(w);
        tf.setPrefHeight(h);
        tf.setMaxWidth(w);
        return tf;
    }

    private final static Text text2 = new Text("现在连不上服务器了,根据报错信息来判断哪里的错误吧:");
    private final static Text text3 = new Text("可以去浏览器搜也可以去问AI哦");

    private Button getButton() {
        Button b = new Button("登        录");

        b.setStyle(staticValue.onStyle + staticValue.norStyle);

        b.setOnMouseEntered(mouseEvent -> b.setStyle(staticValue.leaveStyle + staticValue.norStyle));
        b.setOnMouseExited(mouseEvent -> b.setStyle(staticValue.onStyle + staticValue.norStyle));
        b.setMinWidth(w);
        b.setMinHeight(h - 5);
        b.setOnAction(event -> connectSql());
        return b;
    }

    public void connectSql(){
        String connect = sql.connect("jdbc:mysql://" + ip.getText() + ":" + port.getText() + "/", username.getText(), pwd.getText());
        if (connect.equals("0")) {
            staticValue.loginMap.put("lastPassword",pwd.getText());
            staticValue.loginMap.put("lastPort",port.getText());
            staticValue.loginMap.put("lastIP",ip.getText());
            staticValue.loginMap.put("lastUsername",username.getText());
            listener.Increase(loginStage.CLOSE_STAGE);
            MainPane mainPane = new MainPane();
            App.mainStage.setScene(new Scene(mainPane));
            App.mainStage.show();
            staticValue.loginOutput();
        }else {
            Text text = new Text(connect);
            text.setFont(Font.font("Helvetica", FontWeight.BOLD,26));
            TextFlow textFlow = new TextFlow(text);
            WarningStage warningStage = new WarningStage(connect,text2,textFlow,text3);
            warningStage.show();
        }
    }

    PasswordField pf = new PasswordField();

    private PasswordField getPasswordField(String s) {
        pf.setFocusTraversable(false);
        pf.setText((String) staticValue.loginMap.get("lastPassword"));
        pf.setStyle(staticValue.leaveStyle);
        pf.focusedProperty().addListener((ObservableValue<? extends Boolean> ob, Boolean a, Boolean t) -> {
            if (t) {
                pf.setStyle(staticValue.onStyle);
            } else {
                if (pf.getText() == null || pf.getText().equals("")) {
                    pf.setStyle(staticValue.leaveStyle);
                }
            }
        });
        pf.setFont(new Font("Arial", 16));
        pf.setPromptText(s);
        pf.setPrefWidth(w);
        pf.setPrefHeight(h);
        pf.setMaxWidth(w);
        return pf;
    }

    private CheckBox getCheckBox() {
        CheckBox checkBox = new CheckBox("默认以此IP登录");
        checkBox.setFont(new Font("Microsoft YaHei", 12));
        checkBox.setSelected(staticValue.isLogInWithTheLastIP);
        checkBox.setOnAction(event -> {
            staticValue.isLogInWithTheLastIP = checkBox.isSelected();
            staticValue.loginMap.put("isLogInWithTheLastIP", staticValue.isLogInWithTheLastIP ? "1" : "0");
            staticValue.loginOutput();
        });

        return checkBox;
    }
}

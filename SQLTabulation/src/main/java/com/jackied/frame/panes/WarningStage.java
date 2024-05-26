package com.jackied.frame.panes;

import com.jackied.staticValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class WarningStage extends Stage {
    public static final Font warningFont = Font.font("Microsoft YaHei", FontWeight.BOLD, 18);
    private final static Text text1 = new Text("出错了!!!");
    public WarningStage(String warningInformation,Node... arr) {
        text1.setFont(warningFont);
        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().add(text1);
        for (Node s : arr) {
            vBox.getChildren().add(s);
        }
        Button b  = new Button("复制错误信息");

        b.setStyle(staticValue.onStyle + staticValue.norStyle);

        b.setOnMouseEntered(mouseEvent -> b.setStyle(staticValue.leaveStyle + staticValue.norStyle));
        b.setOnMouseExited(mouseEvent -> b.setStyle(staticValue.onStyle + staticValue.norStyle));
        b.setMinWidth(280);
        b.setMinHeight(31 - 5);
        b.setOnAction(event -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferableText = new StringSelection(warningInformation);
            clipboard.setContents(transferableText, null);
            this.close();
        });
        vBox.getChildren().add(b);
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutY(-150);
        this.setTitle("出错了");
        this.setHeight(600);
        this.setWidth(600);
        this.setScene(new Scene(new ScrollPane(vBox)));
    }
}

package com.jackied.frame;

import com.jackied.frame.panes.loginStage;
import com.jackied.staticValue;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;

/**
 * @author Jackied
 * <p>
 * Begin with 24.5.8
 * <p>
 * end wiht ?
 */

public class App extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        int stageW = 960;
        int stageH = 514;
        if (!staticValue.isLogInWithTheLastIP) new loginStage(true);
        else new loginStage(false);

        stage.setTitle("");
        stage.setWidth(stageW);
        stage.setHeight(stageH);

        stage.setX((Toolkit.getDefaultToolkit().getScreenSize().width-stageW) / 2);
        stage.setY((Toolkit.getDefaultToolkit().getScreenSize().height-stageH) / 2);
    }
}

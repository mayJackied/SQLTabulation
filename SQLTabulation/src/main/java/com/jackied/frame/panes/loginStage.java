package com.jackied.frame.panes;

import com.jackied.backInterface.Increased;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class loginStage extends Stage implements Increased {
    public loginStage(boolean isShow){
        int stageW = 478;
        int stageH = 370;
        this.setHeight(stageH);
        this.setWidth(stageW);
        this.setTitle("登录");
        this.setX((Toolkit.getDefaultToolkit().getScreenSize().width-stageW) / 2);
        this.setY((Toolkit.getDefaultToolkit().getScreenSize().height-stageH) / 2);
        LoginPane loginPane = new LoginPane(this);
        this.setScene(new Scene(loginPane));

        if (isShow) this.show();
        else loginPane.connectSql();
    }

    public static final int CLOSE_STAGE = 0;
    @Override
    public void Increase(int i) {
        switch (i){
            case CLOSE_STAGE -> this.close();
        }
    }
}

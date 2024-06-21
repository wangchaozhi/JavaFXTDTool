package com.wangchaozhi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("TDEngine登录页面");
        // 禁止最大化和调整大小
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        double centerXPosition = ScreenUtil.getCenterXPosition(scene.getWidth());
        double centerYPosition = ScreenUtil.getCenterYPosition(scene.getHeight());
        // 设置窗口位置
        primaryStage.setX(centerXPosition);
        primaryStage.setY(centerYPosition);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

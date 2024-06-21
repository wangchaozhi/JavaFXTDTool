package com.wangchaozhi;

import com.taosdata.jdbc.TSDBDriver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    private Preferences prefs;

    @FXML
    private Button loginButton;


    public void initialize() {
        prefs = Preferences.userNodeForPackage(LoginController.class);
        if (prefs.getBoolean("rememberMe", false)) {
            addressField.setText(prefs.get("address", ""));
            portField.setText(prefs.get("port", ""));
            usernameField.setText(prefs.get("username", ""));
            passwordField.setText(prefs.get("password", ""));
            rememberMeCheckBox.setSelected(true);
        }
    }

    @FXML
    private void handleLogin() {
        String address = addressField.getText();
        String port = portField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (rememberMeCheckBox.isSelected()) {
            prefs.putBoolean("rememberMe", true);
            prefs.put("address", address);
            prefs.put("port", port);
            prefs.put("username", username);
            prefs.put("password", password);
        } else {
            prefs.putBoolean("rememberMe", false);
            prefs.remove("address");
            prefs.remove("port");
            prefs.remove("username");
            prefs.remove("password");
        }

        // 这里添加登录逻辑
        System.out.println("登录尝试: " + address + ":" + port + " 用户名: " + username + " 密码: " + password);
        try {
            Connection conn;
            if (portField.getText().equals("6041")) {
                conn = getConnRs(address, username, password);
            } else {
                conn = getConnNative(address, username, password);
            }
            if (null != conn) {
                TdEngine.setConnection(conn);
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
                List<Map<String, Object>> resultList = tdSet(resultSet);
                System.out.println(resultList);
                // 打开新窗口
                openDataDisplayWindow(resultList);
                statement.close();
            }

        } catch (Exception e) {
            // 弹出包含异常信息的对话框
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());

            showAlert("登录错误", e.getMessage());
//            showCustomDialog(e.getMessage());
            System.out.println(e.getMessage());
        }
    }


    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMaxWidth(300);
        alert.getDialogPane().setMaxHeight(50);
        alert.showAndWait();
    }


    public Connection getConnRs(String address, String username, String password) throws Exception {
//        Class.forName("com.taosdata.jdbc.TSDBDriver");
//        String jdbcUrl = "jdbc:TAOS://" + "tdengineone"+ ":6030/";
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://" + address + ":6041/";
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");
        connProps.setProperty("debugFlag", "135");
        connProps.setProperty("maxSQLLength", "1048576");
        connProps.setProperty("user", username);
        connProps.setProperty("password", password);
        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
        return conn;
    }

    public Connection getConnNative(String address, String username, String password) throws Exception {
        Class.forName("com.taosdata.jdbc.TSDBDriver");
        String jdbcUrl = "jdbc:TAOS://" + address + ":6030/";
//        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
//        String jdbcUrl = "jdbc:TAOS-RS://"+address+":6041/";
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");
        connProps.setProperty("debugFlag", "135");
        connProps.setProperty("maxSQLLength", "1048576");
        connProps.setProperty("user", username);
        connProps.setProperty("password", password);
        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
        return conn;
    }

    public Connection getConnNative3(String address, String username, String password) throws Exception{
        Class.forName("com.taosdata.jdbc.TSDBDriver");
        String jdbcUrl = "jdbc:TAOS://"+address+":6030/";
        Connection conn = DriverManager.getConnection(jdbcUrl, username,password);
        return conn;
    }

    public Connection getRestConn() throws Exception{
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://taosdemo.com:6041/power?user=root&password=taosdata";
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
        return conn;
    }


    private List<Map<String, Object>> tdSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                Object resultSetObject = resultSet.getObject(i);
                if (resultSetObject instanceof byte[]) {
                    resultSetObject = new String((byte[]) resultSetObject, StandardCharsets.UTF_8);
                }
                row.put(resultSet.getMetaData().getColumnName(i), resultSetObject);
            }
            resultList.add(row);
        }

        return resultList;
    }


    private void openDataDisplayWindow(List<Map<String, Object>> data) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DataBasesDisplay.fxml"));
        Parent root = loader.load();
        DataBasesDisplayController controller = loader.getController();
        controller.setData(data);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("数据库列表");
        stage.show();
    }


}

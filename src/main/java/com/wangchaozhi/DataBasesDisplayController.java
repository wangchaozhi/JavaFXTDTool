package com.wangchaozhi;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author wangchaozhi
 */
public class DataBasesDisplayController {

    @FXML
    private TableView<Map<String, Object>> tableView;

    public void setData(List<Map<String, Object>> data) {
        // 确保 TableView 是空的
        tableView.getColumns().clear();


        if (!data.isEmpty()) {
            // 为每个键创建列
            Map<String, Object> firstRow = data.get(0);
            for (String key : firstRow.keySet()) {
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
                column.setCellValueFactory(new MapValueFactory(key));

                if ("name".equals(key)) {
                    // 对 "name" 列使用特殊的单元格工厂
                    column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
                        final Button btn = new Button();

                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                // 设置按钮文本为 name 列的值
                                btn.setText(item.toString());
                                btn.setOnAction(event -> {
                                    // 按钮点击事件处理逻辑
                                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                                    showNextList(rowData);
                                });
                                setGraphic(btn);
                                setAlignment(Pos.CENTER);
                            }
                        }
                    });
                } else {
                    // 对其他列使用普通的文本居中单元格
                    column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                setText(item.toString());
                                setAlignment(Pos.CENTER);
                            }
                        }
                    });
                }
                tableView.getColumns().add(column);
            }
        }

        // 将数据转换为 ObservableList 并设置到 TableView
        ObservableList<Map<String, Object>> observableData = FXCollections.observableArrayList(data);
        tableView.setItems(observableData);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }


    private void showNextList(Map<String, Object> rowData) {
        String name = (String) rowData.get("name");
        Connection connection = TdEngine.getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute("use " + name);
            ResultSet show_tables = statement.executeQuery("show stables ");
            List<Map<String, Object>> maps = ResultSetUtils.tdSet(show_tables);
            statement.close();
            System.out.println(maps);
            showTableData(maps);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
        }
        // 在这里处理显示下一个列表的逻辑
        // rowData 包含了被点击行的数据
    }

    private void showTableData(List<Map<String, Object>> data) {
        if(data.size()==0){
            return;
        }
        Stage stage = new Stage();
        TableView<Map<String, Object>> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(data));

        // 计算 "name" 列最大宽度
        double maxWidth = 0;
        Text text = new Text();
        text.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 12));
        for (Map<String, Object> row : data) {
            if (row.containsKey("name")) {
                String nameValue = row.get("name").toString();
                text.setText(nameValue);
                double textWidth = text.getLayoutBounds().getWidth();
                maxWidth = Math.max(maxWidth, textWidth);
            }
        }
        TableColumn<Map<String, Object>, Object> nameColumn = null;
        for (String key : data.get(0).keySet()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
            column.setCellValueFactory(new MapValueFactory(key));

            column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
                final Button btn = new Button();
                final ContextMenu contextMenu = new ContextMenu();

                {
                    // 添加菜单项
                    MenuItem item1 = new MenuItem("子表查询");
                    MenuItem item2 = new MenuItem("sql查询");
                    MenuItem item3 = new MenuItem("表结构");
                    item1.setOnAction(e -> handleAction1(btn.getText(),getTableView().getItems().get(getIndex())));
                    item2.setOnAction(e -> handleAction2(btn.getText(),getTableView().getItems().get(getIndex())));
                    item3.setOnAction(e -> handleAction3(btn.getText(),getTableView().getItems().get(getIndex())));

                    contextMenu.getItems().addAll(item1, item2,item3);

                    // 设置按钮的点击事件
                    btn.setOnAction(event -> {
                        contextMenu.show(btn, Side.BOTTOM, 0, 0);
                        event.consume(); // 防止事件进一步传播
                    });

                    // 设置按钮的右键点击事件
                    btn.setOnContextMenuRequested(event -> {
                        contextMenu.show(btn, event.getScreenX(), event.getScreenY());
                    });
                }

                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        if (item instanceof String && "name".equals(key)) {
                            btn.setText(item.toString());
                            setGraphic(btn);
                        } else {
                            setText(item.toString());
                            setGraphic(null);
                        }
                        setAlignment(Pos.CENTER);
                    }
                }
            });

            tableView.getColumns().add(column);
            if ("name".equals(key)) {
                nameColumn = column;
            }
        }

        // 设置 "name" 列的宽度
        if (nameColumn != null) {
            nameColumn.setPrefWidth(maxWidth*3); // 加上一些额外的空间
        }

        // 设置列宽自适应
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        AnchorPane anchorPane = new AnchorPane(tableView);
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);

        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);
        stage.setTitle("超级表列表");
        stage.show();
    }

    private void handleAction1(String name, Map<String, Object> rowData) {

        Connection connection = TdEngine.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet showTables = statement.executeQuery("SHOW TABLES LIKE '" + name + "%'");
            List<Map<String, Object>> maps = ResultSetUtils.tdSet(showTables);
            statement.close();
            System.out.println(maps);
            showTableData2(name,maps);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        // 处理操作2
        System.out.println("操作1");
    }

    private void handleAction2(String name, Map<String, Object> rowData) {
        // 处理操作1
        System.out.println("操作2");
        handleNameButtonClick2(name);
    }


    private void handleAction3(String name, Map<String, Object> rowData) {
        Connection connection = TdEngine.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet showTables = statement.executeQuery("describe " + name );
            List<Map<String, Object>> maps = ResultSetUtils.tdSet(showTables);
            statement.close();
            System.out.println(maps);
            showTableData2(name,maps);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    private void subHandleAction1(String name) {
        // 处理操作1
        System.out.println("子表操作1");
//        handleNameButtonClick2(name);
    }

    private void subHandleAction2(String name) {



        // 处理操作1
        System.out.println("子表操作2");
//        handleNameButtonClick2(name);
    }




//    private void showTableData(List<Map<String, Object>> data) {
//        if(data.size()==0){
//            return;
//        }
//        Stage stage = new Stage();
//        TableView<Map<String, Object>> tableView = new TableView<>();
//        tableView.setItems(FXCollections.observableArrayList(data));
//
//        // 计算 "name" 列最大宽度
//        double maxWidth = 0;
//        Text text = new Text();
//        text.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 12));
//        for (Map<String, Object> row : data) {
//            if (row.containsKey("name")) {
//                String nameValue = row.get("name").toString();
//                text.setText(nameValue);
//                double textWidth = text.getLayoutBounds().getWidth();
//                maxWidth = Math.max(maxWidth, textWidth);
//            }
//        }
//        TableColumn<Map<String, Object>, Object> nameColumn = null;
//        for (String key : data.get(0).keySet()) {
//            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
//            column.setCellValueFactory(new MapValueFactory(key));
//
//            // 对所有列使用自定义的单元格工厂来实现居中对齐
//            column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
//                @Override
//                protected void updateItem(Object item, boolean empty) {
//                    super.updateItem(item, empty);
//                    if (empty) {
//                        setText(null);
//                        setGraphic(null);
//                    } else {
//                        if (item instanceof String && "name".equals(key)) {
//                            Button btn = new Button(item.toString());
//                            btn.setOnAction(event -> handleNameButtonClick(item.toString()));
//                            setGraphic(btn);
//                            setText(null);
//                        } else {
//                            setText(item.toString());
//                            setGraphic(null);
//                        }
//                        setAlignment(Pos.CENTER);
//                    }
//                }
//            });
//
//            tableView.getColumns().add(column);
//            if ("name".equals(key)) {
//                nameColumn = column;
//            }
//        }
//
//        // 设置 "name" 列的宽度
//        if (nameColumn != null) {
//            nameColumn.setPrefWidth(maxWidth*3); // 加上一些额外的空间
//        }
//
//        // 设置列宽自适应
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        AnchorPane anchorPane = new AnchorPane(tableView);
//        AnchorPane.setTopAnchor(tableView, 0.0);
//        AnchorPane.setRightAnchor(tableView, 0.0);
//        AnchorPane.setBottomAnchor(tableView, 0.0);
//        AnchorPane.setLeftAnchor(tableView, 0.0);
//
//        Scene scene = new Scene(anchorPane);
//        stage.setScene(scene);
//        stage.setTitle("超级表列表");
//        stage.show();
//    }

//    private void showTableData(List<Map<String, Object>> data) {
//        Stage stage = new Stage();
//        TableView<Map<String, Object>> tableView = new TableView<>();
//        tableView.setItems(FXCollections.observableArrayList(data));
//
//        if (!data.isEmpty()) {
//            Map<String, Object> firstRow = data.get(0);
//            for (String key : firstRow.keySet()) {
//                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
//                column.setCellValueFactory(new MapValueFactory(key));
//
//                // 对所有列使用自定义的单元格工厂来实现居中对齐
//                column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
//                    @Override
//                    protected void updateItem(Object item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setText(null);
//                            setGraphic(null);
//                        } else {
//                            if (item instanceof String && "name".equals(key)) {
//                                // 如果是 "name" 列，则创建按钮
//                                Button btn = new Button(item.toString());
//                                btn.setOnAction(event -> handleNameButtonClick(item.toString()));
//                                setGraphic(btn);
//                                setText(null);
//                            } else {
//                                // 其他列则显示文本
//                                setText(item.toString());
//                                setGraphic(null);
//                            }
//                            setAlignment(Pos.CENTER); // 设置居中对齐
//                        }
//                    }
//                });
//
//                tableView.getColumns().add(column);
//            }
//        }
//        // 设置列宽自适应
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        AnchorPane anchorPane = new AnchorPane(tableView);
//        AnchorPane.setTopAnchor(tableView, 0.0);
//        AnchorPane.setRightAnchor(tableView, 0.0);
//        AnchorPane.setBottomAnchor(tableView, 0.0);
//        AnchorPane.setLeftAnchor(tableView, 0.0);
//        Scene scene = new Scene(anchorPane);
//        stage.setScene(scene);
//        stage.setTitle("超级表列表");
//        stage.show();
//    }

    private void showTableData2(String name,List<Map<String, Object>> data) {
        Stage stage = new Stage();
        TableView<Map<String, Object>> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(data));

        if (!data.isEmpty()) {
            Map<String, Object> firstRow = data.get(0);
            for (String key : firstRow.keySet()) {
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
                column.setCellValueFactory(new MapValueFactory(key));

                if ("table_name".equals(key)) {
                    // 对 "name" 列使用特殊的单元格工厂来创建按钮
                    column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {


                        final Button btn = new Button();
                        final ContextMenu contextMenu = new ContextMenu();

                        {
                            // 添加右键菜单项
                            MenuItem item1 = new MenuItem("sql查询");
                            MenuItem item2 = new MenuItem("删除该表");
                            contextMenu.getItems().addAll(item1, item2);

                            // 设置菜单项的行为
                            item1.setOnAction(e -> subHandleAction1(btn.getText()));
                            item2.setOnAction(e -> subHandleAction2(btn.getText()));

                            // 设置按钮的右键点击事件
                            btn.setOnContextMenuRequested(event -> {
                                contextMenu.show(btn, event.getScreenX(), event.getScreenY());
                            });
                        }

                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btn.setText(item.toString());
                                btn.setOnAction(event -> {
                                    handleNameButtonClick2(item.toString());
                                });
                                setGraphic(btn);
                            }
                        }
//                        final Button btn = new Button();
//
//                        @Override
//                        protected void updateItem(Object item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (empty) {
//                                setGraphic(null);
//                            } else {
//                                btn.setText(item.toString());
//                                btn.setOnAction(event -> {
//                                    // 按钮点击事件处理逻辑
//                                    // 例如，您可以在此处调用另一个方法来处理点击事件
//                                    handleNameButtonClick2(item.toString());
//                                });
//                                setGraphic(btn);
//                            }
//                        }
                    });
                }
                tableView.getColumns().add(column);
            }
        }
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // 设置列宽自适应

        // 创建标签用于显示数据条数
        Label countLabel = new Label("总条数: " + data.size());
        countLabel.setAlignment(Pos.CENTER_RIGHT);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(tableView, countLabel);

//        AnchorPane anchorPane = new AnchorPane(tableView);
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);

        // 设置标签的锚点
        AnchorPane.setBottomAnchor(countLabel, 5.0);
        AnchorPane.setRightAnchor(countLabel, 10.0);
//        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);
        stage.setTitle(name+"子表列表");
        stage.show();
    }

    private void handleNameButtonClick(String name)  {
        Connection connection = TdEngine.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet showTables = statement.executeQuery("SHOW TABLES LIKE '" + name + "%'");
            List<Map<String, Object>> maps = ResultSetUtils.tdSet(showTables);
            statement.close();
            System.out.println(maps);
            showTableData2(name,maps);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    private void handleNameButtonClick2(String name) {

        ProgressIndicator progressIndicator = new ProgressIndicator();
        // 初始时不可见
        progressIndicator.setVisible(false);

        Stage dialogStage = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        TextArea textArea = new TextArea();
        textArea.setPromptText("输入SQL命令");
        textArea.setPrefHeight(200);
        // 创建一个HBox用于水平排列按钮
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);

        Button queryLatestButton = new Button("查询最新1条");
        queryLatestButton.setOnAction(event -> textArea.setText("SELECT * FROM " + name + " ORDER BY TS DESC LIMIT 1"));

        Button queryTop100Button = new Button("查询最新前100条");
        queryTop100Button.setOnAction(event -> textArea.setText("SELECT * FROM " + name + " ORDER BY TS DESC LIMIT 100"));

        hbox.getChildren().addAll(queryLatestButton, queryTop100Button);

        Button executeButton = new Button("执行");
        executeButton.setOnAction(event -> {
            String textContent = textArea.getText();
            if (textContent == null || textContent.trim().isEmpty()) {
                // 显示警告对话框
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("SQL命令不能为空，请输入命令后再尝试执行。");
                alert.show();
            } else {
                System.out.println("执行的SQL命令: " + textContent);
                progressIndicator.setVisible(true); // 显示加载动画
                executeSql(textContent, progressIndicator); // 执行 SQL
            }
        });
//        executeButton.setOnAction(event -> {
//            String textContent = textArea.getText();
//            System.out.println("执行的SQL命令: " + textContent);
//            progressIndicator.setVisible(true); // 显示加载动画
//            if(null!=textContent&&!"".equals(textContent.trim())){
//            executeSql(textContent, progressIndicator); // 执行 SQL，并将进度指示器作为参数传递
//            }else {
//                progressIndicator.setVisible(false); // 隐藏加载动画
//            }
////            executeSql(textContent);
//        });


        vbox.getChildren().addAll(hbox, textArea, executeButton,progressIndicator);
        // 将组件添加到 AnchorPane，并设置锚点
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);

        anchorPane.getChildren().addAll(vbox);
        Scene scene = new Scene(anchorPane, 400, 300);
       // Scene scene = new Scene(vbox, 400, 300);
        dialogStage.setScene(scene);
        dialogStage.setTitle("SQL编辑器 - " + name);
//        dialogStage.setResizable(false); // 允许调整窗口大小
        dialogStage.show();
    }

    public void executeSql(String sql, ProgressIndicator progressIndicator) {
        new Thread(() -> {
            try {
                Connection connection = TdEngine.getConnection();
                Statement statement = connection.createStatement();
                ResultSet show_tables = statement.executeQuery(sql);
                List<Map<String, Object>> maps = ResultSetUtils.tdSet(show_tables);
                statement.close();

                // 在 JavaFX 主线程中更新 UI
                Platform.runLater(() -> {
                    showTableData2("数据", maps); // 在 UI 线程中显示数据
                    progressIndicator.setVisible(false); // 隐藏加载动画
                    System.out.println(maps);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // 即使出错也要确保隐藏加载动画
                    // 显示错误消息
                });
            }
        }).start();
    }





}

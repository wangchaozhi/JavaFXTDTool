package com.wangchaozhi;

public class remark {

    //    public void showCustomDialog(String message) {
//        // 创建 Stage 和布局
//        Stage dialogStage = new Stage();
//        VBox vbox = new VBox(new Label(message));
//        vbox.setAlignment(Pos.CENTER);
//
//        // 设置 Scene 和 Stage 属性
//        Scene scene = new Scene(vbox, 200, 50);
//        double centerXPosition = ScreenUtil.getCenterXPosition(scene.getWidth());
//        double centerYPosition = ScreenUtil.getCenterYPosition(scene.getHeight());
//        dialogStage.setX(centerXPosition);
//        dialogStage.setY(centerYPosition);
//        dialogStage.setScene(scene);
//        dialogStage.setTitle("自定义弹窗");
//        dialogStage.initModality(Modality.APPLICATION_MODAL);
//
//        // 显示弹窗
//        dialogStage.showAndWait();
//    }



    //    private void showTableData(List<Map<String, Object>> data) {
//        Stage stage = new Stage();
//        TableView<Map<String, Object>> tableView = new TableView<>();
//        tableView.setItems(FXCollections.observableArrayList(data));
//
//        // 假设数据的第一行包含所有键
//        if (!data.isEmpty()) {
//            Map<String, Object> firstRow = data.get(0);
//            for (String key : firstRow.keySet()) {
//                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
//                // 使用 MapValueFactory
//                column.setCellValueFactory(new MapValueFactory(key));
//                tableView.getColumns().add(column);
//            }
//        }
//
//        VBox vbox = new VBox(tableView);
//        Scene scene = new Scene(vbox);
//        stage.setScene(scene);
//        stage.setTitle("超级表列表");
//        stage.show();
//    }

}

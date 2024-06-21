package com.wangchaozhi;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结果集utils
 *
 * @author wangchaozhi
 * @date 2023/11/29
 */
public  class ResultSetUtils {

    public static List<Map<String, Object>>  tdSet(ResultSet resultSet) throws SQLException {
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



}

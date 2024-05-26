package com.jackied.jdbc;

import com.jackied.staticValue;

import java.sql.*;
import java.util.*;

public class sql {
    public static String dbUrl;
    public static String username;
    public static String password;
    private static Connection conn = null;
    private static Statement stmt = null;
    private static final String end = "?useSSL=false";

    public static String connect(String url, String username, String password) {
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url + end, username, password);

            stmt = conn.createStatement();

            String sql = "SHOW DATABASES;";
            rs = stmt.executeQuery(sql);

            dbUrl = url;

            loop:
            while (rs.next()) {
                String dbName = rs.getString(1);
                if (staticValue.isBuildInDatabases) {
                    for (String s : staticValue.buildInDatabasesArr) {
                        if (s.equals(dbName)) continue loop;
                    }
                }
                staticValue.dbSet.add(dbName);
            }
        } catch (SQLException e) {
            return e.toString();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) {
            }
        }
        sql.username = username;
        sql.password = password;
        for (String s : staticValue.dbSet) {
            readTable(s);
        }
        finalClose();
        return "0";
    }

    public static void readTable(String s) {
        HashSet<String> tbSet = new HashSet<>();
        try {
            stmt.execute("USE " + s + ";");
            ResultSet rs = stmt.executeQuery("SHOW TABLES;");

            while (rs.next()) {
                String tbName = rs.getString(1);
                tbSet.add(tbName);
            }
            staticValue.tbMap.put(s, tbSet);
        } catch (SQLException ignored) {
        }
    }

    private static void firstConnect(String url) {
        try {
            conn = DriverManager.getConnection(url + end, username, password);
            stmt = conn.createStatement();
        } catch (SQLException ignored) {
        }
    }

    private static void finalClose() {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }

    /**
     * List<Map<String,Map<String,String>>>
     */
    public static Map<String, Map<String, String>> query(String dbName, String tbName) {
        Map<String, Map<String, String>> nameOfKey = new LinkedHashMap<>();
        String key = "";
        firstConnect(dbUrl + dbName);
        try {
            conn = DriverManager.getConnection(dbUrl + dbName + end, username, password);
            stmt = conn.createStatement();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tbName);
            while (primaryKeys.next()) {
                key = primaryKeys.getString("COLUMN_NAME");
            }

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tbName);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Map<String, String> map = new HashMap<>();
                map.put("ColumnTypeName", rsmd.getColumnTypeName(i));
                if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
                    map.put("ColumnPrecision", String.valueOf(rsmd.getPrecision(i)));
                } else map.put("ColumnPrecision", "");
                map.put("isKey", columnName.equals(key) ? "1" : "0");
                nameOfKey.put(columnName, map);
            }

            Object[] temp = nameOfKey.keySet().toArray();
            for (int count = 1; rs.next(); count++) {
                for (int i = 0; i < columnCount; i++) {
                    String s = (String) temp[i];
                    String columnValue = rs.getString(i + 1);
                    nameOfKey.get(s).put(String.valueOf(count), columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
        return nameOfKey;
    }

    public static void addContent(String dbName, String tbName, String headName, String key, String content, boolean isINT, String keyName) {
        firstConnect(dbUrl + dbName);
        String sql;
        if (isINT) sql = "update " + tbName + " set " + headName + " = " + content + " where " + keyName + " = " + key;
        else sql = "update " + tbName + " set " + headName + " = '" + content + "' where " + keyName + " = " + key;
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finalClose();
    }

    private static final StringBuilder sbKey = new StringBuilder();
    private static final StringBuilder sbValues = new StringBuilder();

    public static String addCross(String dbName, String tbName) {
        firstConnect(dbUrl + dbName);
        for (int i = 1; i < staticValue.getHeaderFormX.size(); i++) {
            sbKey.append(staticValue.getHeaderFormX.get(i));
            sbValues.append("NULL");
            if (i < staticValue.getHeaderFormX.size() - 1) {
                sbKey.append(", ");
                sbValues.append(", ");
            }
        }
        String sql = "insert into " + tbName + "(" + sbKey + ")" + "values(" + sbValues + ")";
        sbKey.delete(0, sbKey.length());
        sbValues.delete(0, sbValues.length());

        int generatedKey = 0;

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();

        return String.valueOf(generatedKey);
    }

    public static void deleteCross(String dbName, String tbName, String keyColumn, int key) {
        firstConnect(dbUrl + dbName);
        String sql = "DELETE FROM " + tbName + " WHERE " + keyColumn + " = '" + key + "'";

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ignored) {
        }
        finalClose();
    }

    public static void newDatabases(String name) {
        firstConnect(dbUrl);
        try {
            String sql = "CREATE DATABASE " + name;
            stmt.executeUpdate(sql);
        } catch (SQLException ignored) {
        }
        finalClose();
    }

    public static void newTable(String name, String dbName, LinkedHashMap<String, Map<String, String>> map) {
        firstConnect(dbUrl + dbName);
        String isKey = " PRIMARY KEY AUTO_INCREMENT,";
        String noKey = " ,";
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (");
        for (String s : map.keySet()) {
            Map<String, String> withinMap = map.get(s);
            if (withinMap.get("ColumnPrecision").equals("")) {
                if (withinMap.get("isKey").equals("1"))
                    sql.append(s).append(" ").append(withinMap.get("ColumnTypeName")).append(isKey);
                else sql.append(s).append(" ").append(withinMap.get("ColumnTypeName")).append(noKey);
            } else {
                if (withinMap.get("isKey").equals("1"))
                    sql.append(s).append(" ").append(withinMap.get("ColumnTypeName")).append("(").append(withinMap.get("ColumnPrecision")).append(") ").append(isKey);
                else
                    sql.append(s).append(" ").append(withinMap.get("ColumnTypeName")).append("(").append(withinMap.get("ColumnPrecision")).append(") ").append(noKey);
            }
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")");
        try {
            stmt.execute(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }

    public static void deleteTable(String dbName, String tbName) {
        firstConnect(dbUrl + dbName);
        try {
            String sql = "DROP TABLE " + tbName;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDatabase(String dbName) {
        firstConnect(dbUrl);
        String sql = "DROP DATABASE " + dbName;
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }

    public static void renameTable(String dbName, String oldTbName, String newTbName) {
        firstConnect(dbUrl + dbName);
        String sql = "RENAME TABLE " + oldTbName + " TO " + newTbName;
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }

    public static void renameDatabase(String oldDbName, String newDbName) {
        firstConnect(dbUrl);
        Statement renameStmt = null;
        ResultSet rs = null;
        try {
            stmt.executeUpdate("CREATE DATABASE " + newDbName);

            rs = stmt.executeQuery("SHOW TABLES FROM " + oldDbName);
            renameStmt = conn.createStatement();
            while (rs.next()) {
                String tableName = rs.getString(1);

                String renameTableSQL = "RENAME TABLE " + oldDbName + "." + tableName +
                        " TO " + newDbName + "." + tableName;
                renameStmt.executeUpdate(renameTableSQL);
            }
            rs.close();
            renameStmt.close();
            stmt.executeUpdate("DROP DATABASE " + oldDbName);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (renameStmt != null) renameStmt.close();
                finalClose();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void renameHeader(String dbName, String tbName, String oldName, String newName, String information) {
        firstConnect(dbUrl + dbName);
        try {
            String sql = "ALTER TABLE " + tbName + " CHANGE COLUMN " + oldName + " `" + newName + "` " + information;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }

    public static void addVertical(String dbName, String tbName, String headName, String information) {
        firstConnect(dbUrl + dbName);
        try {
            String sql = "ALTER TABLE " + tbName + " ADD COLUMN `" + headName + "` " + information;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }

    public static void deleteVertical(String dbName, String tbName, String headName) {
        firstConnect(dbUrl+dbName);
        try {
            String sql = "ALTER TABLE "+tbName+" DROP COLUMN "+headName;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalClose();
    }
}

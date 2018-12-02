package com.paypal.desk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final String DATABASE_IP = "127.0.0.1";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_NAME = "paypal";

    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + DATABASE_IP +
                            ":" + DATABASE_PORT +
                            "/" + DATABASE_NAME,
                    "postgres",
                    "2505"
            );

            System.out.println("Connection successful");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int createUser(String firstName, String lastName) {
        String sql = "insert into users " +
                "(first_name, last_name)" +
                " values (" +
                "'" + firstName + "'" +
                ", " +
                "'" + lastName + "'" +
                ")";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates the user balance in database
     * Sets balance = balance + amount
     *
     * @param userId id of the user in users table
     * @param amount double value of the amount to insert
     */
    static void cashFlow(int userId, double amount) {
        String sql = "update users " +
                "set balance = balance " + amount +
                "where id = " + userId;

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            String userBalance = "select balance from users where id = " + Integer.toString(userId);
            Statement balanceStatement = connection.createStatement();
            ResultSet resultSet = balanceStatement.executeQuery(userBalance);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Emulates a transaction between 2 users
     * Takes money from one account and adds to another account
     *
     * @param userFrom source user id
     * @param userTo   target user id
     * @param amount   transaction amount
     */
    static void transaction(int userFrom, int userTo, double amount) {
        String sql = "update users set balance = balance - " + amount + "where id = " + userFrom + ";" +
                     "update users set balance = balance + " + amount + "where id = " + userTo + ";" ;

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<User> listUsers() {
        String sql = "select * from users";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<User> userList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                double balance = resultSet.getDouble("balance");

                userList.add(new User(
                        id, firstName, lastName, balance
                ));
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

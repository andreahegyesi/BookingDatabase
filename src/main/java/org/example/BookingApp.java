package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Booking application with a database
 * Inserting data in the tables using the PreparedStatement
 * Interrogating the database and printing out the prices for each room the database
 */
public class BookingApp {
    public static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";
    static final String USER = "postgres";
    static final String PASS = "root";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement preparedStatement = null;
        Room room1 = new Room(22531, "standard", "double", 2, "private bathroom, without balcony");
        Room room2 = new Room(23657, "superior", "queen", 2, "private bathroom, with balcony");
        Room room3 = new Room(49878, "family", "twin", 4, "extra twin beds");
        List<Room> roomList = new ArrayList<>();
        roomList.add(room1);
        roomList.add(room2);
        roomList.add(room3);
        RoomFair roomFair1 = new RoomFair(221, 80.4, "summer");
        RoomFair roomFair2 = new RoomFair(237, 120.2, "winter");
        RoomFair roomFair3 = new RoomFair(498, 180.5, "summer");
        List<RoomFair> roomFairList = new ArrayList<RoomFair>();
        roomFairList.add(roomFair1);
        roomFairList.add(roomFair2);
        roomFairList.add(roomFair3);
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String truncateAccommodation = "TRUNCATE " + "accommodation" + " CASCADE";
            preparedStatement = conn.prepareStatement(truncateAccommodation);
            preparedStatement.executeUpdate();
            String truncateRoomFair = "TRUNCATE " + "room_fair" + " CASCADE";
            preparedStatement = conn.prepareStatement(truncateRoomFair);
            preparedStatement.executeUpdate();
            String truncateRelation = "TRUNCATE " + "accommodation_room_fair_relation" + " CASCADE";
            preparedStatement = conn.prepareStatement(truncateRelation);
            preparedStatement.executeUpdate();
            String insertAccommodation = "INSERT INTO accommodation(id, type, bed_type, max_guests, description) values(? ,?, ?, ?, ?)";
            preparedStatement = conn.prepareStatement(insertAccommodation);
            for (int i = 0; i < roomList.size(); i++) {
                preparedStatement.setInt(1, roomList.get(i).getId());
                preparedStatement.setString(2, roomList.get(i).getType());
                preparedStatement.setString(3, roomList.get(i).getBedType());
                preparedStatement.setInt(4, roomList.get(i).getMaxGuests());
                preparedStatement.setString(5, roomList.get(i).getDescription());
                preparedStatement.executeUpdate();
            }
            String insertRoomFair = "INSERT INTO room_fair(id, value, season) values(? ,?, ?)";
            preparedStatement = conn.prepareStatement(insertRoomFair);
            for (int i = 0; i < roomFairList.size(); i++) {
                preparedStatement.setInt(1, roomFairList.get(i).getId());
                preparedStatement.setDouble(2, roomFairList.get(i).getValue());
                preparedStatement.setString(3, roomFairList.get(i).getSeason());
                preparedStatement.executeUpdate();
            }
            String insertRelation = "INSERT INTO accommodation_room_fair_relation(id, accommodation_id, room_fair_id) values(? ,?, ?)";
            preparedStatement = conn.prepareStatement(insertRelation);
            for (int i = 0; i < roomList.size(); i++) {
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setInt(2, roomList.get(i).getId());
                preparedStatement.setInt(3, roomFairList.get(i).getId());
                preparedStatement.executeUpdate();
            }
            String join = "SELECT type, value FROM accommodation_room_fair_relation " +
                    "JOIN accommodation ON accommodation.id = accommodation_room_fair_relation.accommodation_id " +
                    "JOIN room_fair ON room_fair.id = accommodation_room_fair_relation.room_fair_id";
            ResultSet resultSet = stmt.executeQuery(join);
            Map<String, Double> prices = new HashMap<>();
            while (resultSet.next()) {
                String roomType = resultSet.getString("type");
                double price = resultSet.getDouble("value");
                prices.put(roomType, price);
            }
            for (String roomType : prices.keySet()) {
                String key = roomType;
                Double value = prices.get(roomType);
                System.out.println("The price of the " + key + " room is " + value + " RON");
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}

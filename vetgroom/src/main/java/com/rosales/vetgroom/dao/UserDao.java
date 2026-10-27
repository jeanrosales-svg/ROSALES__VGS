package com.rosales.vetgroom.dao;

import com.rosales.vetgroom.model.Role;
import com.rosales.vetgroom.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    /** Inserts a new user. Status defaults to PENDING until an administrator approves it. */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, account_status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lookup failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> findByStatus(String status) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE account_status = ? ORDER BY user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
        return users;
    }

    public boolean updateStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET account_status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            rs.getString("email"),
            Role.valueOf(rs.getString("role")),
            rs.getString("account_status"));
    }
}

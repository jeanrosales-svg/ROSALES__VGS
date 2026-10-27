package com.rosales.vetgroom.ui;

import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentUser;

    public AdminMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMINISTRATOR MENU ===");
            System.out.println("  1. Review pending accounts");
            System.out.println("  2. Manage appointments (coming soon)");
            System.out.println("  3. Reports (coming soon)");
            System.out.println("  0. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1": reviewPending(); break;
                case "2":
                case "3": System.out.println("Coming soon in a later deliverable."); break;
                case "0": running = false; System.out.println("Logged out."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void reviewPending() {
        List<User> pending = userDao.findByStatus("PENDING");
        if (pending.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }
        System.out.println("\nID   Username        Role           Full Name");
        for (User u : pending) {
            System.out.printf("%-4d %-15s %-14s %s%n",
                    u.getUserId(), u.getUsername(), u.getRole().name(), u.getFullName());
        }
        System.out.print("Enter user ID to approve (blank to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return;
        }
        try {
            int id = Integer.parseInt(input);
            boolean listed = pending.stream().anyMatch(u -> u.getUserId() == id);
            if (listed && userDao.updateStatus(id, "ACTIVE")) {
                System.out.println("Account approved (ACTIVE).");
            } else {
                System.out.println("That ID is not in the pending list.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }
}

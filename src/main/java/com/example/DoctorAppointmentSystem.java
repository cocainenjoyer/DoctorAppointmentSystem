package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class DoctorAppointmentSystem extends JFrame {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/doctor_appointments";
    private static String username = "postgres";
    private static String password = "5454";

    private Connection connection;
    private String currentRole = "GUEST";

    private JTextField searchField;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public DoctorAppointmentSystem() {
        setTitle("Система записи к врачу");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        showLoginDialog();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loginButton = new JButton("Вход");
        loginButton.addActionListener(e -> showLoginDialog());

        JButton createDbButton = new JButton("Создать БД");
        createDbButton.addActionListener(e -> createDatabase());

        JButton dropDbButton = new JButton("Удалить БД");
        dropDbButton.addActionListener(e -> dropDatabase());

        JButton clearTableButton = new JButton("Очистить таблицу");
        clearTableButton.addActionListener(e -> clearTable());

        topPanel.add(loginButton);
        topPanel.add(createDbButton);
        topPanel.add(dropDbButton);
        topPanel.add(clearTableButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        add(mainPanel);

        try {
            connectToDatabase();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к базе данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginDialog() {
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        JPanel loginPanel = new JPanel(new GridLayout(0, 2));
        loginPanel.add(new JLabel("Логин:"));
        loginPanel.add(userField);
        loginPanel.add(new JLabel("Пароль:"));
        loginPanel.add(passField);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Вход в систему", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            username = userField.getText();
            password = new String(passField.getPassword());

            try {
                connectToDatabase();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка авторизации: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, username, password);
    }

    private void createDatabase() {
        try (CallableStatement stmt = connection.prepareCall("{call create_appointment_database()}");) {
            stmt.execute();
            JOptionPane.showMessageDialog(this, "База данных успешно создана", "Информация", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка создания базы данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropDatabase() {
        try (CallableStatement stmt = connection.prepareCall("{call drop_appointment_database()}");) {
            stmt.execute();
            JOptionPane.showMessageDialog(this, "База данных успешно удалена", "Информация", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка удаления базы данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTable() {
        try (CallableStatement stmt = connection.prepareCall("{call clear_appointments_table()}");) {
            stmt.execute();
            JOptionPane.showMessageDialog(this, "Таблица успешно очищена", "Информация", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка очистки таблицы: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorAppointmentSystem());
    }
}

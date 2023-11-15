import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private Connection con;
    private Statement st;
    private PreparedStatement ps;
    private ResultSet rs;

    public Main() {
        connect();
        showLoginMenu();
    }

    private void showLoginMenu() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        int option = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            if (isValidUser(username, new String(password))) {
                JOptionPane.showMessageDialog(null, "Login berhasil!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(null, "Login gagal. Coba lagi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isValidUser(String username, String password) {
        // Ganti validUsername dan validPassword sesuai kebutuhan
        String validUsername = "admin";
        String validPassword = "password";
        return username.equals(validUsername) && password.equals(validPassword);
    }

    private void showMainMenu() {
        while (true) {
            Object[] options = {"Add Item", "Modify Item", "View Inventory", "Delete Item"};
            int choice = JOptionPane.showOptionDialog(null, "Pilih menu:", "Main Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == -1 || choice == 4) {
                System.exit(0);
            } else if (choice == 0) {
                addNewItem();
            } else if (choice == 1) {
                modifyItem();
            } else if (choice == 2) {
                viewInventory();
            } else if (choice == 3) {
                deleteItem();
            }
        }
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/latihan_crud", "root", "");
            System.out.println("Connected to the database");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addNewItem() {
        try {
            st = con.createStatement();
            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField quantityField = new JTextField();
            inputPanel.add(new JLabel("Item ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("Item Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Item Price:"));
            inputPanel.add(priceField);
            inputPanel.add(new JLabel("Item Quantity:"));
            inputPanel.add(quantityField);
            int option = JOptionPane.showConfirmDialog(null, inputPanel, "Add Item", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String idStr = idField.getText();
                String name = nameField.getText();
                String priceStr = priceField.getText();
                String quantityStr = quantityField.getText();
                if (idStr != null && name != null && priceStr != null && quantityStr != null) {
                    try {
                        int id = Integer.parseInt(idStr);
                        double price = Double.parseDouble(priceStr);
                        int quantity = Integer.parseInt(quantityStr);
                        String insertQuery = "INSERT INTO gudang (ID, Barang, Harga, Stok) VALUES (?, ?, ?, ?)";
                        ps = con.prepareStatement(insertQuery);
                        ps.setInt(1, id);
                        ps.setString(2, name);
                        ps.setDouble(3, price);
                        ps.setInt(4, quantity);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyItem() {
        // Meminta pengguna untuk memasukkan ID item yang akan dimodifikasi
        String idToModify = showInputDialog("Masukkan ID item yang akan dimodifikasi:");
        try {
            st = con.createStatement();
            // Mengecek apakah ID ada dalam database
            ResultSet result = st.executeQuery("SELECT * FROM gudang WHERE ID = " + idToModify);
            if (result.next()) {
                // Menampilkan form untuk mengubah data item
                JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                JTextField nameField = new JTextField(result.getString("Barang"));
                JTextField priceField = new JTextField(result.getString("Harga"));
                JTextField quantityField = new JTextField(result.getString("Stok"));
                inputPanel.add(new JLabel("Item Name:"));
                inputPanel.add(nameField);
                inputPanel.add(new JLabel("Item Price:"));
                inputPanel.add(priceField);
                inputPanel.add(new JLabel("Item Quantity:"));
                inputPanel.add(quantityField);

                int option = JOptionPane.showConfirmDialog(null, inputPanel, "Modify Item", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    String priceStr = priceField.getText();
                    String quantityStr = quantityField.getText();
                    if (name != null && priceStr != null && quantityStr != null) {
                        try {
                            double price = Double.parseDouble(priceStr);
                            int quantity = Integer.parseInt(quantityStr);
                            String updateQuery = "UPDATE gudang SET Barang=?, Harga=?, Stok=? WHERE ID=?";
                            ps = con.prepareStatement(updateQuery);
                            ps.setString(1, name);
                            ps.setDouble(2, price);
                            ps.setInt(3, quantity);
                            ps.setInt(4, Integer.parseInt(idToModify));
                            ps.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Item modified successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Item dengan ID tersebut tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem() {
        // Meminta pengguna untuk memasukkan ID item yang akan dihapus
        String idToDelete = showInputDialog("Masukkan ID item yang akan dihapus:");
        try {
            st = con.createStatement();
            // Mengecek apakah ID ada dalam database
            ResultSet result = st.executeQuery("SELECT * FROM gudang WHERE ID = " + idToDelete);
            if (result.next()) {
                // Konfirmasi pengguna sebelum menghapus item
                int confirmation = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus item ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    String deleteQuery = "DELETE FROM gudang WHERE ID=?";
                    ps = con.prepareStatement(deleteQuery);
                    ps.setInt(1, Integer.parseInt(idToDelete));
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Item berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Item dengan ID tersebut tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void viewInventory() {
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM gudang");
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("NO");
            model.addColumn("ID");
            model.addColumn("Barang");
            model.addColumn("Harga");
            model.addColumn("Stok");
            int no = 1;
            double totalValue = 0;

            // Mengisi model tabel dengan data dari database
            while (rs.next()) {
                Object[] data = {
                        no++,
                        rs.getString("ID"),
                        rs.getString("Barang"),
                        rs.getString("Harga"),
                        rs.getString("Stok")
                };
                model.addRow(data);
            }

            JTable inventoryTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(inventoryTable);

            // Menampilkan dialog JOptionPane setelah mengambil semua data
            JOptionPane.showMessageDialog(null, scrollPane, "Inventory", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String showInputDialog(String message) {
        return JOptionPane.showInputDialog(null, message, "Input", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

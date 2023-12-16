import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.*;
import javax.swing.JOptionPane;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class OrderItem extends javax.swing.JFrame {
  private JTextField cashField;

    public OrderItem() {
        initComponents();
        SelectProd();
        SelectCust();
        GetToday();
        Bersih();
        cashField = new JTextField();       
    }
Statement st = null;
ResultSet rs = null;

 private void Bersih() {
    DefaultTableModel dt = (DefaultTableModel) Billtbl.getModel();
    dt.setRowCount(0);
    BillId.setText("");
    CustNamelbl.setText("Customer Name");
    Amount.setText("Amount");
    Qtytb.setText("");
    PriceBtl.setText("");
    GetToday();
    
}
    @SuppressWarnings("unchecked")    
    public void SelectProd(){
    try{
    java.sql.Connection con = DriverManager.getConnection ("jdbc:mysql://localhost/latihan_crud", "root", "");
    st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM producttbl");
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("NO");
            model.addColumn("Barang");
            model.addColumn("Quantity");
            model.addColumn("Category");
            model.addColumn("Price");
            int no = 1;
            double totalValue = 0;

            // Mengisi model tabel dengan data dari database
            while (rs.next()) {
                Object[] data = {
                        no++,
                        rs.getString("ProdName"),
                        rs.getString("ProdQty"),
                        rs.getString("ProdCat"),
                        rs.getString("ProdPrice")
                };
                model.addRow(data);
                ProductTable.setModel(model);
            }
    } catch (SQLException e){
    e.printStackTrace();
}
}
    public void SelectCust(){
    try{
    java.sql.Connection con = DriverManager.getConnection ("jdbc:mysql://localhost/latihan_crud", "root", "");
    st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM customertable");
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("NO");
            model.addColumn("Cust ID");
            model.addColumn("Name");
            model.addColumn("Phone");
            int no = 1;
            while (rs.next()) {
                Object[] data = {
                        no++,
                        rs.getString("CustId"),
                        rs.getString("CustName"),
                        rs.getString("CustPhone"),
                };
                model.addRow(data);
                CustomerTable.setModel(model);
            }
    } catch (SQLException e){
    e.printStackTrace();
}
}

 private void ShowReceipt() {
    String header = "=================== Receipt Order =======================\n";
    String billId = "Order ID: " + BillId.getText() + "\n";
    String customerName = "Customer Name: " + CustNamelbl.getText() + "\n";
    String date = "Date: " + Datelbl.getText() + "\n\n";
    String tableHeader = String.format("%-4s%-20s%-12s%-12s%-12s\n", "No.", "Product", "Quantity", "Unit Price", "Total");

    StringBuilder tableContent = new StringBuilder();
    DefaultTableModel dt = (DefaultTableModel) Billtbl.getModel();
    for (int row = 0; row < dt.getRowCount(); row++) {
        tableContent.append(String.format("%-4s%-20s%-12s%-12s%-12s\n",
                dt.getValueAt(row, 0),
                dt.getValueAt(row, 1),
                dt.getValueAt(row, 2),
                dt.getValueAt(row, 3),
                dt.getValueAt(row, 4)));

        // Tambahkan garis pemisah antar item kecuali untuk item terakhir
        if (row < dt.getRowCount() - 1) {
            tableContent.append("".format("%-4s%-20s%-12s%-12s%-12s\n",
                    "----", "--------------------", "------------", "------------", "---------"));
        }
    }

    String totalQuantity = "\nTotal Quantity: " + Qtytxt.getText() + "\n";
    String totalAmount = "Total Amount: " + Amount.getText() + "\n";

    String footer = "\n==================== Thank You! =========================";

    String cashReceived = "Cash Received: " + cashField.getText() + "\n";
    int totalAmountValue = Integer.parseInt(Amount.getText());
    int cashReceivedValue = Integer.parseInt(cashField.getText());
    int changeValue = cashReceivedValue - totalAmountValue;
    String change = "Change: " + changeValue + "";
    String receipt = header + billId + customerName + date + tableHeader + tableContent.toString() + totalQuantity + totalAmount + cashReceived + change + footer;

    JTextArea receiptArea = new JTextArea(receipt);
    receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Huruf monospasi
    receiptArea.setBackground(new Color(255, 255, 240)); // Warna latar belakang yang lembut
    receiptArea.setForeground(Color.BLACK); // Warna teks

    JScrollPane scrollPane = new JScrollPane(receiptArea);
    scrollPane.setPreferredSize(new Dimension(400, 300)); // Sesuaikan ukuran tampilan
    JOptionPane.showMessageDialog(this, scrollPane, "Order Receipt", JOptionPane.INFORMATION_MESSAGE);
     Object[] options = {"OK", "Print Receipt"};
    int choice = JOptionPane.showOptionDialog(
            this,
            scrollPane,
            "Order Receipt",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
    );

    // Tombol "Print Receipt" dipilih
    if (choice == 1) {
        printReceipt(receipt); // Panggil metode printReceipt
    }
}

private void printReceipt(String receipt) {
    // Membuat objek PrinterJob
    PrinterJob printerJob = PrinterJob.getPrinterJob();

    // Mengatur tampilan nota ke objek Printable
    Printable printable = new Printable() {
        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) g;

            // Menetapkan ukuran kertas A4 dan margin
            pageFormat.setOrientation(PageFormat.PORTRAIT);
            double paperWidth = 8.3 * 72; // 8.3 inch * 72 dpi
            double paperHeight = 11.7 * 72; // 11.7 inch * 72 dpi
            double margin = 36; // 1/2 inch margin
            pageFormat.setPaper(new Paper());
            pageFormat.getPaper().setSize(paperWidth, paperHeight);
            pageFormat.getPaper().setImageableArea(margin, margin, paperWidth - 2 * margin, paperHeight - 2 * margin);

            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Menetapkan font dan warna
            Font font = new Font("Monospaced", Font.PLAIN, 12);
            g.setFont(font);
            g.setColor(Color.BLACK);

            // Membagi tampilan nota menjadi beberapa baris
            String[] lines = receipt.split("\n");
            int lineHeight = g.getFontMetrics().getHeight();
            int y = lineHeight;

            // Menggambar setiap baris nota
            for (String line : lines) {
                g.drawString(line, 0, y);
                y += lineHeight;
            }

            return Printable.PAGE_EXISTS;
        }
    };

    // Menetapkan objek Printable ke PrinterJob
    printerJob.setPrintable(printable);

    // Menampilkan dialog pencetakan
    if (printerJob.printDialog()) {
        try {
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to print receipt.", "Print Receipt", JOptionPane.ERROR_MESSAGE);
        }
    }
}



    private void GetToday(){
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    Datelbl.setText(dtf.format(now));
    }
    
    private void Update (){
        int newqty = oldqty - Integer.valueOf(Qtytb.getText());
        try{
          java.sql.Connection con = DriverManager.getConnection ("jdbc:mysql://localhost/latihan_crud", "root", "");
     String updateQuery = "UPDATE producttbl SET ProdQty = '"+ newqty +
                              "' WHERE ProdId = '" + productid +"'";
                Statement Add = con.createStatement ();
                Add.executeUpdate (updateQuery);
                JOptionPane.showMessageDialog(null, "Berhasil Diperbarui");
                SelectProd();
    } catch (SQLException e){
        e.printStackTrace();
    }}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        CustomerTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        ProductTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        BillId = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        CustNamelbl = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Datelbl = new javax.swing.JLabel();
        AddOrderBtn = new javax.swing.JButton();
        BatalBtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Billtbl = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        PriceBtl = new javax.swing.JTextField();
        Qtytb = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        AddBtn1 = new javax.swing.JButton();
        Amount = new javax.swing.JLabel();
        Qtytxt = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        Amttbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("INVENTORY MNG");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel10.setBackground(new java.awt.Color(0, 255, 204));
        jLabel10.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setText("PRODUCT");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("ORDER ITEM");

        jLabel13.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setText(" CATEGORY");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 153));
        jLabel14.setText("CUSTOMERS");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(153, 153, 153));
        jLabel15.setText("USER STAFF");

        jLabel17.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(153, 153, 153));
        jLabel17.setText("VIEW ORDER");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel5.setText("  ORDER ITEM PAGE");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(309, 309, 309)
                .addComponent(jLabel5)
                .addContainerGap(601, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, Short.MAX_VALUE)
                .addContainerGap())
        );

        CustomerTable.setAutoCreateRowSorter(true);
        CustomerTable.setBackground(new java.awt.Color(51, 51, 51));
        CustomerTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CustomerTable.setForeground(new java.awt.Color(255, 255, 255));
        CustomerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CustId", "Name", "Phone"
            }
        ));
        CustomerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CustomerTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(CustomerTable);

        ProductTable.setAutoCreateRowSorter(true);
        ProductTable.setBackground(new java.awt.Color(51, 51, 51));
        ProductTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        ProductTable.setForeground(new java.awt.Color(255, 255, 255));
        ProductTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProdId", "Name", "Quantity", "Category"
            }
        ));
        ProductTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProductTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ProductTable);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("OrderId");

        BillId.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BillId.setForeground(new java.awt.Color(51, 51, 51));
        BillId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BillIdActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CustName");

        CustNamelbl.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CustNamelbl.setForeground(new java.awt.Color(51, 51, 51));
        CustNamelbl.setText("Customer Name");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Date");

        Datelbl.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Datelbl.setForeground(new java.awt.Color(51, 51, 51));
        Datelbl.setText("Date");

        AddOrderBtn.setBackground(new java.awt.Color(51, 51, 51));
        AddOrderBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        AddOrderBtn.setForeground(new java.awt.Color(255, 255, 255));
        AddOrderBtn.setText("AddOrder");
        AddOrderBtn.setBorder(null);
        AddOrderBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AddOrderBtnMouseClicked(evt);
            }
        });
        AddOrderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddOrderBtnActionPerformed(evt);
            }
        });

        BatalBtn.setBackground(new java.awt.Color(51, 51, 51));
        BatalBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BatalBtn.setForeground(new java.awt.Color(255, 255, 255));
        BatalBtn.setText("Batal");
        BatalBtn.setBorder(null);
        BatalBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BatalBtnMouseClicked(evt);
            }
        });
        BatalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatalBtnActionPerformed(evt);
            }
        });

        Billtbl.setAutoCreateRowSorter(true);
        Billtbl.setBackground(new java.awt.Color(51, 51, 51));
        Billtbl.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Billtbl.setForeground(new java.awt.Color(255, 255, 255));
        Billtbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Num", "Product", "Quantity", "Uprice", "Total"
            }
        ));
        Billtbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BilltblMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Billtbl);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Price");

        PriceBtl.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        PriceBtl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PriceBtlActionPerformed(evt);
            }
        });

        Qtytb.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Qtytb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QtytbActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Qty");

        AddBtn1.setBackground(new java.awt.Color(51, 51, 51));
        AddBtn1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        AddBtn1.setForeground(new java.awt.Color(255, 255, 255));
        AddBtn1.setText("AddToOrder");
        AddBtn1.setBorder(null);
        AddBtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AddBtn1MouseClicked(evt);
            }
        });
        AddBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddBtn1ActionPerformed(evt);
            }
        });

        Amount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Amount.setForeground(new java.awt.Color(51, 51, 51));
        Amount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Amount.setText("Amount");

        Qtytxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Qtytxt.setForeground(new java.awt.Color(255, 255, 255));
        Qtytxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Qtytxt.setText("Amount");

        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel12.setText("  CUSTOMERS LIST");

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel18.setText("PRODUCT LIST");

        Amttbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Amttbl.setForeground(new java.awt.Color(51, 51, 51));
        Amttbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Amttbl.setText("Amount");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(51, 51, 51)
                                        .addComponent(AddOrderBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BatalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel6)
                                                    .addComponent(jLabel1)
                                                    .addComponent(jLabel8))
                                                .addGap(33, 33, 33)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(CustNamelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(BillId, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Datelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Amount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Qtytxt, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Amttbl, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Qtytb, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addGap(19, 19, 19)
                                                    .addComponent(AddBtn1)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(PriceBtl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel12)
                        .addGap(160, 160, 160)
                        .addComponent(jLabel18)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(PriceBtl, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Qtytb, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(AddBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BillId, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CustNamelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Datelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BatalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AddOrderBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(Amttbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Amount)
                                .addGap(18, 18, 18)
                                .addComponent(Qtytxt)))
                        .addGap(0, 24, Short.MAX_VALUE))))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 977, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
 int flag = 0, productid, oldqty;
 int i = 1,Uprice, tot = 0, total, quantitytotal;
 String Prodname;
    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
   new ProductNew().setVisible(true);
   this.dispose();
    }//GEN-LAST:event_jLabel10MouseClicked

    private void CustomerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CustomerTableMouseClicked
        CustNamelbl.setText(CustomerTable.getValueAt(CustomerTable.getSelectedRow(),2).toString());
    }//GEN-LAST:event_CustomerTableMouseClicked

    private void ProductTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProductTableMouseClicked
        DefaultTableModel model = (DefaultTableModel)ProductTable.getModel();
        int MyIndex = ProductTable.getSelectedRow();
        oldqty = Integer.valueOf(model.getValueAt(MyIndex,2).toString());
        productid = Integer.valueOf(model.getValueAt(MyIndex, 0).toString());
        PriceBtl.setText(model.getValueAt(MyIndex, 4).toString());
        Prodname = model.getValueAt(MyIndex, 1).toString();
        PriceBtl.setEditable(false);
        flag = 1;
    }//GEN-LAST:event_ProductTableMouseClicked

    private void BillIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BillIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BillIdActionPerformed

    private void AddOrderBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddOrderBtnMouseClicked

        try {
            java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://localhost/latihan_crud", "root", "");
            if (BillId.getText().equals("") || PriceBtl.getText().equals("") || Qtytb.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Data tidak boleh kosong", "Validasi Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (AddOrderBtn.getText().equals("AddOrder")) {
                PreparedStatement add = con.prepareStatement("insert into ordertbl values (?,?,?,?,?)");
                add.setInt(1, Integer.valueOf(BillId.getText()));
                add.setString(2, CustNamelbl.getText());
                add.setString(3, Datelbl.getText());
                add.setInt(4, Integer.valueOf(Amount.getText()));
                add.setInt(5, Integer.valueOf(Qtytxt.getText()));
                int row = add.executeUpdate();

                if (row > 0) {
                    JOptionPane.showMessageDialog(this, "Product Successfully Added");
               JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("Cash Received:"));
                panel.add(cashField);

                // Tampilkan dialog untuk input jumlah uang
                int result = JOptionPane.showConfirmDialog(null, panel, "Cash Payment", JOptionPane.OK_CANCEL_OPTION);

                // Jika OK ditekan, hitung kembaliannya dan tampilkan struk
                if (result == JOptionPane.OK_OPTION) {
                    ShowReceipt();
                    SelectProd();
                }
                }
                 Bersih ();
            }
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                // Duplicate key entry violation (orderid already exists)
                JOptionPane.showMessageDialog(this, "Order ID already exists. Please choose a different Order ID.", "Duplicate Order ID", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_AddOrderBtnMouseClicked

    private void AddOrderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddOrderBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddOrderBtnActionPerformed

    private void BatalBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BatalBtnMouseClicked
        //Bersih();        // TODO add your handling code here:
    }//GEN-LAST:event_BatalBtnMouseClicked

    private void BatalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatalBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BatalBtnActionPerformed

    private void BilltblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BilltblMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_BilltblMouseClicked

    private void PriceBtlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PriceBtlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PriceBtlActionPerformed

    private void QtytbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QtytbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_QtytbActionPerformed

    private void AddBtn1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddBtn1MouseClicked
             
    if (flag == 0 || Qtytb.getText().isEmpty() || PriceBtl.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Select Product and Enter Qty");
    } else {
        Vector v = new Vector();
        Uprice = Integer.valueOf(PriceBtl.getText());
        tot = Uprice * Integer.valueOf(Qtytb.getText());
        v.add(i);
        v.add(Prodname);
        v.add(Qtytb.getText());
        v.add(Uprice);
        v.add(tot);
        DefaultTableModel dt = (DefaultTableModel) Billtbl.getModel();
        dt.addRow(v);
        // Menambahkan quantity dari item baru ke total quantity
        quantitytotal += Integer.valueOf(Qtytb.getText());
        Qtytxt.setText("" + quantitytotal);
        
        // Menambahkan total harga dari item baru ke total harga keseluruhan
        total = tot + total;
        Amount.setText("" + total);
        total = tot + total;
        Update();
        i++;
    }
    }//GEN-LAST:event_AddBtn1MouseClicked

    private void AddBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBtn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddBtn1ActionPerformed

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
       new CategoryNew().setVisible(true);
      this.dispose();
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
       new CustomerNew ().setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
      new OrderView().setVisible(true);
      this.dispose();
    }//GEN-LAST:event_jLabel17MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrderItem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddBtn1;
    private javax.swing.JButton AddOrderBtn;
    private javax.swing.JLabel Amount;
    private javax.swing.JLabel Amttbl;
    private javax.swing.JButton BatalBtn;
    private javax.swing.JTextField BillId;
    private javax.swing.JTable Billtbl;
    private javax.swing.JLabel CustNamelbl;
    private javax.swing.JTable CustomerTable;
    private javax.swing.JLabel Datelbl;
    private javax.swing.JTextField PriceBtl;
    private javax.swing.JTable ProductTable;
    private javax.swing.JTextField Qtytb;
    private javax.swing.JLabel Qtytxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}

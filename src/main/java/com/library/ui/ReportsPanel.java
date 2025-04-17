package main.java.com.library.ui;

import main.java.com.library.dao.IssuedBookDAO;
import main.java.com.library.model.IssuedBook;
import main.java.com.library.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ReportsPanel extends JPanel {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JRadioButton allBooksRadio, issuedBooksRadio, returnedBooksRadio, overdueRadio;
    private JButton refreshButton, exportButton;
    
    private IssuedBookDAO issuedBookDAO;
    
    public ReportsPanel() {
        issuedBookDAO = new IssuedBookDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        layoutComponents();
        loadAllBooks();
    }
    
    private void initComponents() {
        // Table components
        String[] columns = {"ID", "Student Name", "Book Title", "ISBN", "Issue Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom renderer for status column
        booksTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                if ("Returned".equals(status)) {
                    c.setForeground(new Color(0, 128, 0)); // Green
                } else if ("Overdue".equals(status)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(0, 0, 128)); // Blue
                }
                
                return c;
            }
        });
        
        // Filter options
        allBooksRadio = new JRadioButton("All Records");
        issuedBooksRadio = new JRadioButton("Issued Books");
        returnedBooksRadio = new JRadioButton("Returned Books");
        overdueRadio = new JRadioButton("Overdue Books");
        
        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(allBooksRadio);
        filterGroup.add(issuedBooksRadio);
        filterGroup.add(returnedBooksRadio);
        filterGroup.add(overdueRadio);
        allBooksRadio.setSelected(true);
        
        // Buttons
        refreshButton = new JButton("Refresh");
        exportButton = new JButton("Export to CSV");
        
        // Add action listeners
        allBooksRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllBooks();
            }
        });
        
        issuedBooksRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadIssuedBooks();
            }
        });
        
        returnedBooksRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReturnedBooks();
            }
        });
        
        overdueRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOverdueBooks();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allBooksRadio.isSelected()) {
                    loadAllBooks();
                } else if (issuedBooksRadio.isSelected()) {
                    loadIssuedBooks();
                } else if (returnedBooksRadio.isSelected()) {
                    loadReturnedBooks();
                } else if (overdueRadio.isSelected()) {
                    loadOverdueBooks();
                }
            }
        });
        
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });
    }
    
    private void layoutComponents() {
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        filterPanel.add(allBooksRadio);
        filterPanel.add(issuedBooksRadio);
        filterPanel.add(returnedBooksRadio);
        filterPanel.add(overdueRadio);
        filterPanel.add(refreshButton);
        filterPanel.add(exportButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        
        // Add panels to main panel
        add(filterPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }
    
    private void loadAllBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load all books from database
        List<IssuedBook> issuedBooks = issuedBookDAO.getAllIssuedBooks();
        
        // Add issued books to table
        addBooksToTable(issuedBooks);
    }
    
    private void loadIssuedBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load issued books from database
        List<IssuedBook> issuedBooks = issuedBookDAO.getCurrentlyIssuedBooks();
        
        // Add issued books to table
        addBooksToTable(issuedBooks);
    }
    
    private void loadReturnedBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load returned books from database
        List<IssuedBook> returnedBooks = issuedBookDAO.getReturnedBooks();
        
        // Add returned books to table
        addBooksToTable(returnedBooks);
    }
    
    private void loadOverdueBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load overdue books from database
        List<IssuedBook> overdueBooks = issuedBookDAO.getOverdueBooks();
        
        // Add overdue books to table
        addBooksToTable(overdueBooks);
    }
    
    private void addBooksToTable(List<IssuedBook> books) {
        for (IssuedBook book : books) {
            String status = book.getReturnDate() != null ? "Returned" : 
                           book.isOverdue() ? "Overdue" : "Issued";
            
            Object[] row = {
                book.getId(),
                book.getStudentName(),
                book.getBookTitle(),
                book.getIsbn(),
                DateUtil.formatDate(book.getIssueDate()),
                book.getReturnDate() != null ? DateUtil.formatDate(book.getReturnDate()) : "",
                status
            };
            tableModel.addRow(row);
        }
    }
    
    private void exportToCSV() {
        // This is a simplified version - in a real application, you would use a file chooser
        // and actually write to a CSV file
        JOptionPane.showMessageDialog(this, "Export functionality would be implemented here.", "Export to CSV", JOptionPane.INFORMATION_MESSAGE);
    }
}

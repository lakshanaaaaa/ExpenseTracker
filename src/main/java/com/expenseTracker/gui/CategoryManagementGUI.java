package com.expenseTracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.expenseTracker.dao.TrackerDAO;
import com.expenseTracker.model.Category;

public class CategoryManagementGUI extends JPanel
{
    private MainGUI mainGUI;
    private TrackerDAO trackerDAO;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton backButton;

    public CategoryManagementGUI(MainGUI mainGUI, TrackerDAO trackerDAO) {
        this.mainGUI = mainGUI;
        this.trackerDAO = trackerDAO;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCategories();
    }

    private void initializeComponents() {
        // setTitle("Categories");
        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // setSize(800, 600); 
        // setLocationRelativeTo(mainGUI);

        this.add(new JLabel("Category Management"));
        backButton = new JButton("<-");

        this.add(backButton);


        String[] columnNames = {"ID", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        categoryField = new JTextField(20);
        addButton = new JButton("Add Category");
        deleteButton =new JButton("Delete Category");
        updateButton =new JButton("Update Category");   
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Category"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel= new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(new JLabel("Category Management", SwingConstants.CENTER),BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        northPanel.add(topPanel,BorderLayout.NORTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a Category to Edit or Delete"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e ->deleteCategory());
        updateButton.addActionListener(e ->updateCategory());
        backButton.addActionListener(e -> mainGUI.showHome());

        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row != -1) {
                populateFieldsFromRow(row);
            }
        });

    }

    private void deleteCategory(){
        int row=categoryTable.getSelectedRow();
        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a row to delete","Selection Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm=JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this category?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            int id=(int) tableModel.getValueAt(row,0);
            try{
                boolean deleted=trackerDAO.deleteCategory(id);
                if(!deleted){
                    JOptionPane.showMessageDialog(this,"Cannot delete category as it is associated with existing expenses.","Deletion Error",JOptionPane.ERROR_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(this,"Category deleted successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                }
                categoryField.setText("");
                loadCategories();
            }
            catch(SQLException e){
                JOptionPane.showMessageDialog(this,"Error deleting category: "+e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void addCategory() {
        String catName = categoryField.getText().trim();
        if (catName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Category newCategory = new Category(catName);
            trackerDAO.addCategory(newCategory);
            JOptionPane.showMessageDialog(this, "Category added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            categoryField.setText("");
            loadCategories();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // private void selectedDisplay(){
    //     int row=categorytable.getSelectedRow();
    //     if(selected==-1){
    //         JOptionPane.showMessageDialog(this,"Please select a row to update","Selection Error",JOptionPane.ERROR_MESSAGE);
    //         return;
    //     }
    //     int catId=(int) tableModel.getValueAt(row,0);
    //     categoryField.setText((String) tableModel.getValueAt(row,1));
    // }

    private void updateCategory(){
        int row=categoryTable.getSelectedRow();
        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a row to update","Selection Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String catName=categoryField.getText().trim();
        if(catName.isEmpty()){
            JOptionPane.showMessageDialog(this,"Category name cannot be empty","Input Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        int catId=Integer.parseInt(tableModel.getValueAt(row,0).toString());

        try{
            Category cat=trackerDAO.getCategoryById(catId);

            if(cat==null){
                JOptionPane.showMessageDialog(this,"Selected category does not exist","Input Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(cat!=null){
                cat.setCatName(catName);
            }
            if(trackerDAO.updateCategory(cat)){
                JOptionPane.showMessageDialog(this,"Category updated successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                categoryField.setText("");
                loadCategories();
            }else{
                JOptionPane.showMessageDialog(this,"Failed to update category","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error updating category: "+e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }


    }


    private void loadCategories() {
        try {
            List<Category> categories = trackerDAO.getAllCategories();
            tableModel.setRowCount(0);
            for (Category cat : categories) {
                Object[] row = {cat.getCatId(), cat.getCatName()};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFieldsFromRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return; // Invalid row, do nothing
        }
        String catN = (String) tableModel.getValueAt(row, 2);

        categoryField.setText(catN);
    }
}

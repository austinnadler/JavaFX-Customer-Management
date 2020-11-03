package com.austinnadler.custmgmtmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class App extends Application {
    
    private ObservableList<Person> pList = FXCollections.observableArrayList();
    private TableView<Person> table;
    
    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtPhoneNumber;
    private TextField txtEmailAddress;
    
    private RadioButton rdoFF;
    private RadioButton rdoCC;
    private RadioButton rdoTM;
    
    private TableColumn colName;
    private TableColumn colPhone;
    private TableColumn colEmail;
    private TableColumn colType;

    private Stage stage;
    
    private final String DB_URL = "jdbc:mysql://localhost:3306/custmgmt";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "root";

    @Override
    public void start(Stage primaryStage) {
        
        stage = primaryStage;
      
        Label lblFirstName = new Label("First name: ");
        txtFirstName = new TextField();
        txtFirstName.setPromptText("First name");
        
        Label lblLastName = new Label("Last name: ");
        txtLastName = new TextField();
        txtLastName.setPromptText("Last name");
        
        Label lblPhoneNumber = new Label("Phone number: ");
        txtPhoneNumber = new TextField();
        txtPhoneNumber.setPromptText("10 digit phone");
        
        Label lblEmailAddress = new Label("Email address: ");
        txtEmailAddress = new TextField();
        txtEmailAddress.setPromptText("Email address");

        //
        // GridPane for the left side of the form
        //
        
        GridPane gridText = new GridPane(); 
        gridText.setPadding(new Insets(10));
        gridText.setHgap(5);
        gridText.setVgap(5);
        gridText.setMinWidth(350);
        gridText.setPrefWidth(350);
        gridText.setMaxWidth(800);
        gridText.setAlignment(Pos.CENTER);
        gridText.addRow(0, lblFirstName, txtFirstName);
        gridText.addRow(1, lblLastName, txtLastName);
        gridText.addRow(2, lblPhoneNumber, txtPhoneNumber);
        gridText.addRow(3, lblEmailAddress, txtEmailAddress);
        
        //
        // Create the RadioButtons and ToggleGroup
        //
        
        Label lblType = new Label("Type");
        rdoFF = new RadioButton(Person.FF);
        rdoCC = new RadioButton(Person.CC);
        rdoTM = new RadioButton(Person.TM);
        ToggleGroup groupType = new ToggleGroup();
        rdoFF.setToggleGroup(groupType);
        rdoCC.setToggleGroup(groupType);
        rdoTM.setToggleGroup(groupType);
        
        //
        // VBox for the RadioButtons on the right side of the form
        //
        
        VBox paneType = new VBox(lblType, rdoFF, rdoCC, rdoTM);
        paneType.setSpacing(10);
        paneType.setPadding(new Insets(10));
        paneType.setMinWidth(200);
        paneType.setPrefWidth(200);
        paneType.setMaxWidth(800);
        
        //
        // HBox to hold all of the input controls together
        //
        
        HBox paneForm = new HBox(gridText, paneType);
        
        //
        // Creating the Submit, Clear, and Delete buttons
        //
        HBox paneButtons = new HBox();
        
        Button btnInsert = new Button("Insert");
        btnInsert.setPrefWidth(80);
        btnInsert.setOnAction(e -> btnInsert_Click());
        
        Button btnUpdate = new Button("Update");
        btnUpdate.setPrefWidth(80);
        btnUpdate.setOnAction(e -> btnUpdate_Click());
        
        // Spacer to push the Delete button to the right
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);
        
        Button btnClear = new Button("Clear");
        btnClear.setPrefWidth(80);
        btnClear.setOnAction(e -> btnClear_Click());       
        
        Button btnDelete = new Button("Delete");
        btnDelete.setPrefWidth(80);
        btnDelete.setOnAction(e -> btnDelete_Click());
        //
        // HBox to hold the three buttons
        //
        
        paneButtons = new HBox(10, btnInsert, btnUpdate, btnClear, spacer, btnDelete);   
        
        //
        // Setting up a TableView that gets data from pList, which gets data from the input file
        //
        
        table = new TableView<Person>();
        table.setPadding(new Insets(5));
        
        colName = new TableColumn("Name");
        colPhone = new TableColumn("Phone number");
        colEmail = new TableColumn("Email address");
        colType = new TableColumn("Type");
        
        // these lines basically call the getters for the class property you specify in quotes
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        table.getColumns().addAll(colName, colPhone, colEmail, colType);
        table.setItems(pList);
        
       // Event handler fires whenever the selected item in the TableView changes (Click, d-pad, etc)
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                Person p = table.getSelectionModel().getSelectedItem();
                txtFirstName.setText(p.getFirstName());
                txtLastName.setText(p.getLastName());
                txtPhoneNumber.setText(p.getPhoneNumber());
                txtEmailAddress.setText(p.getEmailAddress());
                if(p.getType().equals(Person.FF)) { rdoFF.setSelected(true); }
                if(p.getType().equals(Person.CC)) { rdoCC.setSelected(true); }
                if(p.getType().equals(Person.TM)) { rdoTM.setSelected(true); }
            }
        });
        
        //
        // Create 3 row VBox with the input fields, butttons, and table
        //        
        
        VBox panePrimary = new VBox(10, paneForm, paneButtons, table);
        panePrimary.setPadding(new Insets(10));
       
        fetchCustomers();
       
        Scene scene = new Scene(panePrimary);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Customer Management");
        primaryStage.show();
    }
    
    public void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg);
        a.setTitle(title);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
    
    //
    // Database CRUD methods
    //
    
    public void fetchCustomers() {
        pList.clear();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String query = "select id, firstName, lastName, phoneNumber, emailAddress, type from person";
                
        try {
            con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery(query);
            
            while(rs.next()) {
                int id = rs.getInt(1);
                String firstName = rs.getString(2);
                String lastName = rs.getString(3);
                String phoneNumber = rs.getString(4);
                String emailAddress = rs.getString(5);
                String type = rs.getString(6);
                
                Person p = new Person(id);
                p.setId(id);
                p.setFirstName(firstName);
                p.setLastName(lastName);
                p.setPhoneNumber(phoneNumber);
                p.setEmailAddress(emailAddress);
                p.setType(type);
                
                pList.add(p);
            }
            con.close();
        } catch(SQLException e) {
            showAlert(Alert.AlertType.ERROR, "DATABASE ERROR", e.getMessage());
        }
        table.setItems(pList);
        clearFieldsAndSelection();
    }
    
    public void deleteCustomer(Person p) {
        Connection con = null;
        PreparedStatement stmt = null;

        String query = "delete from person where id=?";
                
        try {
            con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = con.prepareStatement(query);
            stmt.setInt(1, p.getId());
            stmt.execute();
            con.close();
            
            // Assuming the operation was successful if we got this far.
            pList.remove(p);
            table.setItems(pList);
            clearFieldsAndSelection();
        } catch(SQLException e) {
            showAlert(Alert.AlertType.ERROR, "DATABASE ERROR", e.getMessage());
        }
    }
    
    private void insertOrUpdateCustomer(Person p, boolean isInsert) {        
        Connection con = null;
        PreparedStatement stmt = null;
        String query = "";
        if(isInsert) {
            query = "insert into person (firstName, lastName, phoneNumber, emailAddress, type) values (?, ?, ?, ?, ?)";
        } else {
            query = "update person set firstName=?, lastName=?, phoneNumber=?, emailAddress=?, type=? where id=?";
        }
        try {
            con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = con.prepareStatement(query);
            stmt.setString(1, p.getFirstName());
            stmt.setString(2, p.getLastName());
            stmt.setString(3, p.getPhoneNumber());
            stmt.setString(4, p.getEmailAddress());
            stmt.setString(5, p.getType());
            if(!isInsert) { stmt.setInt(6, p.getId()); }
            int rows = stmt.executeUpdate();
            con.close();
            // Assuming the operation was successful if we got this far.
            if(isInsert){ 
                pList.add(p);
            }
            fetchCustomers();

        } catch(SQLException e) {
            showAlert(Alert.AlertType.ERROR, "DATABASE ERROR", e.getMessage());
        }
    }
    
    //
    // End Database methods
    //
    
    //
    // Events
    //
    
    private void btnInsert_Click() {
        insertOrUpdateCustomer(getCustomerFromForm(), true);
    }
    
    private void btnUpdate_Click() {
        insertOrUpdateCustomer(getCustomerFromForm(), false);
    }
    
    private void btnClear_Click() {
        clearFieldsAndSelection();
    }

    private void btnDelete_Click() {
        Person p = table.getSelectionModel().getSelectedItem();
        if(p != null) {
            deleteCustomer(p);    
        } else {
            showAlert(Alert.AlertType.ERROR, "No Customer Selected", "No customer selected to delete/");
        }
    }
    
    //
    // End Events
    //
    
    //
    // Utilities
    //
    
    private Person getCustomerFromForm() { // Whether an insert or update is being done, the fields will be populated or empties will be caught by exceptions.
        int id = -1;
        if(table.getSelectionModel().getSelectedItem() != null) {
            id = table.getSelectionModel().getSelectedItem().getId();
        }
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String emailAddress = txtEmailAddress.getText();
        String type = "";
        if(rdoCC.isSelected()) { type = Person.CC; }
        if(rdoFF.isSelected()) { type = Person.FF; } 
        if(rdoTM.isSelected()) { type = Person.TM; } 
        
        Person p = new Person();
        if(id != -1) { p.setId(id); }
        p.setFirstName(firstName);
        p.setLastName(lastName);
        try {
            p.setPhoneNumber(phoneNumber);
        } catch (IllegalArgumentException e) {
            p = null;
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone number must be 10 digits.");
            return null;
        }
        try {
            p.setEmailAddress(emailAddress);
        } catch (IllegalArgumentException e) {
            p = null;
            showAlert(Alert.AlertType.ERROR, "Invalid Email Address", "Invalid email address. Correct your input.");
            return null;
        }
        try {
            p.setType(type);
        } catch (IllegalArgumentException e) {
            p = null;
            showAlert(Alert.AlertType.ERROR, "Invalid type", "That's weird, somehow the type given is invalid. Try again.");
            return null;
        }
        return p;        
    }
    
    public void clearFieldsAndSelection() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhoneNumber.setText("");
        txtEmailAddress.setText("");
        rdoFF.setSelected(false);
        rdoCC.setSelected(false);
        rdoTM.setSelected(false);
        table.getSelectionModel().clearSelection();
    }
    //
    // End Utilities
    //
}
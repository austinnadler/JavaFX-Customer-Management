package com.austinnadler.custmgmtcsv;

import static javafx.application.Application.launch;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    
    final String appFileName = "people.csv";
    
    TextField txtFirstName;
    TextField txtLastName;
    TextField txtPhoneNumber;
    TextField txtEmailAddress;
    
    RadioButton rdoFF;
    RadioButton rdoCC;
    RadioButton rdoTM;
    
    ObservableList<Person> pList;
    TableView<Person> table;
    
    TableColumn colName;
    TableColumn colPhone;
    TableColumn colEmail;
    TableColumn colType;

    Stage stage;
    
    @Override
    public void start(Stage primaryStage) {
        
        stage = primaryStage;
        primaryStage.setResizable(false);
        readCSV();
        
        //
        // Creating labels and text boxes
        //
        
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
       //        panePrimary.setAlignment(Pos.TOP_LEFT); 
       
        Scene scene = new Scene(panePrimary);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Customer Management");
        primaryStage.show();
    }
    
    public void readCSV() {         
        this.pList = FXCollections.observableArrayList();
        int rowIndex = 0; // Used to track what line is currently being read
        int colIndex = 0; // Used to track what item on the line is currently being read. (firstname, lastname, phone, email)
        String currentDataValue = ""; // The exact piece of data currently being parsed. Declared here so it can be used in exception handling.
        String filePath = ""; // File path of people.csv for exception message        
        try {
            // FileReader doesnt have an exists() method
            File f = new File(appFileName);            
            if(!f.exists()) { throw new IOException("File not found"); }
            filePath = f.getAbsolutePath();
            f = null;
            BufferedReader fin = new BufferedReader(new FileReader(appFileName));
            Scanner scanner = null;
            String line = null;
            Person p = null;
            // Read the whole line into a string
            while ((line = fin.readLine()) != null) {
                p = new Person();
                scanner = new Scanner(line);
                // Set the delimeter to comma for CSV, loop through each item on the line
                scanner.useDelimiter(",");
                while (scanner.hasNext()) {
                    currentDataValue = scanner.next();
                    switch (colIndex) {
                        case 0:
                            p.setFirstName(currentDataValue);
                            break;
                        case 1:
                            p.setLastName(currentDataValue);
                            break;
                        case 2:
                            p.setPhoneNumber(currentDataValue);
                            break;
                        case 3:
                            p.setEmailAddress(currentDataValue);                          
                            break;
                        case 4:
                            p.setType(currentDataValue);
                            break;
                        default:
                            showAlert(Alert.AlertType.INFORMATION, "File Read Error", "File read error on line " + (rowIndex + 1) + ", col " + (colIndex + 1) + "\n\nThe input file has more properties than expected. These extra properties will be ignored.\n\nYour customer list is located at " + filePath);
                            break;
                    }
                    colIndex++;
                }
                rowIndex++;
                colIndex = 0; // resetting the line item index
                pList.add(p);
            }
        } catch(IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "File Read Error", "Invalid value: " + currentDataValue + " at row " + (rowIndex + 1) + ", col " + (colIndex + 1) + "\n\nClose the program now and correct the data, or the program will remove the bad data on the next action.\n\nYour customer list is located at " + filePath);
        } catch(IOException e) {
            showAlert(Alert.AlertType.INFORMATION, "File Read Error", "No input file. Found.\n\nContinuing with no data...\n\nIf you think you have a file created, check the program directory before continuing." + filePath);
        }
    }
    
    public void writeCSV() {
        PrintStream fout = null;
        try {
            fout = new PrintStream(appFileName);
            for(int i = 0; i < pList.size(); i++) {
                Person p = pList.get(i);
                fout.println(p.getCSVString());         
            }
           fout.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Write Error", "Error writing to file.");
        }
    }
    
    // use the form fields and the button being clicked (Event handlers below) to add a new item to the list or update one
    public void createOrUpdate(Person p, boolean isInsert) {
        // don't allow commas
        if((!rdoFF.isSelected() && !rdoCC.isSelected() && !rdoTM.isSelected()) || txtFirstName.getText().isBlank() || txtLastName.getText().isBlank() || txtPhoneNumber.getText().isBlank() || txtEmailAddress.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "One or more fields are invalid.");
        } else {
            // Set the person fields. If the person exists, it is updated, otherwise it will be added after.
            p.setFirstName(txtFirstName.getText());
            p.setLastName(txtLastName.getText());
            // These try catches are split up to avoid having to determine which field is invalid
            try {
                p.setPhoneNumber(txtPhoneNumber.getText());
            } catch(IllegalArgumentException e) { 
                showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone number must be 10 digits.");
                return; // return so the person isnt saved with partial info
            }
            try {
                p.setEmailAddress(txtEmailAddress.getText());
            } catch(IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Email Address", "Invalid email address. Please correct your input.");
                return; // return so the person isnt saved with partial info
            }
            if(rdoFF.isSelected()) { p.setType(Person.FF); }  // Don't worry about the exception since we are using the classes static strings
            if(rdoCC.isSelected()) { p.setType(Person.CC); }
            if(rdoTM.isSelected()) { p.setType(Person.TM); }
            if(isInsert) { // if a new record being created, add the person to the list now. 
                pList.add(p); 
            }
            writeCSV(); 
            clearFieldsAndSelection();
        }
    }
           
    public void btnInsert_Click() {
        createOrUpdate(new Person(), true);
    }
    
    public void btnUpdate_Click() {
        Person p = table.getSelectionModel().getSelectedItem();
        createOrUpdate(p, false);
        table.refresh();
    }
    
    public void btnClear_Click() {
        clearFieldsAndSelection();
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
    
    public void btnDelete_Click() {
        Person p = table.getSelectionModel().getSelectedItem();
        if(p != null) {
            pList.remove(p);
            writeCSV();
            clearFieldsAndSelection();
        } else {
            showAlert(Alert.AlertType.ERROR, "No Customer Selected", "There is currently no customer selected to delete.");
        }
    }
    
    // Got tired of writing these 3 lines
    public void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg);
        a.setTitle(title);
        a.showAndWait();
    }
    
    @Override
    public void stop() {
//        writeCSV();
    }
        
    public static void main(String[] args) {
        launch();
    }
}
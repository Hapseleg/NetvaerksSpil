package client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class IpNameWindow extends Stage {
    
    private TextField txfIp, txfName;
    private Label lblError;
    
    public IpNameWindow() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(0, 10, 0, 10));
        
        Label lblIp = new Label("IP:");
        Label lblName = new Label("Navn:");
        lblError = new Label();
        txfIp = new TextField();
        txfName = new TextField();
        Button btnOk = new Button("Ok");
        btnOk.setOnAction(event -> okAction());
        pane.add(lblIp, 0, 0);
        pane.add(txfIp, 1, 0);
        pane.add(lblName, 0, 1);
        pane.add(txfName, 1, 1);
        pane.add(btnOk, 0, 2);
        pane.add(lblError, 0, 3);
        
        Scene scene = new Scene(pane);
        setScene(scene);
    }
    
    private void okAction() {
        close();
    }
    
    public String getName() {
        return txfName.getText();
    }
    
    public String getIp() {
        return txfIp.getText();
    }
    
    public void nameTaken() {
        lblError.setText("Navnet er taget");
    }
}

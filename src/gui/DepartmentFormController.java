package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {
	
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField txtFiledName;
	@FXML
	private TextField txtId;
	@FXML
	private Label labelError;
	
	
	@FXML
	public void onBtncSaveAction() {
		System.out.println("save");
	}
	
	@FXML
	public void onBtncCancelAction() {
		System.out.println("cancel");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	public void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtFiledName, 32);
	}
}

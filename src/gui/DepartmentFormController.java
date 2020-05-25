package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtId;
	@FXML
	private Label labelError;
	
	// CRIANDO A DEPENDENCIA DE DEPARTAMENTO
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	
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
		Constraints.setTextFieldMaxLength(txtName, 32);
	}
	
	public void updateFormData() { // RETORNA OS DADOS DO OBJETO NAS CAIXAS DE TEXTO
		if (entity == null) {
			throw new IllegalStateException("Entidade esta vazia");
		}
		if(entity.getId() == null) 		txtId.setText(String.valueOf(""));
		else							txtId.setText(String.valueOf(entity.getId()));
		
		txtName.setText(entity.getName());
	}
}

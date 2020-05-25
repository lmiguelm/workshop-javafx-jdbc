package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	// DEPENDENCIAS
	private Department entity;
	private DepartmentService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
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
	
	// INJETANDO A DEPENDENCIAS
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	public void subscriteDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	
	@FXML
	public void onBtncSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade nula");
		}
		if (service == null) {
			throw new IllegalStateException("Service nula");
		}
		
		try {			
			entity = getFormData(); //RESPONSAVEL POR PEGAR OS DADOS DO FORUMLARIO E CRIAR UM OBJETO DEPARTAMENTO
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close(); // FECHA A JANELA DO MODAL
		}
		catch (DbException e) {
			Alerts.showAlert("Erro salvando", null, e.getMessage(), AlertType.ERROR);
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener: dataChangeListeners) {
			listener.onDataChange();
		}
	}
	
	private Department getFormData() {
		
		Department obj = new Department();
		
		ValidationException exception = new ValidationException("Erro de validação");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			 exception.addError("name", "O campo nome não pode ser vazio");
			 throw exception;
		}
		obj.setName(txtName.getText());
		
		return obj;
	}
	@FXML
	public void onBtncCancelAction(ActionEvent event) {
		System.out.println("cancel");
		Utils.currentStage(event).close(); // FECHA A JANELA DO MODAL
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
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
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) { // VERIFICAR SE EXISTE A KEY NAME NO MAP EXCEPTION
			labelError.setText(errors.get("name"));
		}
	}
}

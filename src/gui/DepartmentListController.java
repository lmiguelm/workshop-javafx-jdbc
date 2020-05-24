package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	private DepartmentService service;
	
	
	
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btnNew;
	
	private ObservableList<Department> obsList;
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtnNewAction() {
		System.out.println("click");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));  // INICIAR O COMPORTAMENTO DAS COLUNAS
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // PEGA A REFERENCIA DA JANELA
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // FAZ A TABELA ACOMPANHAR A ALTURA DA JANELA
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service est� nulo");
		}
		else {
			List<Department> list = service.findAll(); // PEGA OS DEPARTAMENTOS CADASTRADOS
			obsList = FXCollections.observableArrayList(list); // JOGA DENTRO DO obsList
			tableViewDepartment.setItems(obsList); // CARREGA OS DEPARTAMENTOS NA TABELA
		}
	}
}

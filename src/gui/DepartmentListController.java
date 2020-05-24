package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {
	
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btnNew;
	
	
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
}

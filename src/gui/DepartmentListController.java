package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btnNew;

	private ObservableList<Department> obsList;

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@FXML
	public void onBtnNewAction(ActionEvent event) { // FUNÇÃO QUE RECEBE O EVENTO DO CLICK
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage); // CHAMA A FUNÇÃO QUE ABRE O MODAL
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name")); // INICIAR O COMPORTAMENTO DAS COLUNAS

		Stage stage = (Stage) Main.getMainScene().getWindow(); // PEGA A REFERENCIA DA JANELA
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // FAZ A TABELA ACOMPANHAR A ALTURA DA
																				// JANELA
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		} else {
			List<Department> list = service.findAll(); // PEGA OS DEPARTAMENTOS CADASTRADOS
			obsList = FXCollections.observableArrayList(list); // JOGA DENTRO DO obsList
			tableViewDepartment.setItems(obsList); // CARREGA OS DEPARTAMENTOS NA TABELA
			initEditButtons(); // ACRESCENTA UM BOTÃO DE EDIÇÃO DE DADOS
		}
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { // CRIANDO O MODA. FUNÇÃO
																								// PARA CARREGAR A
																								// JANELA MODAL
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController(); // PEGA O CONTROLLARDOR DO FORMULARIO
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscriteDataChangeListener(this); // ME INSCREVENDO PARA RECEBER O EVENTO
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Dados do departamento"); // TITULO DO MODAL
			dialogStage.setScene(new Scene(pane)); // INSTANCIAR A CENA
			dialogStage.setResizable(false); // REDIMENSIONAR A JANELA
			dialogStage.initOwner(parentStage); // PASSANDO QUEM É O PAI DO MODAL
			dialogStage.initModality(Modality.WINDOW_MODAL); // INDICA QUE A JANELA É UM MODAL
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exceotio", "Erro carregando view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
}

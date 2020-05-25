package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	@FXML
	private Button btnNew;

	private ObservableList<Seller> obsList;

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@FXML
	public void onBtnNewAction(ActionEvent event) { // FUNÇÃO QUE RECEBE O EVENTO DO CLICK
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage); // CHAMA A FUNÇÃO QUE ABRE O MODAL
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy"); // FORMATANDO O TIPO DATE
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary")); // INICIAR O COMPORTAMENTO DAS COLUNAS
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2); // FORMATANDO AS CASAS DECIMAIS

		Stage stage = (Stage) Main.getMainScene().getWindow(); // PEGA A REFERENCIA DA JANELA
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); // FAZ A TABELA ACOMPANHAR A ALTURA DA JANELLA
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		} else {
			List<Seller> list = service.findAll(); // PEGA OS DEPARTAMENTOS CADASTRADOS
			obsList = FXCollections.observableArrayList(list); // JOGA DENTRO DO obsList
			tableViewSeller.setItems(obsList); // CARREGA OS DEPARTAMENTOS NA TABELA
			initEditButtons(); // ACRESCENTA UM BOTÃO DE EDIÇÃO DE DADOS
			initRemoveButtons(); // ACRESCENTA UM BOTÃO PARA DELETAR 
		}
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) { // CRIANDO O MODA. FUNÇÃO
																							// PARA CARREGAR A																					// JANELA MODAL
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController(); // PEGA O CONTROLLARDOR DO FORMULARIO
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());
//			controller.subscriteDataChangeListener(this); // ME INSCREVENDO PARA RECEBER O EVENTO
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Dados do departamento"); // TITULO DO MODAL
//			dialogStage.setScene(new Scene(pane)); // INSTANCIAR A CENA
//			dialogStage.setResizable(false); // REDIMENSIONAR A JANELA
//			dialogStage.initOwner(parentStage); // PASSANDO QUEM É O PAI DO MODAL
//			dialogStage.initModality(Modality.WINDOW_MODAL); // INDICA QUE A JANELA É UM MODAL
//			dialogStage.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exceotio", "Erro carregando view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChange() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar este departamento?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service esta nulo");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro removendo vendedor", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}
}

package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	
	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
	}
	
	// CARREGAR UMA TELA
	private synchronized <T> void loadView(String absoluteName, Consumer<T> acaoIncicializacao) { //FUNCAO GENEREICA
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // PEGA O PRIMEIRO ELEMENTO DA VIEW (ScrollPane) e acessa o conteudo
			
			Node mainMenu = mainVBox.getChildren().get(0); // RECUPERA O PRIMEIRO FILHO DO VBOX NA JANELA PRINCIPAL (MAINMENU)
			mainVBox.getChildren().clear(); // LIMPA TODOS OS FILHOS DO MAINVBOX
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// COMANDO PARA ATIVAR A FUNÇÃO GENERICA PASSADA NO PARAMETRO
			T controller = loader.getController(); // RETORNA O CONTROLADOR DO TIPO QUE FOR CHAMADO
			acaoIncicializacao.accept(controller); // EXECUTA A FUNÇÃO PASSADA COMO ARGUMENTO
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro carregando a página", e.getMessage(), AlertType.ERROR);
		}
	}
}

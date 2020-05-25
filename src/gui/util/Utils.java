package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) { //PALCO ATUAL
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // ACESSA O STAGE AONDE O CONTROLLER QUE RECEBEU O EVENTO EST�
	}
	
	public static Integer tryParseToInt(String str) {
		try {			
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}

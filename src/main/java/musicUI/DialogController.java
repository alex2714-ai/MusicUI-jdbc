package musicUI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class DialogController {

    @FXML
    private TextField nameField;

    public void setNameField(String nameField) {
        this.nameField.setText(nameField);
    }

    public String getName(){
        return nameField.getText().trim();
    }


}

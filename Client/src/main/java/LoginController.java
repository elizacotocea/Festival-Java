import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginController extends UnicastRemoteObject implements Serializable {

        @FXML
        TextField usernameField;

        @FXML
        PasswordField passwordField;

        private Stage dialogStage;
        private IService server;

        public LoginController() throws RemoteException {
        }

    public void setService(IService server, Stage stage){
            this.server = server;
            this.dialogStage = stage;
        }

        @FXML
        public void signInAction() throws IOException {

            Runnable runnable = () -> {
                String userName = this.usernameField.getText();
                String password = this.passwordField.getText();
                Employee user = new Employee(0,userName, password);
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/views/mainView.fxml"));

                    AnchorPane root = (AnchorPane) loader.load();

                    // Create the dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Log In Client");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    MainController mController = loader.getController();
                    mController.setService(server, dialogStage);

                    mController.setLoggedInClient(user);


                    this.server.login(user, mController); //LOGIN HERE, AFTER WE HAVE INITIALISED THE CONTROLLER.

                    this.dialogStage.close();
                    dialogStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "ATENTIE", e.getMessage());
                }
            };
            Platform.runLater(runnable);
        }
}



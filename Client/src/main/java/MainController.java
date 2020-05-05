import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainController extends UnicastRemoteObject implements Serializable,IObserver {
   // ObservableList<Show> modelGrade = FXCollections.observableArrayList();
    ObservableList<Show> artists = FXCollections.observableArrayList();

    private Employee loggedInClient;
    private IService server;
    private ObservableList<Show> model = FXCollections.observableArrayList();
    private Stage dialogStage;
    @FXML
    TableView showsTable;
    @FXML
    TableView artistTable;

    @FXML
    TableColumn aArtistColumn;
    @FXML
    TableColumn aLocationColumn;
    @FXML
    TableColumn aAvailableColumn;
    @FXML
    TableColumn aHourColumn;

    @FXML
    TableColumn sArtistColumn;
    @FXML
    TableColumn sLocationColumn;
    @FXML
    TableColumn sDateColumn;
    @FXML
    TableColumn sSoldColumn;
    @FXML
    TableColumn sAvailableColumn;
    @FXML
    TableColumn sIdColumn;

    @FXML
    TextField wantedSeatsField;
    @FXML
    TextField buyerNameField;

    @FXML
    DatePicker calendar;

    public MainController() throws RemoteException {
    }
    public void setService(IService server, Stage stage){
        this.dialogStage = stage;
        this.server = server;
        initModel();
        calendar.setValue(LocalDate.now());
    }


    private void initModel() {
        Runnable runnable = () -> {
            try {
                Iterable<Show> showuri = Arrays.asList(server.findAllShows()); //called from proxy

                List<Show> showList = StreamSupport
                        .stream(showuri.spliterator(), false)
                        .collect(Collectors.toList());
                System.out.println(showList);
                model.setAll(showList);
                initialize();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        };
        Platform.runLater(runnable);
    }

    public void setLoggedInClient(Employee loggedInClient){
        this.loggedInClient = loggedInClient;
        //this.labelStudent.setText(loggedInClient.getId());
        //this.textFieldNume.setText(loggedInClient.getId());
    }

    @FXML
    public void initialize() {
        sIdColumn.setCellValueFactory(new PropertyValueFactory<Show,Integer>("ID"));
        sArtistColumn.setCellValueFactory(new PropertyValueFactory<Show, String>("artistName"));
        sLocationColumn.setCellValueFactory(new PropertyValueFactory<Show, String>("location"));
        sDateColumn.setCellValueFactory(new PropertyValueFactory<Show, LocalDateTime>("dataTimp"));
        sSoldColumn.setCellValueFactory(new PropertyValueFactory<Show, Integer>("nrSoldSeats"));
        sAvailableColumn.setCellValueFactory(new PropertyValueFactory<Show, Integer>("nrAvailableSeats"));
        showsTable.setItems(model);
        showsTable.setRowFactory(tv -> new TableRow<Show>() {
            @Override
            protected void updateItem(Show item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null)
                    setStyle("");
                else if (item.getNrAvailableSeats()== 0)
                    setStyle("-fx-background-color: red;");
                else
                    setStyle("");
            }
        });
    }

    public void searchAction(){
        Show[] shows= null;
        try {
            shows = server.findAllShows();
            List<Show> rez =new ArrayList<>();
            LocalDate data=calendar.getValue();
            for (Show s:shows){
                if (s.getDataTimp().toLocalDate().isEqual(data))
                {
                    rez.add(s);
                }
            }
            artists.setAll(rez);
            populateArtistTable();
        } catch (ServiceException  e) {
            e.printStackTrace();
        }

    }

    private void populateArtistTable(){
        aArtistColumn.setCellValueFactory(new PropertyValueFactory<Show, String>("artistName"));
        aLocationColumn.setCellValueFactory(new PropertyValueFactory<Show, String>("location"));
        aAvailableColumn.setCellValueFactory(new PropertyValueFactory<Show, Integer>("nrAvailableSeats"));
        aHourColumn.setCellValueFactory(new PropertyValueFactory<Show,String>("dataTimp"));
        artistTable.setItems(artists);
    }

    public void logOutAction(ActionEvent actionEvent){
        Runnable runnable = () -> {
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/loginView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                LoginController loginFormController = loader.getController();
                loginFormController.setService(server, dialogStage);

                this.server.logout(loggedInClient, this);
                this.dialogStage.close();
                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        };
        Platform.runLater(runnable);
    }

    public void sellAction(){
        if (showsTable.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "ATENTIE", "Nu ati ales niciun spectacol!");
        } else {
            String item = showsTable.getSelectionModel().getSelectedItem().toString();
            String[] items = item.split(";");
            try {
                if (Integer.parseInt(wantedSeatsField.getText())>Integer.parseInt(items[3]))
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "ATENTIE", "Nu exista suficiente locuri!");
                else {
                    Show sh=new Show(Integer.parseInt(items[0]),LocalDateTime.now(),"location",0,0,"artist");
                    Ticket ticket=new Ticket(0,Integer.parseInt(wantedSeatsField.getText()),buyerNameField.getText(),Integer.parseInt(items[0]));
                    server.ticketsSold(sh,ticket);
                    wantedSeatsField.clear();
                    buyerNameField.clear();
                }
            } catch (NumberFormatException | ServiceException  e) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "ATENTIE", "Parsare esuata!");

            }
        }
        showsTable.getSelectionModel().clearSelection();
    }

    @Override
    public void notifyTicketsSold(Show show) throws ServiceException {
        Runnable runnable = () -> {
            System.out.println("REQUEST TO REFRESH TABLE");
            for (Show m : this.model) {
                if (m.getID().equals(show.getID())) {
                    System.out.println(m.getID().toString()+" "+show.getID());
                    this.model.remove(m);
                    this.model.add(show);
                    break;
                }
            }
            showsTable.setItems(model);
            //initialize();
            System.out.println(" FINISHED TO REFRESH TABLE");
        };
        Platform.runLater(runnable);
    }
}




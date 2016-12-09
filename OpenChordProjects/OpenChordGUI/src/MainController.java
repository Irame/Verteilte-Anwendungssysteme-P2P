import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {
    public ListView<ChordNode> localChordNodeListView;
    public ObservableList<ChordNode> localChordNodeList;
    public Button removeNodeButton;


    public ListView<Node> networkScanResultListView;
    public ObservableList<Node> localNodeList;
    public Button networkScanButton;
    public CheckBox automatedScanCheckBox;
    public CheckBox saveScanToFileCheckBox;
    public TextField saveScanToFileTextField;

    public Label nodeLabel;
    public Label referencesLabel;
    public Label entriesLabel;

    public TextField entryKeyTextField;
    public TextField entryDataTextField;
    public Button addEntryButton;
    public Button removeEntryButton;

    private Thread scannerThread;

    public void init() {
        localChordNodeList = FXCollections.observableList(new ArrayList<>());
        localChordNodeListView.setItems(localChordNodeList);

        localNodeList = FXCollections.observableList(new ArrayList<>());
        networkScanResultListView.setItems(localNodeList);

        removeNodeButton.disableProperty().bind(localChordNodeListView.getSelectionModel().selectedItemProperty().isNull());
        networkScanButton.disableProperty().bind(localChordNodeListView.getSelectionModel().selectedItemProperty().isNull());

        addEntryButton.disableProperty()
                .bind(localChordNodeListView.getSelectionModel().selectedItemProperty().isNull()
                        .or(entryKeyTextField.textProperty().isEmpty())
                        .or(entryDataTextField.textProperty().isEmpty()));

        removeEntryButton.disableProperty()
                .bind(localChordNodeListView.getSelectionModel().selectedItemProperty().isNull()
                        .or(entryKeyTextField.textProperty().isEmpty())
                        .or(entryDataTextField.textProperty().isEmpty()));

        localChordNodeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nodeLabel.setText(newValue.name);
                referencesLabel.setText(newValue.getReferencesAsString());
                entriesLabel.setText(newValue.getEntriesAsString());
            } else {
                nodeLabel.setText("No Node selected...");
                referencesLabel.setText("");
                entriesLabel.setText("");
            }
        });

        networkScanResultListView.setCellFactory(lv -> new ListCell<Node>() {
            @Override
            public void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getNodeID().toHexString(4) + " - "  + item.getNodeURL());
                }
            }
        });

        scannerThread = new Thread(new AutomatedNetworkScan());
        scannerThread.setDaemon(true);
        scannerThread.start();

        automatedScanCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> scannerThread.interrupt());
    }

    public void addNewNode(ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addNodeWindow.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Add new Node");
            stage.setScene(new Scene(root1));
            stage.setResizable(false);

            AddNodeController controller = fxmlLoader.getController();
            ChordNode chordNode = localChordNodeListView.getSelectionModel().getSelectedItem();
            if (chordNode != null) {
                controller.init(true, chordNode.getUrl().getHost(), String.valueOf(chordNode.getUrl().getPort()));
            } else {
                controller.init();
            }
            stage.setOnHidden(event -> {
                if (controller.chordNode != null) {
                    localChordNodeList.add(controller.chordNode);
                }
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNode(ActionEvent actionEvent) {
        int idx = localChordNodeListView.getSelectionModel().getSelectedIndex();
        ChordNode node = localChordNodeList.get(idx);
        try {
            node.leave();
        } catch (ServiceException e) {
            Utils.openAlert(AlertType.ERROR, "Failed to remove node!", "Failed to remove node from network! (" + e + ")");
            return;
        }
        localChordNodeList.remove(idx);
    }

    public void startNetworkScan(ActionEvent actionEvent) throws IllegalAccessException {
        if (localChordNodeListView.getSelectionModel().isEmpty()) return;
        localNodeList.clear();
        try {
            localNodeList.addAll(localChordNodeListView.getSelectionModel().getSelectedItem().scanNetworkFromThisNode());
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        if (saveScanToFileCheckBox.isSelected()) {
            try (FileWriter fw = new FileWriter(saveScanToFileTextField.getText())) {
                String data = localNodeList.stream().map(node -> node.getNodeID().toHexString().trim() + ";" + node.getNodeURL()).collect(Collectors.joining("\n"));
                fw.write(data);
                fw.flush();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void addDataEntry(ActionEvent actionEvent) {
        localChordNodeListView.getSelectionModel().getSelectedItem().insert(entryKeyTextField.getText(), entryDataTextField.getText());
    }

    public void removeDataEntry(ActionEvent actionEvent) {
        localChordNodeListView.getSelectionModel().getSelectedItem().remove(entryKeyTextField.getText(), entryDataTextField.getText());
    }

    private class AutomatedNetworkScan implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (automatedScanCheckBox.isSelected()) {
                        Platform.runLater(() -> {
                            try {
                                startNetworkScan(null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {

                }
            }
        }
    }
}

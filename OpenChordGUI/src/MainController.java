import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class MainController {
    public ListView<ChordNode> localChordNodeListView;
    public ObservableList<ChordNode> localChordNodeList;
    public Button removeNodeButton;


    public ListView<Node> networkScanResultListView;
    public ObservableList<Node> localNodeList;

    public Label nodeLabel;
    public Label referencesLabel;
    public Label entriesLabel;

    public void init() {
        localChordNodeList = FXCollections.observableList(new ArrayList<>());
        localChordNodeListView.setItems(localChordNodeList);

        localNodeList = FXCollections.observableList(new ArrayList<>());
        networkScanResultListView.setItems(localNodeList);

        removeNodeButton.disableProperty().bind(Bindings.size(localChordNodeList).isEqualTo(0));

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
        localNodeList.clear();
        try {
            localNodeList.addAll(localChordNodeListView.getSelectionModel().getSelectedItem().scanNetworkFromThisNode());
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
    }

    public void addDataEntry(ActionEvent actionEvent) {

    }

    public void removeDataEntry(ActionEvent actionEvent) {

    }
}

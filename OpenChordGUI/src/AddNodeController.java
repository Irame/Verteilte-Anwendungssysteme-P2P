import de.uniba.wiai.lspi.chord.service.ServiceException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Created by Felix on 25.11.2016.
 */
public class AddNodeController {
    public TextField nameTextField;
    public TextField localAddressTextField;
    public TextField localPortTextField;
    public CheckBox joinCheckBox;
    public TextField otherAddressTextField;
    public TextField otherPortTextField;
    public Label otherAddressLabel;
    public Label otherPortLabel;

    public ChordNode chordNode;

    public void init() {
        init(false, null, null);
    }

    public void init(boolean join, String otherIp, String otherPort){
        chordNode = null;
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            localAddressTextField.setText(localAddress.getHostAddress());
        } catch (UnknownHostException e) {
            //e.printStackTrace();
        }

        try {
            ServerSocket s = new ServerSocket(0, 0, localAddress);
            localPortTextField.setText(String.valueOf(s.getLocalPort()));
            s.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        joinCheckBox.setSelected(join);
        if (join) {
            otherAddressTextField.setText(otherIp);
            otherPortTextField.setText(otherPort);
        }

        otherAddressTextField.disableProperty().bind(joinCheckBox.selectedProperty().not());
        otherPortTextField.disableProperty().bind(joinCheckBox.selectedProperty().not());
        otherAddressLabel.disableProperty().bind(joinCheckBox.selectedProperty().not());
        otherPortLabel.disableProperty().bind(joinCheckBox.selectedProperty().not());
    }

    public void cancel(ActionEvent actionEvent) {
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void addNode(ActionEvent actionEvent) {
        ChordNode node = new ChordNode(nameTextField.getText());

        try {
            node.setUrl(localAddressTextField.getText(), localPortTextField.getText());
            if (joinCheckBox.isSelected()) {
                node.join(otherAddressTextField.getText(), otherPortTextField.getText());
            } else {
                node.create();
            }
        } catch (Exception e) {
            Utils.openAlert(AlertType.ERROR, "Connection Failed!", "Connection Failed (" + e + ")");
            return;
        }

        chordNode = node;
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
}

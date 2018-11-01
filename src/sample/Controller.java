package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Controller {
    @FXML
    private TextField txtFieldKi,txtFieldKd,txtFieldKp;
    @FXML
    private Label lblX,lblY;
    @FXML
    private ComboBox<String> cBComPort;
    @FXML
    private Pane paneXY;
    private SerialPort currentConn;
    private SerialCommunicator sc;

    @FXML
    public void initialize(){
        refresh();

    }
    @FXML
    private void refresh(){
        cBComPort.getItems().clear();
        SerialPort ports[] = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            cBComPort.getItems().add(port.getSystemPortName());
        }
        cBComPort.getSelectionModel().select(0);
    }

    @FXML
    private void getValues(){
        if(currentConn== null||(!currentConn.isOpen())){
            if(! initComm())return;
        }
        sc.send("w");
    }

    @FXML
    private void submit(){
        if(currentConn== null||(!currentConn.isOpen())){
            if(! initComm())return;
        }
     sc.send("r",txtFieldKi.getText(),txtFieldKd.getText()
            ,txtFieldKp.getText(),lblX.getText(),lblY.getText());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean initComm(){
        boolean success;
            currentConn = SerialPort.getCommPort(cBComPort.getSelectionModel().getSelectedItem());
            currentConn.setBaudRate(9600);
            currentConn.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
           success = currentConn.openPort();
            if (!success) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Fehler!");
                a.setContentText(String.format("Fehler beim Öffnen von Port %s", currentConn.getSystemPortName()));
                a.show();
                return false;
            }
            sc = new SerialCommunicator(currentConn, this);
            sc.setDaemon(true);
            sc.start();
            return true;
    }

    @FXML
    private void mouseClick(MouseEvent e){
        System.out.println((e.getX()/4)+","+((paneXY.getHeight()-e.getY())/4)+","+paneXY.getHeight());
        paneXY.getChildren().get(paneXY.getChildren().size()-1).setLayoutX(e.getX());
        paneXY.getChildren().get(paneXY.getChildren().size()-1).setLayoutY(e.getY());
        lblX.setText(String.valueOf((int)e.getX()));
        lblY.setText(String.valueOf((int)(paneXY.getHeight()-e.getY())));
        if(currentConn== null||(!currentConn.isOpen())){
           if(! initComm())return;
        }
        sc.send("r",txtFieldKi.getText(),txtFieldKd.getText()
                ,txtFieldKp.getText(),lblX.getText(),lblY.getText());
    }

    public class SetValues implements Runnable{

        float Ki,Kd,Kp;
        int setX,setY;

        SetValues(float Ki, float Kd, float Kp, int setX, int setY){
            this.Ki=Ki; this.Kd=Kd; this.Kp=Kp;
            this.setX=setX; this.setY=setY;
        }

        @Override
        public void run() {
            txtFieldKi.setText(String.valueOf(Ki));
            txtFieldKd.setText(String.valueOf(Kd));
            txtFieldKp.setText(String.valueOf(Kp));
            lblX.setText(String.valueOf(setX));
            lblY.setText(String.valueOf(setY));
            paneXY.getChildren().get(paneXY.getChildren().size()-1).setLayoutX(setX);
            paneXY.getChildren().get(paneXY.getChildren().size()-1).setLayoutY(paneXY.getHeight()-setY);
        }
    }

    public class NotifyUser implements Runnable {

        Alert.AlertType type;
        String title,message;

       NotifyUser(Alert.AlertType type,String title, String message){
            this.title=title;
            this.message=message;
            this.type=type;
        }

        @Override
        public void run() {
            Alert a = new Alert(type);
            a.setTitle(title);
            a.setContentText(message);
            a.show();
        }
    }
}
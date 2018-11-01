package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SerialCommunicator extends Thread{

    private boolean received=false;
    private Scanner in;
    private PrintWriter out;
    private String input;
    private List<String> toSend =new ArrayList<>();
    private Controller controller;
    private boolean send;
    private SerialPort port;

    SerialCommunicator(SerialPort port, Controller controller){
        this.controller=controller;
        this.port=port;
        in = new Scanner(port.getInputStream());
        out = new PrintWriter(port.getOutputStream());
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                while(port.bytesAvailable()>0)
                input = in.nextLine();

                System.out.println("return: " + input);
                received=true;
            }
        });
    }

   void send(String... toSend){
        this.toSend.clear();
        for(String s:toSend){
            this.toSend.add(s);
        }
        send=true;
   }

    private String send(){
        input=" ";
        received=false;
        while ((!received)&&(!(input.charAt(0)==toSend.get(0).charAt(0)))){
            for(String s: toSend){
                if(s.contains(".")) {
                    out.print(Float.parseFloat(s));
                    out.print(" ");
                }else if(s.matches("\\d+")){
                    out.print(Integer.valueOf(s));
                    out.print(" ");
                }else {
                    out.print(s + " ");
                }
            }
            out.flush();
            try{
                Thread.sleep(20);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        return input;
    }


    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            if(send) {
                String receive;
                receive = send();
                receive = receive.replaceAll("\n", "");
                String[] received = receive.split(",");
                if (receive.length() > 1) {
                    Platform.runLater(controller.new SetValues(Float.parseFloat(received[1]),
                            Float.parseFloat(received[2]), Float.parseFloat(received[3]), Integer.valueOf(received[4]), Integer.valueOf(received[5])));
                }
                Platform.runLater(controller.new NotifyUser(Alert.AlertType.INFORMATION,"Fertig","Fertig! :"+input));
            }
            send=false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

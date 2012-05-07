/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Converter;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFSend extends Thread {

    private InetAddress _addr;
    private int _port;
    private DatagramSocket _socket;
    
    private UDPFDatabase _db;
    
    private boolean _run;

    public UDPFSend(UDPFDatabase db, DatagramSocket socket, InetAddress addr, int port) {
        _socket = socket;
        _db = db;
        _run = true;
        _addr = addr;
        _port = port;
    }

    @Override
    public void run() {
        try {
            byte[] send_info = new byte[1024];
            int i = 0;
            while (_run) {
                send_info = Converter.objectToBytes(_db.get(i));
                _socket.send(new DatagramPacket(send_info, send_info.length, _addr, _port));
                i++;
		Debug.dump("Debug:: Send:: SENT!");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(UDPFSend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPFSend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setPort(int port) {
        _port = port;
    }

    public void stopSend() {
        _run = false;
    }
}

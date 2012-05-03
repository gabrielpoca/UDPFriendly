/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import udpf.UDPFSend;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpf.UDPFDatabase;
import udpf.UDPFDatagram;
import utils.Converter;
import utils.Debug;

public class UDPFClientSide extends Thread {

    public static final int BUFFER_SIZE = 512;
    public static final int PORT = 9998;
    /* Server Information. */
    private InetAddress _addr;
    private int _port;
    
    
    int _sent;
    int _confirmed; 

    /* Local variables. */
    private DatagramSocket _socket;
    private UDPFDatabase _db;
    private UDPFSend _send;
    private String _file;
    int _wait_type; // next datagram type. -1 for none.
    private boolean _run;

    public UDPFClientSide(String file) throws SocketException, UnknownHostException {
	// Start socket and database.
	_socket = new DatagramSocket(PORT);
	_db = new UDPFDatabase();
	// Set send
	_addr = InetAddress.getByName("localhost");
	_port = 9999;
	_send = new UDPFSend(_db, _socket, _addr, _port);
	// Set file
	_file = file;
	// Set run
	_run = true;
	// none type waiting.
	_wait_type = -1;
	// ACK confirmation
	_sent = _confirmed = 0;
    }

    
    public void run() {
	/* Start send. */
	Thread t = new Thread(_send);
	t.start();
	/* Send SYN message and wait SYN_ACK. */
	putStartDatagram();
	_wait_type = UDPFDatagram.UDPF_HEADER_TYPE.SYN_ACK.ordinal();
	while (_run) {
	    try {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		_socket.receive(receivePacket);
		UDPFDatagram receiveDatagram = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
		Debug.dump("CLIENT: Package Received! " + receiveDatagram.getType().name());
		/* if waiting type is correct or none is waiting. */
		if (receiveDatagram.getType().ordinal() == _wait_type || _wait_type != -1) {
		    switch (receiveDatagram.getType()) {
			case SYN_ACK: // End Handshake
			    /* Update Server Port. */
			    _port = receivePacket.getPort();
			    _send.setPort(_port);   
			    /* Send File. */
			    putFile();
			    putEndComunication();
			    break;
			case ACK:
			    _confirmed++;
			    break;
			case FIN_ACK: // End Comunication
			    _run = false;
			    break;
		    }
		} else {
		    System.out.println("Wrong package received!");
		}
	    } catch (ClassNotFoundException ex) {
		Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
		Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	_send.stopSend();
    }

    public void putFile() throws FileNotFoundException, IOException {
	byte[] file_info = Converter.filetoBytes(_file);
	UDPFDatagram datagram = new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.INFO);
	datagram.setData(file_info);
	datagram.setSeqNum(1);
	_db.put(datagram);
	_sent++;
    }

    /**
     * End comunication.
     */
    public void putEndComunication() {
	_db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.FIN));
    }

    /**
     * Put syn message in database to be sent.
     */
    public void putStartDatagram() {
	_db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.SYN));

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	try {
	    Thread t = new Thread(new UDPFClientSide("/Users/gabrielpoca/Projects/UDPFriendly/code/file.txt"));
	    t.start();
	    t.join();
	} catch (SocketException ex) {
	    Logger.getLogger(UDPFClient.class.getName()).log(Level.SEVERE, null, ex);
	} catch (UnknownHostException ex) {
	    Logger.getLogger(UDPFClient.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InterruptedException ex) {
	    Logger.getLogger(UDPFClient.class.getName()).log(Level.SEVERE, null, ex);
	}
    }    
}

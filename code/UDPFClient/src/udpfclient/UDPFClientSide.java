package udpfclient;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Observable;
import udpf.UDPFSend;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpf.UDPFDatabase;
import udpf.UDPFDatabaseWindow;
import udpf.UDPFDatagram;
import udpf.UDPFTimeout;
import utils.Converter;
import utils.Debug;

public class UDPFClientSide extends Thread implements Observer {

    public static final int BUFFER_SIZE = 512;
    public static final int PORT = 9998;
    public static final int TIMEOUT = 5000;
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
    private UDPFTimeout _timeout;

    public UDPFClientSide(String file) throws SocketException, UnknownHostException {
	// Start socket and database.
	_socket = new DatagramSocket(PORT);
	_db = new UDPFDatabaseWindow(3);
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

	// Timeout
	_timeout = new UDPFTimeout();
	_timeout.addObserver(this);
    }

    public void run() {
	try {
	    /* Start timeout. */
	    Thread time = new Thread(_timeout);
	    time.start();
	    _socket.setSoTimeout(TIMEOUT);
	    /* Start send. */
	    Thread t = new Thread(_send);
	    t.start();
	    /* Send SYN message and wait SYN_ACK. */
	    putStartDatagram();
	    _wait_type = UDPFDatagram.UDPF_HEADER_TYPE.SYN_ACK.ordinal();
	    while (_run) {
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
			    /* Wait for ack and start timeout. */
			    _wait_type = UDPFDatagram.UDPF_HEADER_TYPE.ACK.ordinal();
			    _timeout.waitNewTime(TIMEOUT);
			    break;
			case ACK:
			    _confirmed++;
			    _timeout.waitNewTime(TIMEOUT);
			    if (_sent == _confirmed) {
				_wait_type = UDPFDatagram.UDPF_HEADER_TYPE.FIN_ACK.ordinal();
				putEndComunication();
			    }
			    break;
			case FIN_ACK: // End Comunication
			    _run = false;
			    break;
		    }
		} else {
		    System.out.println("Wrong package received!");
		}

	    }
	    System.out.println("Ending client!");
	    _send.stopSend();
	    _timeout.stop();
	} catch (ClassNotFoundException ex) {
	    Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SocketTimeoutException e) {
	    
	} catch (IOException ex) {
	    Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void putFile() throws FileNotFoundException, IOException {
	byte[] file_info = Converter.filetoBytes(_file);
	UDPFDatagram datagram = new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.INFO);
	datagram.setSeqNum(9999);

	byte[] buffer = null;
	int buffer_length = 512 - Converter.objectToBytes(datagram).length - 22;
	/* Create file datagrams. */
	for (int i = 0, seq = 0; i < file_info.length; i += buffer.length, seq++) {
	    // Update length if needed
	    if (i + buffer_length >= file_info.length) {
		buffer_length = file_info.length - i;
	    }
	    datagram = new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.INFO);
	    buffer = new byte[buffer_length];
	    System.arraycopy(file_info, i, buffer, 0, buffer_length);
	    datagram.setData(buffer);
	    datagram.setSeqNum(seq);
	    _db.put(datagram);
	    _sent++;
	}
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

    @Override
    public void update(Observable o, Object o1) {
	if (_wait_type == UDPFDatagram.UDPF_HEADER_TYPE.ACK.ordinal()) {
	    Debug.dump("CLIENT: TIMEOUT ACK!");
	    _wait_type = UDPFDatagram.UDPF_HEADER_TYPE.FIN_ACK.ordinal();
	    putEndComunication();
	    _run = false;
	}
    }
}

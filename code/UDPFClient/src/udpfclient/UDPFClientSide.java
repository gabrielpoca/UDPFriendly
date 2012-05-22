package udpfclient;

import java.io.BufferedWriter;
import udpf.UDPFRTT;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.UnknownHostException;
import udpf.UDPFSend;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpf.UDPFDatabaseWindow;
import udpf.UDPFDatagram;
import utils.Converter;
import utils.Debug;

public class UDPFClientSide extends Thread {

    public static final int BUFFER_SIZE = 512;
    public static final int PORT = 9998;
    private UDPFRTT _timeout;

    /* Server Information. */
    private InetAddress _addr;
    private int _port;
    int _exiting;
    int _sent;
    int _confirmed;
    int _lost;
    
    /* Local variables. */
    private DatagramSocket _socket;
    private UDPFDatabaseWindow _db;
    private UDPFSend _send;
    private String _file;
    int _wait_type; // next datagram type. -1 for none.
    private boolean _run;

    public UDPFClientSide(String file, int port) throws SocketException, UnknownHostException {

	_timeout = new UDPFRTT();
	// Start socket and database.
	_socket = new DatagramSocket(port);
	_db = new UDPFDatabaseWindow();
	// Set send
	_addr = InetAddress.getByName("localhost");
	_port = 9999;
	_send = new UDPFSend(_db, _socket, _addr, _port, _timeout);
	// Set file
	_file = file;
	// Set run
	_run = true;
	// none type waiting.
	_wait_type = -1;
	// ACK confirmation
	_exiting = _confirmed = _sent = _lost = 0;

	// Timeout
	//_timeout = new UDPFTimeout();
	//_timeout.addObserver(this);

    }

    public void run() {
	try {
	    /* Start send. */
	    Thread t = new Thread(_send);
	    t.start();
	    /* Send SYN message and wait SYN_ACK. */
	    putStartDatagram();
	    _wait_type = UDPFDatagram.UDPF_HEADER_TYPE.SYN_ACK.ordinal();
	    sendOne();
	    while (_run) {
		try {
		    /* Update Timeout. */
		    _socket.setSoTimeout((int) _timeout.getTimeout());
		    /* Wait Package. */
		    byte[] buffer = new byte[BUFFER_SIZE];
		    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		    _socket.receive(receivePacket);
		    /* Decode Package. */
		    UDPFDatagram receiveDatagram = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
		    //Debug.dumpPackageReceived(receiveDatagram.getType().name());
		    /* if waiting type is correct or none is waiting. */
		    if (receiveDatagram.getType().ordinal() == _wait_type || _wait_type != -1) {
			switch (receiveDatagram.getType()) {
			    case SYN_ACK: // End Handshake
			    /* Update Server Port. */
				_port = receivePacket.getPort();
				_send.setPort(_port);
				/* Send File. */
				putFile();
				send();
				/* Wait for ack and start timeout. */
				_wait_type = UDPFDatagram.UDPF_HEADER_TYPE.ACK.ordinal();
				break;
			    case ACK:
				/* Confirm package received. */
				if (receiveDatagram.getSeqNum() == _confirmed) {
				    _confirmed++;
				    _timeout.addRecvTime(System.currentTimeMillis(), receiveDatagram.getSeqNum());
				} else {
				    Debug.dumpException("OUT OF ORDER ACK");
				    if (receiveDatagram.getSeqNum() > _confirmed) {
					_lost += receiveDatagram.getSeqNum() - _confirmed;
					_confirmed = (int) (receiveDatagram.getSeqNum() + 3);
					_timeout.setTreshold(_timeout.getWindow() / 2);
					_timeout.setWindow(1);
				    }
				}

				// if all confirmed or no more to be confirmed
				if (_confirmed == _sent) {
				    if (_exiting <= _sent) {
					_wait_type = UDPFDatagram.UDPF_HEADER_TYPE.FIN_ACK.ordinal();
					putEndComunication();
					sendOne();
				    } else {
					_timeout.incWindow();
					send();
				    }
				}
				break;
			    case FIN_ACK: // End Comunication
				_run = false;
				break;
			}
		    } else {
			Debug.dumpMessage("Wrong package received! " + receiveDatagram.getType());
		    }
		} catch (SocketTimeoutException e) {
		    Debug.dumpException("TIMEOUT EXCEPTION");
		    _lost += _sent - _confirmed;
		    _confirmed = _sent;
		    if (_exiting <= _sent) {
			_wait_type = UDPFDatagram.UDPF_HEADER_TYPE.FIN_ACK.ordinal();
			putEndComunication();
			sendOne();
			_run = false;
		    } else {
			_timeout.setTreshold(_timeout.getWindow() / 2);
			_timeout.setWindow(1);
			send();
		    }
		}
	    }
	    Debug.dumpMessage("Ending client!");
	    _send.stopSend();
	    dumpTimes();
	    Debug.dumpMessage("Total Packages: "+_exiting);
	    Debug.dumpMessage("Lost Packages: "+_lost);
	    //_timeout.stop();
	} catch (ClassNotFoundException ex) {
	} catch (IOException ex) {
	}
    }

    public void dumpTimes() {
	Debug.dumpMessage("Dump Log to File");
	TreeMap<Long, Long> sent = _timeout.getSentDB();
	TreeMap<Long, Long> recv = _timeout.getRecvDB();

	TreeMap<Long, ArrayList<String>> sorted = new TreeMap<Long, ArrayList<String>>();

	/* put sent packages */
	// for each key
	for (Long entry : sent.keySet()) {
	    // if the value doesnt exist
	    if (!sorted.containsKey(sent.get(entry))) // create the value
	    {
		sorted.put(sent.get(entry), new ArrayList<String>());
	    }
	    sorted.get(sent.get(entry)).add("S" + entry);
	}

	/* put recv packages */
	// for each key
	for (Long entry : recv.keySet()) {
	    // if the value doesnt exist
	    if (!sorted.containsKey(recv.get(entry))) // create the value
	    {
		sorted.put(recv.get(entry), new ArrayList<String>());
	    }
	    sorted.get(recv.get(entry)).add("R" + entry);
	}

	/* put exception messages */
	for (Long entry : Debug.EXCEPTION_DUMP.keySet()) {
	    if (!sorted.containsKey(entry)) {
		sorted.put(entry, new ArrayList<String>());
	    }
	    for (String entry2 : Debug.EXCEPTION_DUMP.get(entry)) {
		sorted.get(entry).add(entry2);
	    }
	}

	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter("tmp/tmp_log_" + System.currentTimeMillis()));
	    for (Long entry : sorted.keySet()) {
		out.write(entry.toString() + ": ");
		for (String entry2 : sorted.get(entry)) {
		    out.write(entry2.toString() + " ");
		}
		out.write("\n");
	    }
	    out.close();
	} catch (IOException e) {
	}

    }

    public void send() {
	int tmp = _exiting - _sent;
	if (tmp > _timeout.getWindow()) {
	    tmp = _timeout.getWindow();
	}
	_db.send(tmp);
	_sent += tmp;
    }

    public void sendOne() {
	_db.send(1);
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
	    _exiting++;
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
	    String file = "imagem2.jpg";
	    String port = "9998";
	    
	    if (args.length >= 1) {
		if (args[0] != null) {
		    file = args[0];
		}
	    }

	    if (args.length >= 2) {
		if (args[1] != null) {
		    port = args[1];
		}
	    }
	    
	    Debug.dumpMessage("Sending file "+file);

	    Thread t = new Thread(new UDPFClientSide(file, Integer.valueOf(port)));
	    t.start();
	    t.join();
	    System.exit(0);
	} catch (SocketException ex) {
	} catch (UnknownHostException ex) {
	} catch (InterruptedException ex) {
	}
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.Observable;
import java.util.Observer;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabaseWindow extends UDPFDatabase implements Observer {
    private int _sent;
    private int _window;
    
    private boolean _time;
    private UDPFTimeout _timeout;
    
    public UDPFDatabaseWindow(int window) {
        super();
	_sent = 0;
	_window = window;
	
	_timeout = new UDPFTimeout();
	_timeout.addObserver(this);
	Thread t = new Thread(_timeout);
	t.start();
	
	_time = false;
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size())
            wait();
	while(_time)
	    wait();
	_sent++;
	_time = true;
	_timeout.waitNewTime(2000);
        return _database.get(last_index);
    }
    
    public synchronized void resetSent() {
	_sent = 0;
    }
    
    public synchronized void setWindow(int window) {
	_window = window;
    }

    @Override
    public synchronized void update(Observable o, Object o1) {
	_time = false;
	notifyAll();
    }
       
}

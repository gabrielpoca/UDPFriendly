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
    
    private boolean _wait;
    
    public UDPFDatabaseWindow() {
        super();
	_sent = 0;
	
	_wait = true;
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size())
            wait();
	
	while(_wait)
	    wait();
	
	_sent++;
	_wait = true;
        return _database.get(last_index);
    }
    
    public synchronized void resetSent() {
	_sent = 0;
    }
    
    public synchronized void sendOne() {
	_wait = false;
	notifyAll();
    }

    @Override
    public synchronized void update(Observable o, Object o1) {
	_wait = false;
	notifyAll();
    }
       
}

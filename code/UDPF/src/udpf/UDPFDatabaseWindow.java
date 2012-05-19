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
    
    private boolean _time;
    
    public UDPFDatabaseWindow() {
        super();
	_sent = 0;
	
	_time = true;
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size())
            wait();
	
	while(_time)
	    wait();
	
	_sent++;
	_time = true;
        return _database.get(last_index);
    }
    
    public synchronized void resetSent() {
	_sent = 0;
    }
    
    public synchronized void sendOne() {
	_time = false;
	notifyAll();
    }

    @Override
    public synchronized void update(Observable o, Object o1) {
	_time = false;
	notifyAll();
    }
       
}

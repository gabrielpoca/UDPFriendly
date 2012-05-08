/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gabrielpoca
 */
public class UDPFTimeout extends Observable implements Runnable {

    private int _time;
    private boolean _run;

    public UDPFTimeout() {
	_time = 0;
	_run = true;
    }

    public void run() {
	try {
	    checkNewTime();
	    while (_run) {
		Thread.sleep(_time);
		setChanged();
		notifyObservers();
		_time = 0;
		checkNewTime();
	    }
	} catch (InterruptedException ex) {
	    Logger.getLogger(UDPFTimeout.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public synchronized void waitNewTime(int time) {
	_time = time;
	notifyAll();
    }

    private synchronized void checkNewTime() throws InterruptedException {
	if (_time == 0 && _run == true) {
	    wait();
	}
    }

    public synchronized void stop() {
	_run = false;
	notifyAll();
    }
}

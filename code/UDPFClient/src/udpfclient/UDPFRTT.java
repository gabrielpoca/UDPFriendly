/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.util.ArrayList;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFRTT {

    private long _timeout;
    private int _treshdold;
    
    private float _a;
    private float _b;
    private int _window;
    public ArrayList<Long> _db;

    public UDPFRTT() {
	_db = new ArrayList<Long>();
	_a = (float) 0.125;
	_timeout = 0;
	_window = 1;
	_treshdold = 0;
    }

    public UDPFRTT(int timeout) {
	_timeout = timeout;
	_db = new ArrayList<Long>();
	_a = (float) 0.125;
    }

    public void addTime(long time) {
	_db.add(time);
	if (_timeout == 0) {
	    _timeout = time * 4;
	}
    }
    
    public void incWindow() {
	if(_window <= _treshdold)
	    _window = _window*2;
	else _window += 1;
    }

    public int getWindow() {
	return _window;
    }

    public void setWindow(int window) {
	_window = window;
    }

    public long getTimeout() {
	return _timeout;
    }

    public void setTimeout(long timeout) {
	_timeout = timeout;
    }

    public void setTreshold(int treshold) {
	_treshdold = treshold;
    }
    
    public int getTreshold() {
	return _treshdold;
    }
}

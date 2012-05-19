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

    private int _timeout;
    private float _a;
    private float _b;
    public ArrayList<Long> _db;

    public UDPFRTT() {
	_db = new ArrayList<Long>();
	_a = (float) 0.125;
	_timeout = 0;
    }

    public UDPFRTT(int timeout) {
	_timeout = timeout;
	_db = new ArrayList<Long>();
	_a = (float) 0.125;
    }

    public void addTime(long time) {
	Debug.dump("RTT:: Time:: "+time);
	if(!_db.isEmpty())
	    _timeout = (int) ((1 - _a) * _db.get(_db.size() - 1) + _a * time);
	_db.add(time);
    }

    public int getTimeout() {
	Debug.dump("RTT:: Timeout:: "+_timeout);
	return _timeout*2;
    }
}

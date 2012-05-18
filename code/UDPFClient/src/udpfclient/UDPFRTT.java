/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.util.ArrayList;

/**
 *
 * @author gabrielpoca
 */
public class UDPFRTT {

    private int _timeout;
    private float _a;
    private float _b;
    public ArrayList<Float> _db;

    public UDPFRTT() {
	_db = new ArrayList<Float>();
	_a = (float) 0.125;
	_timeout = 0;
    }

    public UDPFRTT(int timeout) {
	_timeout = timeout;
	_db = new ArrayList<Float>();
	_a = (float) 0.125;
    }

    public void addTime(float time) {
	_timeout = (int) ((1 - _a) * _db.get(_db.size()) + _a * time);
	_db.add(time);
    }

    public int getTimeout() {
	return _timeout;
    }
}

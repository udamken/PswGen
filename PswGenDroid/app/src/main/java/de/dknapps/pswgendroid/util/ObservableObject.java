package de.dknapps.pswgendroid.util;

import java.util.Observable;
import java.util.Observer;

public class ObservableObject<T> extends Observable {

    T value = null;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        setChanged();
        notifyObservers();
    }

    /**
     * Use this method instead of addObjectObserver(ObjectObserver&lt;T&gt;) for your convenience.
     */
    public synchronized <T> void addObjectObserver(ObjectObserver<T> o) {
        super.addObserver(o);
    }

}
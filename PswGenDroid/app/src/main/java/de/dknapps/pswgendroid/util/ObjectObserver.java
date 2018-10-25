package de.dknapps.pswgendroid.util;

import java.util.Observable;
import java.util.Observer;

public abstract class ObjectObserver<T> implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        onChange((ObservableObject<T>) o, (T) arg);
    }

    public abstract void onChange(ObservableObject<T> observableObject, T newValue);

}

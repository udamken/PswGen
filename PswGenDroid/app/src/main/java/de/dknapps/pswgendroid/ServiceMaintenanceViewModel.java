package de.dknapps.pswgendroid;

import java.util.Observable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class ServiceMaintenanceViewModel extends ViewModel {

    public class ObervableObject<T> extends Observable {

        T value = null;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
            setChanged();
            notifyObservers();
        }
    }

    private ObervableObject<String> currentServiceAbbreviation = new ObervableObject<>();

    public ObervableObject<String> getCurrentServiceAbbreviation() {
        return currentServiceAbbreviation;
    }

    public void setCurrentServiceAbbreviation(String serviceAbbreviation) {
        currentServiceAbbreviation.setValue(serviceAbbreviation);
    }

}

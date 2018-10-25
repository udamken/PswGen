package de.dknapps.pswgendroid;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ServiceMaintenanceViewModel extends ViewModel {

    private MutableLiveData<String> currentServiceAbbreviation = new MutableLiveData<>();

    public LiveData<String> getCurrentServiceAbbreviation() {
        return currentServiceAbbreviation;
    }

    public void setCurrentServiceAbbreviation(String serviceAbbreviation) {
        currentServiceAbbreviation.setValue(serviceAbbreviation);
    }

}

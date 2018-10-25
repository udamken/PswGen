package de.dknapps.pswgendroid;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ServiceMaintenanceViewModel extends ViewModel {

    private MutableLiveData<String> currentServiceAbbreviation = new MutableLiveData<>();

    public LiveData<String> getCurrentServiceAbbreviation() {
        return currentServiceAbbreviation;
    }

    public void setCurrentServiceAbbreviation(String serviceAbbreviation) {
        currentServiceAbbreviation.setValue(serviceAbbreviation);
    }

}

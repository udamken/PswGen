package de.dknapps.pswgendroid;

import androidx.lifecycle.ViewModel;
import de.dknapps.pswgendroid.util.ObservableObject;

public class ServiceMaintenanceViewModel extends ViewModel {

    private ObservableObject<String> currentServiceAbbreviation = new ObservableObject<>();

    public ObservableObject<String> getCurrentServiceAbbreviation() {
        return currentServiceAbbreviation;
    }

    public void setCurrentServiceAbbreviation(String serviceAbbreviation) {
        currentServiceAbbreviation.setValue(serviceAbbreviation);
    }

}

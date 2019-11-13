package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {

    //Loading of company data methods
    public void createNewCompany(String companyName);

    public void setRegionsForCompany(String companyName, List<String> regions);

    public void addCarForCompany(String companyName, CarType type);

    //Data methods
    public Set<CarType> getCarTypes(String company);

    public Set<Integer> getCarIds(String company, String type);

    public int getNumberOfReservations(String company, String type);

    public Set<String> getBestClients();

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year);

    public int getNumberOfReservationsBy(String clientName);

}

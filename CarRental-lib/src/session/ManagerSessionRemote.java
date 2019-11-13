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
    public Set<CarType> getCarTypes(String company); //(b)

    public Set<Integer> getCarIds(String company, String type); //(c)

    public int getNumberOfReservationsGivenCarId(String company, String type, int id); //(d)

    public int getNumberOfReservationsForCompanyAndType(String company, String type); //(e)

    public int getNumberOfReservationsByRenter(String clientName); //(f)

    public Set<String> getBestClients(); //(g)

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year); //(h)

}

package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @PersistenceContext
    private EntityManager entityManager;

    //Creating company methods
    @Override
    public void createNewCompany(String companyName) {
        CarRentalCompany newCompany = new CarRentalCompany(companyName);
        System.out.println("Server created new company with no cars & regions: " + companyName);
        entityManager.persist(newCompany);
    }

    @Override
    public void setRegionsForCompany(String companyName, List<String> regions) {
        CarRentalCompany company = entityManager.find(CarRentalCompany.class, companyName);
        company.setRegions(regions);
        System.out.println("Server set regions company: " + companyName + " " + regions);
    }

    @Override
    public void addCarForCompany(String companyName, CarType type) {
        CarRentalCompany company = entityManager.find(CarRentalCompany.class, companyName);
        company.addCar(type);
        System.out.println("Server added car to company:" + companyName + " " + type);
    }

    //Data methods
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<>(
                    entityManager.createNamedQuery(
                            "getCarTypesOfCompany", CarType.class)
                            .setParameter("name", company)
                            .getResultList());

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        try {
            return new HashSet<>(
                    entityManager.createNamedQuery(
                            "getCarIdsOfGivenTypeAndCompany", Integer.class)
                            .setParameter("typeName", type)
                            .setParameter("crcName", company)
                            .getResultList());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            // for (Car c : RentalStore.getRental(company).getCars(type)) {
            //    out.addAll(c.getReservations());
            //}
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public Set<String> getBestClients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfReservationsBy(String clientName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

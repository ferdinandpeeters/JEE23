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
    }

    @Override
    public void addCarForCompany(String companyName, CarType type) {
        CarRentalCompany company = entityManager.find(CarRentalCompany.class, companyName);
        company.addCar(type);
    }

    //Data methods
    @Override
    public Set<CarType> getCarTypes(String company) { //(b)
        try {
            System.err.println(" no error");

            return new HashSet<CarType>(
                    entityManager.createNamedQuery(
                            "getCarTypesOfCompany", CarType.class)
                            .setParameter("name", company)
                            .getResultList());

        } catch (IllegalArgumentException ex) {
            System.err.println("error");
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) { //(c)
        try {
            return new HashSet<>(
                    entityManager.createNamedQuery(
                            "getCarIdsOfGivenTypeAndCompany", Integer.class)
                            .setParameter("typeName", type)
                            .setParameter("companyName", company)
                            .getResultList());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type, int carId) { //(d)
        try {
           return entityManager.createNamedQuery(
                   "getNumberOfReservationsGivenCarTypeInCompany", Long.class)
                   .setParameter("carId", carId)
                   .getSingleResult()
                   .intValue();
           
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    @Override
    public int getNumberOfReservationsGivenCarId(String company, String type) { //(e)
        try {
           return entityManager.createNamedQuery(
                   "getNumberOfReservationsGivenCarId", Long.class)
                   .setParameter("crcName", company)
                   .setParameter("typeName", type)
                   .getSingleResult()
                   .intValue();
           
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservationsBy(String clientName) { //(f)
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getBestClients() { //(g)
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) { //(h)
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

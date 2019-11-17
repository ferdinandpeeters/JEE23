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
    public Set<CarType> getCarTypes(String company) { //(b) ok
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
    public int getNumberOfReservationsGivenCarId(String company, String type, int carId) { //(d) ok
        try {
            return entityManager.createNamedQuery(
                    "getNumberOfReservationsGivenCarId", Long.class)
                    .setParameter("carId", carId)
                    .getSingleResult()
                    .intValue();

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservationsForCompanyAndType(String company, String type) { //(e) OK
        try {
            return entityManager.createNamedQuery(
                    "getNumberOfReservationsGivenCarTypeInCompany", Long.class)
                    .setParameter("crcName", company)
                    .setParameter("typeName", type)
                    .getSingleResult()
                    .intValue();

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public int getNumberOfReservationsByRenter(String clientName) {
        try {
             return entityManager.createNamedQuery(
                   "getNumberOfReservationsByRenter", Long.class)
                   .setParameter("renterName", clientName)
                   .getSingleResult()
                   .intValue();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public Set<String> getBestClients() { //(g)
        try {
            System.out.println(new HashSet<>(entityManager.createNamedQuery(
                    "getBestClients", String.class).getResultList())); // TODO
            return null;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) { //(h)
        try {
             List<Object[]> result= entityManager.createNamedQuery(
                   "getMostPopularCarTypeInCompanyAndYear")
                   .setParameter("companyName", carRentalCompanyName)
                   .setParameter("year", year)
                   .setMaxResults(1).getResultList();
             String type = (String) result.get(0)[0];
             return entityManager.createNamedQuery("getTypeOfName", CarType.class).setParameter("name", type).getResultList().get(0);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }


}

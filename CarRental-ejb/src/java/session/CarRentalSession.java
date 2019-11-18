package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    private EntityManager entityManager;

    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Resource
    private EJBContext ejbContext;

    @Override
    public Set<String> getAllRentalCompanies() { //(a) ok
        return new HashSet<>(
                entityManager.createNamedQuery("getAllCompanyNames").
                        getResultList());
    }

    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return entityManager.
                createNamedQuery("getAvailableCarTypes", CarType.class)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        try {
            List<CarRentalCompany> crcs = entityManager.createNamedQuery("getAllCompanies").getResultList();

            for (CarRentalCompany crc : crcs) {
                if (crc.containsType(constraints.getCarType())
                        && crc.isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {

                    Quote q = crc.createQuote(constraints, getRenterName());
                    getQuotes().add(q);
                    return q;
                }
            }
            throw new ReservationException("No quote created!");
        } catch (Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : getQuotes()) {
                CarRentalCompany crc = entityManager.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(crc.confirmQuote(quote));
                System.out.println("Server quote confirmed!");
            }
        } catch (Exception e) {
            ejbContext.setRollbackOnly();
            throw new ReservationException(e.getMessage());
        }
        return done;
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws ReservationException { //(i)
        try {
            return entityManager.
                    createNamedQuery("getCheapestCarBetweenDatesInRegion", String.class)
                    .setParameter("region", region)
                    .setParameter("startDate", start)
                    .setParameter("endDate", end)
                    .setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            throw new ReservationException(e);
        }
    }

    //Getters & Setters
    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    public String getRenterName() {
        return renter;
    }
    
    @Override
    public List<Quote> getQuotes() {
        return quotes;
    }

}

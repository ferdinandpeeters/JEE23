package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {
    
    public void setRenterName(String name);
    
    public Set<String> getAllRentalCompanies(); //(a)
    
    public List<CarType> getAvailableCarTypes(Date start, Date end);
    
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException;
    
    public List<Quote> getQuotes();
    
    public List<Reservation> confirmQuotes() throws ReservationException;

    public String getCheapestCarType(Date start, Date end, String region) throws ReservationException; //(i)

    
}
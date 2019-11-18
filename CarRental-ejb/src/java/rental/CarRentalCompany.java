package rental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@NamedQueries({
    @NamedQuery(name = "getAllCompanyNames", query
            = "SELECT crc.name FROM CarRentalCompany crc") //(a) OK
    ,
    
     @NamedQuery(name = "getAllCompanies", query
            = "SELECT crc FROM CarRentalCompany crc")
    , 
    
    @NamedQuery(name = "getAvailableCarTypes", query
            = "SELECT DISTINCT car.type FROM Car car "
            + "WHERE car.id NOT IN "
            + "("
            + "SELECT res.carId FROM Reservation res "
            + "WHERE res.startDate <= :endDate AND res.endDate >= :startDate"
            + ")")
    ,
    
    @NamedQuery(name = "getCheapestCarBetweenDatesInRegion", query
            = "SELECT DISTINCT car.type.name "
            + "FROM CarRentalCompany crc, IN (crc.cars) car, IN (crc.regions) region "
            + "WHERE region = :region AND car.id NOT IN "
            + "("
            + "SELECT res.carId FROM Reservation res "
            + "WHERE res.startDate <= :endDate AND res.endDate >= :startDate"
            + ") "
            + "ORDER BY car.type.rentalPricePerDay ASC") //(i) ok
    ,

    @NamedQuery(name = "getCarTypesOfCompany", query
            = "SELECT DISTINCT crc.carTypes "
            + "FROM CarRentalCompany crc "
            + "WHERE crc.name = :name") //(b) OK
    ,
    
    @NamedQuery(name = "getCarIdsOfGivenTypeAndCompany", query
            = "SELECT DISTINCT car.id "
            + "FROM CarRentalCompany crc, IN (crc.cars) car "
            + "WHERE crc.name = :companyName AND car.type.name = :typeName") //(c) ok
    ,
    
    @NamedQuery(name = "getNumberOfReservationsGivenCarId", query
            = "SELECT COUNT (DISTINCT r.id) "
            + "FROM Reservation r "
            + "WHERE r.carId = :carId") //(d) ok
    ,
        
    @NamedQuery(name = "getNumberOfReservationsGivenCarTypeInCompany", query
            = "SELECT COUNT (car.reservations) "
            + "FROM CarRentalCompany crc, IN (crc.cars) car "
            + "WHERE crc.name = :crcName AND car.type.name = :typeName")// (e) ok
    , 
        
    @NamedQuery(name = "getNumberOfReservationsByRenter", query
            = "SELECT COUNT (DISTINCT res.id) "
            + "FROM Reservation res "
            + "WHERE res.carRenter = :renterName ") //(f) ok
    ,
    
    @NamedQuery(name = "getMostPopularCarTypeInCompanyAndYear", query
            = "SELECT res.carType, COUNT(DISTINCT res.id) AS num  "
            + "FROM Reservation res "
            + "WHERE   res.rentalCompany = :companyName "
            + "AND ( EXTRACT(YEAR FROM res.startDate)= :year  OR  EXTRACT(YEAR FROM res.endDate)=:year ) "
            + "GROUP BY   res.carType "
            + "ORDER BY   num DESC " //(h) ok
    )
    ,
    
    @NamedQuery(name = "getTypeOfName", query
            = "SELECT t FROM CarType t WHERE t.name = :name")
})

//We used a Native query since a NamedQuery only accepts statements in the FROM clause. 
//We needed a subquery in the FROM clause.
@NamedNativeQueries({
    @NamedNativeQuery(name = "getBestClients", query
            = "SELECT m.renter  "
            + "FROM ( "
            + "    SELECT r.carRenter AS renter, COUNT(DISTINCT r.id) AS amt "
            + "    FROM Reservation r "
            + "    GROUP BY r.carRenter "
            + "    ) as m  "
            + "WHERE m.amt = "
            + "    ( SELECT max(c.amt) "
            + "    FROM ( "
            + "        SELECT COUNT(DISTINCT res.id) AS amt  "
            + "        FROM Reservation res "
            + "        GROUP BY res.carRenter "
            + "        ) as c" //(g) ok
            + "    )")
})

@Entity
public class CarRentalCompany {

    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

    @Id
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Car> cars;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Set<CarType> carTypes = new HashSet<CarType>();

    @ElementCollection
    private List<String> regions;

    /**
     * *************
     * CONSTRUCTORS* ***********
     */
    public CarRentalCompany() {
        //no argument constructor used for enty class creation
    }

    public CarRentalCompany(String companyName) {
        this(companyName, new ArrayList<String>(), new ArrayList<Car>());
    }

    public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
        logger.log(Level.INFO, "<{0}> Starting up CRC {0} ...", name);
        setName(name);
        this.cars = cars;
        setRegions(regions);
        for (Car car : cars) {
            carTypes.add(car.getType());
        }
    }

    /**
     * ******
     * NAME * ****
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * *********
     * Regions * ******
     */
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getRegions() {
        return this.regions;
    }

    /**
     * ***********
     * CAR TYPES * **********
     */
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        System.out.println("ALL TYPES!! = " + carTypes);
        System.out.println("CHECKED !!  = " + carTypeName);
        for (CarType type : carTypes) {
            if (type.getName().equals(carTypeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1} on " + getName(), new Object[]{name, carTypeName});
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    public boolean containsType(String typeName) {
        for (CarType type : carTypes) {
            if (type.getName().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     *********
     * CARS * *******
     */
    public void addCar(CarType type) {
        cars.add(new Car(type));
        carTypes.add(type);
    }

    public Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars(CarType type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }

    public Set<Car> getCars(String type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /**
     * **************
     * RESERVATIONS * *************
     */
    public Quote createQuote(ReservationConstraints constraints, String guest)
            throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, guest, constraints.toString()});

        if (!this.regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }

        CarType type = getType(constraints.getCarType());

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }

    public Set<Reservation> getReservationsBy(String renter) {
        logger.log(Level.INFO, "<{0}> Retrieving reservations by {1}", new Object[]{name, renter});
        Set<Reservation> out = new HashSet<Reservation>();
        for (Car c : cars) {
            for (Reservation r : c.getReservations()) {
                if (r.getCarRenter().equals(renter)) {
                    out.add(r);
                }
            }
        }
        return out;
    }

}

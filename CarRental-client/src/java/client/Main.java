package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main("trips");
        ManagerSessionRemote managerSession = main.getNewManagerSession("manager");
        loadData("dockx.csv", managerSession);
        loadData("hertz.csv", managerSession);

        /*CarRentalSessionRemote carRentalSession = main.getNewReservationSession("Steven");
        carRentalSession.createQuote(new ReservationConstraints(new Date(100, 1, 1), new Date(100, 2, 1), "Economy", "Brussels"));
        carRentalSession.createQuote(new ReservationConstraints(new Date(100, 1, 1), new Date(100, 2, 1), "MPV", "Brussels"));
        List<Reservation> l = carRentalSession.confirmQuotes();

        CarRentalSessionRemote carRentalSession2 = main.getNewReservationSession("Dries");
        carRentalSession2.createQuote(new ReservationConstraints(new Date(100, 1, 1), new Date(100, 2, 1), "Mini", "Brussels"));
        List<Reservation> l2 = carRentalSession2.confirmQuotes();

        CarRentalSessionRemote carRentalSession3 = main.getNewReservationSession("Driesjex3");
        carRentalSession3.createQuote(new ReservationConstraints(new Date(100, 10, 1), new Date(100, 11, 1), "Mini", "Brussels"));
        List<Reservation> l3 = carRentalSession3.confirmQuotes();

        System.out.println(managerSession.getBestClients());*/
        main.run();
    }

    //Session methods
    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        CarRentalSessionRemote session = (CarRentalSessionRemote) (new InitialContext()).lookup(CarRentalSessionRemote.class.getName());
        session.setRenterName(name);
        return session;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        return (ManagerSessionRemote) (new InitialContext()).lookup(ManagerSessionRemote.class.getName());
    }

    //Data methods
    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapestCarType(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected void getAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("CLIENT getAvailableCarTypes");
        for (CarType ct : session.getAvailableCarTypes(start, end)) {
            //System.out.println(ct);
        }
    }

    @Override
    protected void createQuote(CarRentalSessionRemote session, String clientName, Date start, Date end, String carType, String region) throws Exception {
        session.createQuote(new ReservationConstraints(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsByRenter(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCompanyAndType(carRentalName, carType);
    }

    //Loading csv data method
    public static void loadData(String datafile, ManagerSessionRemote managerSession)
            throws NumberFormatException, IOException {

        StringTokenizer csvReader;
        String companyName = null;

        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(datafile)));

        try {
            while (in.ready()) {
                String line = in.readLine();

                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");

                    companyName = csvReader.nextToken();
                    managerSession.createNewCompany(companyName);
                    managerSession.setRegionsForCompany(companyName, Arrays.asList(csvReader.nextToken().split(":")));

                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        managerSession.addCarForCompany(companyName, type);
                    }
                }
            }
        } finally {
            in.close();
        }
    }

}

package rental;

import java.util.List;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rental.Car;
import rental.CarType;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-11-13T17:45:15")
@StaticMetamodel(CarRentalCompany.class)
public class CarRentalCompany_ { 

    public static volatile ListAttribute<CarRentalCompany, Car> cars;
    public static volatile SingularAttribute<CarRentalCompany, List> regions;
    public static volatile SetAttribute<CarRentalCompany, CarType> carTypes;
    public static volatile SingularAttribute<CarRentalCompany, String> name;

}
package drivers;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

public interface TripRepository extends CrudRepository<Trip, Long> {

	Collection<Trip> findByDriver(Driver driver);

	Collection<Trip> findByDriverId(long id);

}

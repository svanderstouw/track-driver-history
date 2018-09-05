package drivers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface DriverRepository extends CrudRepository<Driver, Long> {

	Optional<Driver> findByDriverName(String name);
	
	default Collection<Driver> findAndCustomSort() {
		ArrayList<Driver> allDrivers = new ArrayList<Driver>((Collection<Driver>) this.findAll());
		Collections.sort(allDrivers, (d1, d2) -> Double.compare(d2.getAccumulatedDistance(), d1.getAccumulatedDistance()));
		
		return allDrivers;
	}

}

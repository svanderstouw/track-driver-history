package drivers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class DriverTests {
	
	@Resource
	private TestEntityManager entityManager;
	
	@Resource
	private DriverRepository driverRepo;
	
	@Resource
	private TripRepository tripRepo;
	
	@Test
	public void shouldSaveAndLoadDriver() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getDriverName(), is("driver1"));
	}
	
	@Test
	public void shouldGenerateDriverId() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		entityManager.flush();
		entityManager.clear();
		
		assertThat(driverId, is(greaterThan(0L)));
	}
	
	@Test
	public void shouldFindDriverByName() {
		Driver driver1 = driverRepo.save(new Driver("Sam"));
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findByDriverName("Sam");
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getDriverName(), is("Sam"));
	}

	@Test
	public void shouldSaveAndLoadTrip() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		long tripId = trip1.getId();
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Trip> result = tripRepo.findById(tripId);
		Trip resultingTrip = result.get();
		assertThat(resultingTrip.getDuration(), is(Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")).getSeconds()));
		assertThat(resultingTrip.getDistance(), is(100.0));
	}
	
	@Test
	public void shouldEstablishTripToDriverRelationship() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getTrips(), containsInAnyOrder(trip2, trip1));
	}
	
	@Test
	public void shouldFindTripsForDriver() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		Driver driver2 = driverRepo.save(new Driver("driver2"));
		
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver2, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		
		entityManager.flush();
		entityManager.clear();
		
		Collection<Trip> tripsForDriver = tripRepo.findByDriver(driver1);
		assertThat(tripsForDriver, containsInAnyOrder(trip1, trip3));
	}
	
	@Test
	public void shouldFindTripsForDriverId() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		Driver driver2 = driverRepo.save(new Driver("driver2"));
		
		Trip trip1 = tripRepo.save(new Trip(driver2, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		
		entityManager.flush();
		entityManager.clear();
		
		Collection<Trip> tripsForDriver = tripRepo.findByDriverId(driverId);
		assertThat(tripsForDriver, containsInAnyOrder(trip3, trip2));
	}
	
	@Test
	public void driverShouldAccumulateDistanceFromTrips() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getAccumulatedDistance(), is(285.0));
	}
	
	@Test
	public void driverShouldAccumulateNoDistanceFromNoTrips() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		Driver driver2 = driverRepo.save(new Driver("driver2"));
		
		Trip trip1 = tripRepo.save(new Trip(driver2, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver2, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver2, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getAccumulatedDistance(), is(0.0));
	}
	
	@Test
	public void driverShouldAccumulateDurationFromTrips() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		long addedDuration = trip1.getDuration() + trip2.getDuration() + trip3.getDuration();
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getAccumulatedDuration(), is(addedDuration));
	}
	
	@Test
	public void driverShouldCalculateAverageSpeed() {
		Driver driver1 = driverRepo.save(new Driver("driver1"));
		long driverId = driver1.getId();
		
		Trip trip1 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("06:10"), LocalTime.parse("08:20")), 100));
		Trip trip2 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("09:15"), LocalTime.parse("11:35")), 120));
		Trip trip3 = tripRepo.save(new Trip(driver1, Duration.between(LocalTime.parse("12:08"), LocalTime.parse("13:23")), 65));
		double averageSpeed = 285 / (20700 / 60.0 / 60.0);
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Driver> result = driverRepo.findById(driverId);
		Driver resultingDriver = result.get();
		assertThat(resultingDriver.getAverageSpeed(), is(averageSpeed));
	}
	
}

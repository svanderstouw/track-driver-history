package drivers;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Driver {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String driverName;
	private double accumulatedDistance;
	private long accumulatedDuration;
	private double averageSpeed;
	
	@OneToMany(mappedBy = "driver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Collection<Trip> trips;
	
	public long getId() {
		return id;
	}
	
	public String getDriverName() {
		return driverName;
	}
	
	public double getAccumulatedDistance() {
		accumulatedDistance = 0;
		if (trips.size() != 0) {
			for (Trip trip : trips) {
				accumulatedDistance += trip.getDistance();
			}
		}
		return accumulatedDistance;
	}
	
	public long getAccumulatedDuration() {
		accumulatedDuration = 0;
		if (trips.size() != 0) {
			for (Trip trip : trips) {
				accumulatedDuration += trip.getDuration();
			}
		}
		return accumulatedDuration;
	}
	
	public double getAverageSpeed() {
		averageSpeed = 0;
		if (getAccumulatedDistance() > 0) {
			averageSpeed = getAccumulatedDistance() / (getAccumulatedDuration() / 60.0 / 60.0);
		}
		return averageSpeed;
	}
	
	public Collection<Trip> getTrips() {
		return trips;
	}
	
	protected Driver() {}

	public Driver(String driverName) {
		this.driverName = driverName;
	}
	
	@Override
	public String toString() {
		int roundedDistance = (int) Math.round(getAccumulatedDistance());
		if (roundedDistance > 0) {
			int roundedSpeed = (int) Math.round(getAverageSpeed());
			return driverName + ": " + roundedDistance + " miles @ " + roundedSpeed + " mph";
		} else {
			return driverName + ": " + roundedDistance + " miles";
		}
	}
	
}

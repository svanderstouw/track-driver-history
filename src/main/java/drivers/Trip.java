package drivers;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Trip {
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	private Driver driver;
	
	private Duration duration;
	private double distance;
	
	public long getId() {
		return id;
	}
	
	public String getDriverName() {
		return driver.getDriverName();
	}
	
	public long getDuration() {
		return duration.getSeconds();
	}
	
	public double getDistance() {
		return distance;
	}
	
	protected Trip () {}

	public Trip(Driver driver, Duration duration, double distance) {
		this.driver = driver;
		this.duration = duration;
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trip other = (Trip) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}

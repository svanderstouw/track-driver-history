package drivers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InputOutput implements CommandLineRunner {

	@Resource
	private DriverRepository driverRepo;

	@Resource
	private TripRepository tripRepo;

	@Override
	public void run(String... args) {

		Scanner input = new Scanner(System.in);

		System.out.println();
		System.out.println("Enter path and file name:");
		System.out.print("> ");
		String fileName = input.nextLine();
		
		processFile(fileName);
		generateReport(fileName);
		
		input.close();
	}
	
	public void processFile(String fileName) {
		
		final double MIN_SPEED = 5.0;
		final double MAX_SPEED = 100.0;
		
		Stream<String> lines = null;
		try {
			lines = Files.lines(Paths.get(fileName));
		} catch (IOException e) {
			System.out.println("Unable to read file");;
		}
		
		lines.forEach(line -> {
			String[] commandArray = line.split(" ");
			
			if (commandArray[0].equals("Driver")) {
				Driver driver = driverRepo.save(new Driver(commandArray[1]));
				
			} else if (commandArray[0].equals("Trip")) {
				Optional<Driver> driverOptional = driverRepo.findByDriverName(commandArray[1]);
				
				if (driverOptional.isPresent()) {
					Driver resultingDriver = driverOptional.get();
					
					double tripDistance = Double.valueOf(commandArray[4]);
					Duration tripDuration = Duration.between(LocalTime.parse(commandArray[2]), LocalTime.parse(commandArray[3]));
					double tripSpeed = 0;
					
					if (tripDistance > 0) {
						tripSpeed = tripDistance / (tripDuration.getSeconds() / 60.0 / 60.0);
					}
					
					if (tripSpeed >= MIN_SPEED && tripSpeed <= MAX_SPEED) {
						Trip trip = tripRepo.save(new Trip(resultingDriver, tripDuration, tripDistance));
					}
				}
			}
		});
		
		lines.close();
		
	}
	
	private void generateReport(String fileName) {
		
		Path path = Paths.get(fileName);
		Path root = path.getRoot();
		String directory;
		
		if (root != null) {
			directory = root.toString() + path.subpath(0, path.getNameCount()-1).toString() + File.separator;
		} else {
			directory = path.subpath(0, path.getNameCount()-1).toString() + File.separator;
		}

		try {
			FileWriter fileWriter = new FileWriter(directory + "report.txt");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			Collection<Driver> drivers = driverRepo.findAndCustomSort();
			for (Driver driver : drivers) {
				printWriter.println(driver.toString());
			}
			
			printWriter.close();
			
			System.out.println();
			System.out.println("Report written to:");
			System.out.println(directory + "report.txt");
			
		} catch (IOException e1) {
			System.out.println("Unable to write file");;
		}
	}	
}

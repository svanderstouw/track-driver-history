# track-driver-history

Track driving history for people

### Database Design ###

I decided to use Spring JPA to create my database relationships in Java.  This way the project could easily be expanded to support a web application and AJAX requests with the addition of a few more dependencies from the Spring Framework.

I created two main classes - `Driver` and `Trip`.  The `Trip` entity has instance variables for an ID number, duration of the trip, and trip distance.  It also has a Many-To-One relationship with `Driver`.

The `Driver` class has instance variables for an ID number and the Driver Name, and a One-To-Many relationship with the `Trip` entity.  In addition the `Driver` entity has getters that calculate the accumulated distance, accumulated duration, and average speed based on the associated trips.  It also has an overridden `toString` method which generates each driver's line for the final report including rounding the distance and speed.

Both classes have associated respositories that are interfaces which extend the CRUD Repository.  The `DriverRepository` has a custom sort which orders the collection of drivers based on their trips' accumulated distances.

All entities, repositories, and relationships were created based on tests in the `DriverTests` class.  This class also has tests that drove the development of the calculations within the `Driver` class.

### Input and Output ###

The `DriverApplication` class starts the main String Application, but all interactions with the user and external files are managed in the `InputOutput` component.

After receiving the path and file name through a standard console input, the `InputOutput` class processes the file by loading it to a Stream and iterating through the file line-by-line.  Driver command lines are saved as new drivers to the `DriverRepository`.  Trip command lines are tested to determine if their speed exceeds the minimum and maximum constants.  If their speeds are within bounds, the trip is then saved as a new trip to the `TripRepository`.

To generate the final report, a collection of all drivers is gathered and sorted.  A file is then written using each driver's `toString` method. The resulting text file is saved to the same directory as the original input file.
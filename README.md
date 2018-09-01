# validity

1. I'm using Spring Batch to read the input csv file and write the Person object into H2 memory database
2. The PersonService and PersonRepository will retrieve all of the people from the H2 memory database
3. The PersonController and three different html pages will display All The People / Potential People / Non Dup People

To run the project:

a. Simply run "gradle bootRun"
b. Run "gradle build" and execute the jar file "java -jar build/libs/validity-0.0.1-SNAPSHOT.jar"

After the application starts running:

a. Go to http://localhost:8080/people to list all the people
b. Go to http://localhost:8080/duppeople to list all the potential people
c. Go to http://localhost:8080/nonduppeople to list all the non duplicate people
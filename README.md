# JPA Learning System

This repository contains a study project that demonstrates the use of the Java Persistence API (JPA) to perform CRUD (Create, Read, Update, Delete) operations.

The project is structured using the Person class and the PersonService service to manipulate Person objects. The main program (Program) creates a list of Person objects, establishes a connection to the database, and provides an interactive menu for the user to execute or get details about various JPA methods.

## Features

- Demonstrates the use of JPA for database connectivity.
- Implements CRUD operations.
- Provides an interactive menu to explore various JPA methods.

## How to use

To clone and run this application, you'll need Git, Java, and Maven installed on your computer. From your command line:

```bash
# Clone this repository
$ git clone https://github.com/gamtcode/JPA-Learning-System

# Go into the repository
$ cd JPA-Learning-System

# Compile the project
$ mvn compile

# Run the project
$ mvn exec:java -Dexec.mainClass="application.Program"
```

## Dependencies
This project depends on the following:

- Java 17 or higher
- Maven
- Hibernate
- MySQL

## Authorship
This project was created and is maintained by [Guilherme Teixeira](https://github.com/gamtcode/).

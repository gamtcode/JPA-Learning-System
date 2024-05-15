package application;

import domain.Person;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import services.PersonService;
import util.PersonMessages;

import javax.persistence.*;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Program {

    public static void main(String[] args) {

        List<Person> people = createPeople();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-jpa");
        EntityManager em = emf.createEntityManager();
        PersonService personService = new PersonService(em);

        System.out.println("Welcome to our learning system! This is a program dedicated to exploring and understanding the Java Persistence API (JPA). It provides a variety of methods, each with a clear and concise explanation of its function. This resource is useful for anyone wishing to deepen their knowledge in JPA, whether you are a beginner or someone with prior experience. Let's get started.");

        System.out.println("\nThese are the instantiated Person objects:");
        for (Person p : people) {
            System.out.println(p);
        }

        Scanner scanner = new Scanner(System.in);
        int option = 0;
        String choice;

        do {
            System.out.println("\nPlease choose a method to execute or get more details:");
            System.out.println("1. persist                | 8. clear                 | 15. createNativeQuery v1 | 22. updatePersonObj");
            System.out.println("2. merge                  | 9. detach                | 16. createNativeQuery v2 | 23. modifyPersonId");
            System.out.println("3. remove                 | 10. isOpen               | 17. createNativeQuery v3 | 24. Display Objects");
            System.out.println("4. find                   | 11. getMetamodel         | 18. refresh              | 25. Descriptive Menu");
            System.out.println("5. getReference           | 12. lock                 | 19. contains             | 26. Exit the program");
            System.out.println("6. flush                  | 13. createQuery          | 20. close");
            System.out.println("7. isActive               | 14. createNamedQuery     | 21. updatePersonDb");

            System.out.print("\nEnter your choice: ");
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.\n");
                scanner.next(); // to consume the invalid token
                continue; // skip the rest of the loop and start over
            }
            System.out.println();

            switch (option) {
                case 1:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Do you want to persist all persons? (Y/N) ");
                        if (scanner.next().equalsIgnoreCase("Y")) {
                            System.out.println();
                            for (Person p : people) {
                                personService.persist(p);
                                System.out.println("Person " + p.getName() + " persisted with auto-generated ID " + p.getId() + ".");
                            }
                        } else {
                            System.out.print("Enter the first name of the person you want to persist: ");
                            String firstName = scanner.next();
                            boolean found = false;
                            System.out.println();
                            for (Person p : people) {
                                String[] nameParts = p.getName().split(" ");
                                if (nameParts[0].equalsIgnoreCase(firstName)) {
                                    personService.persist(p);
                                    System.out.println("Person " + p.getName() + " persisted with auto-generated ID " + p.getId() + ".");
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                System.out.println("Invalid first name.");
                            }
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.PERSIST_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 2:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Do you want to merge changes for all persons? (Y/N) ");
                        if (scanner.next().equalsIgnoreCase("Y")) {
                            for (int i = 1; i <= people.size(); i++) {
                                personService.merge(people, i);
                            }
                        } else {
                            System.out.print("Enter id to execute: ");
                            while (!scanner.hasNextInt()) {
                                System.out.print("That's not a number! Please enter a valid ID: ");
                                scanner.next();
                            }
                            int id = scanner.nextInt();
                            personService.merge(people, id);
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.MERGE_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 3:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter id to execute: ");
                        try {
                            personService.remove(scanner.nextInt());
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input. Please enter a number.\n");
                            scanner.next(); // to consume the invalid token
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.REMOVE_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 4:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter id to execute: ");
                        try {
                            int id = scanner.nextInt();
                            Person p = personService.find(id);
                            if (p != null) {
                                System.out.println(p);
                            } else {
                                System.out.println(PersonMessages.PERSON_NOT_FOUND_MSG);
                            }
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input. Please enter a number.\n");
                            scanner.next();
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.FIND_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 5:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter id to execute: ");
                        try {
                            int id = scanner.nextInt();
                            Person p = personService.getReference(id);
                            try {
                                System.out.println(p);
                            } catch (EntityNotFoundException e) {
                                System.err.println("No person found with the given ID.\n");
                            }
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input. Please enter a number.\n");
                            scanner.next(); // to consume the invalid token
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.GET_REFERENCE_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 6:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        personService.flush();
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.FLUSH_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 7:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        boolean isActive = personService.isTransactionActive();
                        System.out.println("Transaction is active: " + isActive);
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.IS_ACTIVE_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 8:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        personService.clear();
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CLEAR_MSG);
                    }
                    break;

                case 9:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter id to execute: ");
                        try {
                            personService.detach(scanner.nextInt());
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input. Please enter a number.\n");
                            scanner.next(); // to consume the invalid token
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.DETACH_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 10:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        boolean isOpen = personService.isEntityManagerOpen();
                        System.out.println("EntityManager is open: " + isOpen);
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.IS_ENTITY_MANAGER_OPEN_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 11:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter the ID of the person: ");
                        try {
                            int personId = scanner.nextInt();
                            Person person = personService.find(personId);
                            if (person != null) {
                                personService.printPersonAttributes(person);
                            } else {
                                System.out.println("Person with ID " + personId + " not found.");
                            }
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input. Please enter a number.\n");
                            scanner.next(); // to consume the invalid token
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.GET_METAMODEL_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 12:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter the person ID: ");
                        try {
                            int id = scanner.nextInt();
                            Person person = personService.find(id);
                            if (person != null) {
                                System.out.print("Enter the lock type (OPTIMISTIC, PESSIMISTIC_WRITE): ");
                                String lockTypeInput = scanner.next();
                                try {
                                    LockModeType lockType = LockModeType.valueOf(lockTypeInput.toUpperCase());
                                    personService.lock(person, lockType);
                                    System.out.println("Lock successfully applied to person with ID " + id + ". Note that this is an illustrative example. In a real scenario, you would typically perform some operations on the entity within the transaction after applying the lock.");
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Invalid lock type provided: " + e.getMessage());
                                } catch (PersistenceException e) {
                                    System.err.println("An error occurred while trying to lock the person: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Person with ID " + id + " not found.");
                            }
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid ID provided.\n");
                            scanner.next(); // to consume the invalid token
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.LOCK_MSG);
                    }
                    break;

                case 13:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter your JPQL query: ");
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        String jpqlQuery = scanner.nextLine();
                        System.out.println();
                        try {
                            Query query = personService.createQuery(jpqlQuery);
                            if (query != null) {
                                List<?> resultList = query.getResultList();
                                for (Object obj : resultList) {
                                    System.out.println(obj);
                                }
                            }
                        } catch (QuerySyntaxException e) {
                            System.err.println("Invalid JPQL query: " + e.getMessage() + "\n");
                        } catch (Exception e) {
                            System.err.println("An error occurred while executing the query: " + e.getMessage() + "\n");
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CREATE_QUERY_MSG);
                    }
                    break;

                case 14:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        scanner.nextLine();
                        System.out.print("Please enter the name of the Person you want to find: ");
                        String name = scanner.nextLine();
                        List<Person> foundPersons = personService.findPersonByName(name);
                        if (foundPersons != null && !foundPersons.isEmpty()) {
                            System.out.println("Found persons: ");
                            for (Person person : foundPersons) {
                                System.out.println(person);
                            }
                        } else {
                            System.out.println("No person found with name " + name + ".");
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CREATE_NAMED_QUERY_MSG);
                    }
                    break;

                case 15:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter your SQL query: ");
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        String sqlQuery = scanner.nextLine();
                        try {
                            Query query = personService.createNativeQuery(sqlQuery);
                            if (query != null) {
                                List<?> resultList = query.getResultList();
                                for (Object result : resultList) {
                                    if (result instanceof Object[] resultArray) {
                                        Integer id = (Integer) resultArray[0];
                                        String name = (String) resultArray[2];
                                        String email = (String) resultArray[1];
                                        System.out.println("Id: " + id + ", Name: " + name + ", Email: " + email);
                                    }
                                }
                            }
                        } catch (GenericJDBCException e) {
                            System.err.println("Invalid SQL query.\n");
                        } catch (Exception e) {
                            System.err.println("An error occurred while executing the query: " + e.getMessage() + "\n");
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CREATE_NATIVE_QUERY_MSG);
                    }
                    break;

                case 16:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter your SQL query: ");
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        String sqlQuery = scanner.nextLine();
                        System.out.print("Enter the result class: ");
                        String resultClass = scanner.nextLine();
                        try {
                            Query query = personService.createNativeQuery(sqlQuery, Class.forName(resultClass));
                            if (query != null) {
                                List<?> resultList = query.getResultList();
                                for (Object obj : resultList) {
                                    System.out.println(obj);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("An error occurred while executing the query: " + e.getMessage() + "\n");
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CREATE_NATIVE_QUERY_WITH_CLASS_MSG);
                        scanner.nextLine();
                    }
                    break;

                case 17:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Enter your SQL query: ");
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        String sqlQuery = scanner.nextLine();
                        System.out.print("Enter the result set mapping: ");
                        String resultSetMapping = scanner.nextLine();
                        try {
                            Query query = personService.createNativeQuery(sqlQuery, resultSetMapping);
                            if (query != null) {
                                List<?> resultList = query.getResultList();
                                for (Object obj : resultList) {
                                    System.out.println(obj);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("An error occurred while executing the query: " + e.getMessage() + "\n");
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CREATE_NATIVE_QUERY_WITH_MAPPING_MSG);
                    }
                    break;

                case 18:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Do you want to merge changes for all persons? (Y/N) ");
                        if (scanner.next().equalsIgnoreCase("Y")) {
                            for (Person p : people) {
                                if (!em.contains(p)) {
                                    em.merge(p);

                                }
                                personService.refresh(p);
                            }
                        } else {
                            System.out.print("Enter the first name of the person you want to refresh: ");
                            String firstName = scanner.next();
                            for (Person p : people) {
                                String[] nameParts = p.getName().split(" ");
                                if (nameParts[0].equalsIgnoreCase(firstName) && em.contains(p)) {
                                    personService.refresh(p);
                                }
                            }
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.REFRESH_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 19:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        System.out.print("Please enter the ID of the Person you want to check: ");
                        if (scanner.hasNextInt()) {
                            int id = scanner.nextInt();
                            scanner.nextLine();
                            Person person = em.find(Person.class, id);
                            if (person == null) {
                                System.out.println("No Person found with ID " + id + ".");
                            } else {
                                if (personService.contains(person)) {
                                    System.out.println("The Person with ID " + id + " is being managed by the EntityManager.");
                                } else {
                                    System.out.println("The Person with ID " + id + " is not being managed by the EntityManager.");
                                }
                            }
                        } else {
                            System.out.println("Invalid input. Please enter a valid ID.");
                            scanner.next(); // discard the invalid input
                        }
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CONTAINS_MSG);
                    }
                    break;

                case 20:
                    choice = personService.getUserChoice(scanner);
                    if (choice.equalsIgnoreCase("E")) {
                        personService.closeEntityManager();
                        System.out.println("EntityManager has been closed.");
                    } else if (choice.equalsIgnoreCase("D")) {
                        System.out.println(PersonMessages.CLOSE_ENTITY_MANAGER_MSG);
                    }
                    scanner.nextLine();
                    break;

                case 21:
                    System.out.print("Enter the ID of the Person to be updated: ");
                    if (scanner.hasNextInt()) {
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter the attribute to be updated (name or email): ");
                        String attribute = scanner.next();
                        scanner.nextLine();
                        if (!attribute.equalsIgnoreCase("name") && !attribute.equalsIgnoreCase("email")) {
                            System.out.println("Invalid attribute. Only 'name' and 'email' can be updated.");
                            break;
                        }
                        System.out.print("Enter the new value for " + attribute + ": ");
                        String newValue = scanner.nextLine();
                        String result = personService.updatePersonDb(id, attribute, newValue);
                        System.out.println(result);
                    } else {
                        System.out.println("Invalid input. Please enter a valid ID.");
                        scanner.next(); // discard the invalid input
                    }
                    break;

                case 22:
                    System.out.print("Do you want to search the Person by ID or full name (ID/FN)? ");
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    String searchBy = scanner.nextLine();
                    Integer idInteger = null;
                    String name = null;
                    if (searchBy.equalsIgnoreCase("ID")) {
                        System.out.print("Enter the ID of the Person to be updated in memory: ");
                        if (scanner.hasNextInt()) {
                            idInteger = scanner.nextInt();
                            scanner.nextLine();
                        } else {
                            System.out.println("Invalid input. Please enter a valid ID.");
                            scanner.next(); // discard the invalid input
                            break;
                        }
                    } else if (searchBy.equalsIgnoreCase("FN")) {
                        System.out.print("Enter the full name of the Person to be updated in memory: ");
                        name = scanner.nextLine();
                    } else {
                        System.out.println("Invalid input. You can only search by 'ID' or 'name'.");
                        break;
                    }
                    System.out.print("Enter the attribute to be updated in memory (name or email): ");
                    String attribute = scanner.next();
                    scanner.nextLine();
                    if (!attribute.equalsIgnoreCase("name") && !attribute.equalsIgnoreCase("email")) {
                        System.out.println("Invalid attribute. Only 'name' and 'email' can be updated in memory.");
                        break;
                    }
                    System.out.print("Enter the new value for " + attribute + " in memory: ");
                    String newValue = scanner.nextLine();
                    String result = personService.updatePersonObj(people, idInteger, name, attribute, newValue);
                    System.out.println(result);
                    break;

                case 23:
                    System.out.print("Which ID do you want to modify? ");
                    if (scanner.hasNextInt()) {
                        int id = scanner.nextInt();
                        System.out.print("Do you want to set the ID to null or enter a new ID? Enter 'null' or 'new': ");
                        String response = scanner.next();
                        if (response.equalsIgnoreCase("new")) {
                            System.out.print("Enter the new ID: ");
                            if (scanner.hasNextInt()) {
                                int newId = scanner.nextInt();
                                personService.modifyPersonId(people, id, response, newId);
                            } else {
                                System.out.println("Invalid input. Please enter a valid ID.");
                                scanner.next(); // discard the invalid input
                            }
                        } else {
                            personService.modifyPersonId(people, id, response, null);
                        }
                        scanner.nextLine();
                    } else {
                        System.out.println("Invalid input. Please enter a valid ID.");
                        scanner.next(); // discard the invalid input
                    }
                    break;

                case 24:
                    for (Person p : people) {
                        System.out.println(p);
                    }
                    scanner.nextLine();
                    break;

                case 25:
                    System.out.println("1. persist - Makes the passed entity instance managed and persistent.");
                    System.out.println("2. merge - Merges the changes of a detached entity back into the persistence context.");
                    System.out.println("3. remove - Removes the entity instance from the persistence context, causing the removal from the database.");
                    System.out.println("4. find - Finds an entity by its class type and primary key.");
                    System.out.println("5. getReference - Gets a reference to the entity without retrieving its data until needed (lazy loading).");
                    System.out.println("6. flush - Synchronizes the persistence context with the underlying database, writing any changes.");
                    System.out.println("7. isActive - Checks if the current transaction associated with the EntityManager is active.");
                    System.out.println("8. clear - Clears the persistence context, detaching all entities.");
                    System.out.println("9. detach - Detaches the entity from the persistence context.");
                    System.out.println("10. isOpen - Checks if the EntityManager is open.");
                    System.out.println("11. getMetamodel - Returns the metamodel that corresponds to the persistence context.");
                    System.out.println("12. lock - Locks the entity with the provided lock type.");
                    System.out.println("13. createQuery - Creates a new instance of Query for the provided JPQL query.");
                    System.out.println("14. createNamedQuery - Creates a new instance of Query for the named query.");
                    System.out.println("15. createNativeQuery (version 1) - Creates a new instance of Query for the provided native SQL query.");
                    System.out.println("16. createNativeQuery (version 2) - Creates a new instance of Query for the provided native SQL query, with the specified result type.");
                    System.out.println("17. createNativeQuery (version 3) - Creates a new instance of Query for the provided native SQL query, with the specified result set mapping.");
                    System.out.println("18. refresh - Refreshes the state of the entity with the current state in the database.");
                    System.out.println("19. contains - Checks if the entity is managed by the EntityManager.");
                    System.out.println("20. close - Closes the EntityManager, releasing its resources.");
                    System.out.println("21. updatePersonDb - Updates the attribute of a Person in the database with a new value.");
                    System.out.println("22. updatePersonObj - Updates the attribute of a Person in memory with a new value.");
                    System.out.println("23. modifyPersonId - Modifies the ID of a Person based on user input.");
                    System.out.println("24. Display Objects - Display the current state of all Person objects in memory");
                    System.out.println("25. Descriptive Menu - Shows the list of available commands with a brief description of each.");
                    System.out.println("26. Exit the program.");
                    break;

                case 26:
                    System.out.println("Concluding the session. We appreciate your engagement with our learning system! We hope it has been a valuable resource in your journey of understanding Java Persistence API (JPA). Thank you and we look forward to your next visit!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 26);

        scanner.close();
    }

    private static List<Person> createPeople() {
        Person p1 = new Person(null, "John Smith", "john@gmail.com");
        Person p2 = new Person(null, "James Johnson", "james@gmail.com");
        Person p3 = new Person(null, "Mary Williams", "mary@gmail.com");
        Person p4 = new Person(null, "Patricia Brown", "patricia@gmail.com");
        Person p5 = new Person(null, "Robert Jones", "robert@gmail.com");
        Person p6 = new Person(null, "Michael Miller", "michael@gmail.com");
        Person p7 = new Person(null, "Linda Davis", "linda@gmail.com");
        Person p8 = new Person(null, "Elizabeth Garcia", "elizabeth@gmail.com");
        Person p9 = new Person(null, "Charles Rodriguez", "charles@gmail.com");
        Person p10 = new Person(null, "Jennifer Wilson", "jennifer@gmail.com");

        return Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }
}

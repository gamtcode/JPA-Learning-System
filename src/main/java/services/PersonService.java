package services;

import domain.Person;

import javax.persistence.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class PersonService {
    private final EntityManager entityManager;

    public PersonService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Solicits the user to make a choice whether to execute the method or get more details.
     *
     * @param scanner The Scanner object to be used for user input.
     * @return The user's choice as a string. It can be "E" for execute or "D" for details. If an error occurs, it returns "D" as a default choice.
     */
    public String getUserChoice(Scanner scanner) {
        try {
            System.out.print("Do you want to execute the method or get more details? (E/D) ");
            return scanner.next();
        } catch (NoSuchElementException | IllegalStateException e) {
            System.err.println("Failed to get user choice: " + e.getMessage());
            return "D";
        }
    }

    /**
     * Makes the passed entity instance managed and persistent.
     *
     * @param person the entity to persist
     */
    public void persist(Person person) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(person);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to persist entity: " + e.getMessage());
        }
    }

    /**
     * Merges the changes of a detached entity back into the persistence context.
     *
     * @param people the list of all people
     * @param id     the id of the person in the list to merge
     */
    public void merge(List<Person> people, Integer id) {
        try {
            entityManager.getTransaction().begin();
            Person person = people.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (person != null) {
                Person managedPerson = entityManager.merge(person);
                int index = people.indexOf(person);
                people.set(index, managedPerson);
                entityManager.getTransaction().commit();
            } else {
                System.err.println("Failed to merge entity: No entity with id " + id + " found.");
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to merge entity: " + e.getMessage()+ "\n");
        }
    }

    /**
     * Removes the entity instance from the persistence context, causing the removal from the database.
     *
     * @param id the id of the entity to remove
     */
    public void remove(Integer id) {
        try {
            entityManager.getTransaction().begin();
            Person person = entityManager.find(Person.class, id);
            if (person != null) {
                entityManager.remove(person);
                entityManager.getTransaction().commit();
            } else {
                System.err.println("Failed to remove entity: No entity with id " + id + " found.\n");
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to remove entity: " + e.getMessage() + "\n");
        }
    }

    /**
     * Finds an entity by its class type and primary key.
     *
     * @param id the primary key of the entity
     * @return the found entity or null if not found
     */
    public Person find(Integer id) {
        try {
            return entityManager.find(Person.class, id);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments provided for find operation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets a reference to the entity without retrieving its data until needed (lazy loading).
     *
     * @param id the primary key of the entity
     * @return a reference to the entity
     */
    public Person getReference(Integer id) {
        try {
            return entityManager.getReference(Person.class, id);
        } catch (EntityNotFoundException e) {
            System.err.println("Entity with id " + id + " not found: " + e.getMessage());
            return null;
        }
    }

    /**
     * Synchronizes the persistence context with the underlying database, writing any changes.
     */
    public void flush() {
        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to synchronize with the database: " + e.getMessage());
        }
    }

    /**
     * Checks if the current transaction associated with the EntityManager is active.
     *
     * @return true if the transaction is active, false otherwise
     * @throws IllegalStateException if the EntityManager is in a state that does not allow a transaction to be retrieved
     */
    public boolean isTransactionActive() {
        try {
            EntityTransaction et = entityManager.getTransaction();
            return et != null && et.isActive();
        } catch (IllegalStateException e) {
            System.err.println("Cannot check the transaction status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears the persistence context, detaching all entities.
     */
    public void clear() {
        try {
            entityManager.clear();
        } catch (IllegalStateException e) {
            System.err.println("Cannot clear the persistence context: " + e.getMessage());
        }
    }

    /**
     * Detaches the entity with the given ID from the persistence context.
     *
     * @param id the ID of the entity to detach
     */
    public void detach(Integer id) {
        try {
            Person person = entityManager.find(Person.class, id);
            if (person != null) {
                entityManager.detach(person);
            } else {
                System.out.println("The person with the entered ID does not exist in the database.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Cannot detach a null entity: " + e.getMessage());
        }
    }

    /**
     * Checks if the EntityManager is open.
     *
     * @return true if the EntityManager is open, false otherwise
     */
    public boolean isEntityManagerOpen() {
        return entityManager.isOpen();
    }

    /**
     * Returns the metamodel corresponding to the persistence context.
     *
     * @return the metamodel, or null if it cannot be retrieved
     * @throws IllegalStateException if the EntityManager has been closed
     */
    public Metamodel getMetamodel() {
        try {
            return entityManager.getMetamodel();
        } catch (IllegalStateException e) {
            System.err.println("Cannot retrieve the metamodel: " + e.getMessage());
            return null;
        }
    }

    /**
     * Prints the attributes of a person.
     *
     * @param person the person whose attributes should be printed
     * @throws IllegalStateException if the EntityManager has been closed
     */
    public void printPersonAttributes(Person person) {
        Metamodel metamodel = getMetamodel();
        EntityType<Person> personEntityType = metamodel.entity(Person.class);
        Set<Attribute<? super Person, ?>> attributes = personEntityType.getAttributes();
        System.out.println("Attributes of Person with ID " + person.getId() + ":");
        for (Attribute<? super Person, ?> attribute : attributes) {
            // Convert field name to getter method name
            String getterName = "get" + attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1);
            // Use reflection to get the value of the attribute
            Object value;
            try {
                value = person.getClass().getMethod(getterName).invoke(person);
            } catch (Exception e) {
                value = "[Error retrieving value]";
            }
            System.out.println("- " + attribute.getName() + ": " + value);
        }
    }

    /**
     * Creates a new instance of Query for the provided JPQL query.
     *
     * @param qlString a JPQL query
     * @return the new query instance
     */
    public Query createQuery(String qlString) {
        try {
            return entityManager.createQuery(qlString);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid JPQL query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Finds a person by name using a named query.
     *
     * @param name the name of the person to find
     * @return the person with the given name, or null if no such person was found
     */
    public List<Person> findPersonByName(String name) {
        TypedQuery<Person> query = entityManager.createNamedQuery("findPersonByName", Person.class);
        query.setParameter("name", "%" + name + "%");
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Creates a new instance of Query for the provided native SQL query.
     *
     * @param sqlString a native SQL query
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString) {
        try {
            return entityManager.createNativeQuery(sqlString);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid SQL query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new instance of Query for the provided native SQL query, with the specified result type.
     *
     * @param sqlString   a native SQL query
     * @param resultClass the type of the query result
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString, Class<?> resultClass) {
        try {
            return entityManager.createNativeQuery(sqlString, resultClass);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid SQL query or result class: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new instance of Query for the provided native SQL query, with the specified result set mapping.
     *
     * @param sqlString        a native SQL query
     * @param resultSetMapping the result set mapping
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        try {
            return entityManager.createNativeQuery(sqlString, resultSetMapping);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid SQL query or result set mapping: " + e.getMessage());
            return null;
        }
    }

    /**
     * Locks the entity with the provided lock type.
     *
     * @param entity   the entity to lock
     * @param lockMode the lock mode
     */
    public void lock(Object entity, LockModeType lockMode) {
        try {
            entityManager.getTransaction().begin();
            entityManager.lock(entity, lockMode);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to lock entity: " + e.getMessage());
        }
    }

    /**
     * Refreshes the state of the entity with the current state in the database.
     *
     * @param entity the entity to refresh
     */
    public void refresh(Object entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.refresh(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("An error occurred while refreshing the entity: " + e.getMessage() + "\n");
        }
    }

    /**
     * Checks if the entity is managed by the EntityManager.
     *
     * @param entity the entity to check
     * @return true if the entity is managed, false otherwise
     */
    public boolean contains(Object entity) {
        try {
            return entityManager.contains(entity);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid entity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes the EntityManager and releases its resources.
     */
    public void closeEntityManager() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    /**
     * Updates the attribute of a Person with a new value. The attribute can be 'name' or 'email'.
     *
     * @param id        The ID of the Person to be updated.
     * @param attribute The attribute of the Person to be updated. This can be 'name' or 'email'.
     * @param newValue  The new value for the attribute.
     * @return A string message indicating the result of the operation.
     */
    public String updatePersonDb(int id, String attribute, String newValue) {
        Person person = entityManager.find(Person.class, id);
        if (person == null) {
            return "No Person found with ID " + id + ". Please check the database to ensure the entity has been persisted.";
        }
        try {
            entityManager.getTransaction().begin();
            if (attribute.equalsIgnoreCase("name")) {
                person.setName(newValue);
            } else if (attribute.equalsIgnoreCase("email")) {
                person.setEmail(newValue);
            } else {
                return "Invalid attribute. Only 'name' and 'email' can be updated.";
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return "An error occurred while updating the Person: " + e.getMessage();
        }
        return "Person updated successfully.";
    }

    /**
     * Updates a Person object in memory.
     *
     * @param people    The list of people to search for the person to be updated.
     * @param id        The ID of the person to be updated. If this is null, the method will search by name.
     * @param name      The name of the person to be updated. This is used if the id is null.
     * @param attribute The attribute of the person to be updated (should be "name" or "email").
     * @param newValue  The new value for the specified attribute.
     * @return A message indicating the result of the update operation.
     */
    public String updatePersonObj(List<Person> people, Integer id, String name, String attribute, String newValue) {
        Person person;
        if (id != null) {
            person = people.stream()
                    .filter(p -> p.getId() != null && p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } else {
            person = people.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }
        if (person == null) {
            return id != null ? "No Person found with ID " + id + " in memory."
                    : "No Person found with name " + name + " in memory.";
        }
        try {
            if (attribute.equalsIgnoreCase("name")) {
                person.setName(newValue);
            } else if (attribute.equalsIgnoreCase("email")) {
                person.setEmail(newValue);
            } else {
                return "Invalid attribute. Only 'name' and 'email' can be updated with this method.";
            }
        } catch (Exception e) {
            return "An error occurred while updating the Person in memory: " + e.getMessage();
        }
        return "Person updated in memory successfully.";
    }

    /**
     * Modifies the ID of a Person object in memory.
     *
     * @param people   The list of people to search for the person whose ID is to be modified.
     * @param id       The current ID of the person to be modified.
     * @param response The response indicating how to modify the ID (should be "null" or "new").
     * @param newId    The new ID for the person, if the response is "new".
     */
    public void modifyPersonId(List<Person> people, int id, String response, Integer newId) {
        Person person = null;
        for (Person p : people) {
            if (p.getId() != null && p.getId() == id) {
                person = p;
                break;
            }
        }
        if (person == null) {
            System.err.println("No entity with ID " + id + " found." + "\n");
            return;
        }
        try {
            if (response.equalsIgnoreCase("null")) {
                person.setId(null);
                System.out.println("ID set to null successfully.");
            } else if (response.equalsIgnoreCase("new")) {
                if (newId == null) {
                    System.err.println("New ID is null. Please enter a valid ID.");
                } else {
                    person.setId(newId);
                    System.out.println("ID set to " + newId + " successfully.");
                }
            } else {
                System.err.println("Invalid response. Please enter 'null' or 'new'.");
            }
        } catch (Exception e) {
            System.err.println("Failed to modify entity: " + e.getMessage());
        }
    }
}

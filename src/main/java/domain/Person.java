package domain;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@SqlResultSetMapping(
        name = "PersonResult",
        entities = @EntityResult(
                entityClass = Person.class,
                fields = {
                        @FieldResult(name = "id", column = "id"),
                        @FieldResult(name = "name", column = "name"),
                        @FieldResult(name = "email", column = "email")
                }
        )
)

@Entity
@NamedQuery(name = "findPersonByName", query = "SELECT p FROM Person p WHERE p.name LIKE :name")
public class Person implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;

    @Version
    private int version;

    public Person() {
    }

    public Person(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Id: " + id + ", Name: " + name + ", Email: " + email;
    }
}

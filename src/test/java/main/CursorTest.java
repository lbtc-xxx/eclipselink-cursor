package main;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.queries.ScrollableCursor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import entity.Employee;
import entity.EmployeeName;

public class CursorTest {
    static EntityManagerFactory emf = null;
    EntityManager em;

    @BeforeClass
    public static void initClass() {
        emf = Persistence.createEntityManagerFactory("myPU");
    }

    @Before
    public void init() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Employee e").executeUpdate();
        em.persist(new Employee(new EmployeeName("Scott", "Vogel")));
        em.persist(new Employee(new EmployeeName("Nick", "Jett")));
        em.persist(new Employee(new EmployeeName("Martin", "Stewart")));
        em.persist(new Employee(new EmployeeName("Jordan", "Posner")));
        em.persist(new Employee(new EmployeeName("David", "Wood")));
        em.getTransaction().commit();
    }

    // https://wiki.eclipse.org/EclipseLink/Examples/JPA/Pagination
    @Test
    public void cursorTest() {
        TypedQuery query =
            em
                .createQuery(
                    "SELECT e FROM Employee e ORDER BY e.employeeName.lastName ASC, e.employeeName.firstName ASC",
                    Employee.class);
        query.setHint("eclipselink.cursor.scrollable", true);
        ScrollableCursor scrollableCursor = (ScrollableCursor) query.getSingleResult();
        final List<Object> next = scrollableCursor.next(5);
        for (Object item : next) {
            Employee emp = (Employee) item;
            System.out.println(emp);
        }
    }

    @After
    public void clean() {
        em.close();
    }

    @AfterClass
    public static void cleanClass() {
        emf.close();
    }
}

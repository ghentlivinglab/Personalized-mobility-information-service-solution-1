package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.*;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;

/**
 * Created by Michael on 4/27/16.
 */
public class MongoOperatorDAOTest {

    private static final int TEST_SIZE = 20;
    private MongoDataAccessContext dac;
    private OperatorDAO operatorDAO;
    private MongoDataAccessProvider dap;

    /**
     * Set up the DataAccessProvider, DataAccessContext, and DAO.
     */
    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("testbackend");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        operatorDAO = dac.getOperatorDAO();
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }

    /**
     * Test the listAll method by generating a list of random Operators
     * and comparing the results to the original list.
     */
    @Test
    public void testListAllOperators() {
        assertEquals(0, operatorDAO.listAll().size());

        List<Operator> operators = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Operator operator = generateOperator();
            operators.add(operator);
            operatorDAO.insert(operator);
        }

        List<Operator> results = operatorDAO.listAll();
        assertEquals(TEST_SIZE, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Operator> comparator = (o1, o2) -> o1.getIdentifier().compareTo(o2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(operators, comparator);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertOperatorsEqual(operators.get(i), results.get(i));
        }
    }

    /**
     * Test the find method by generating a random Operator, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindOperatorById() {
        Operator operator = generateOperator();
        assertNull(operatorDAO.find(operator.getIdentifier().toString()));

        operatorDAO.insert(operator);
        Operator result = operatorDAO.find(operator.getIdentifier().toString());

        assertNotNull(result);
        assertOperatorsEqual(operator, result);
    }

    /**
     * Test the findByEmail method by generating a random Operator, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindOperatorByEmail() {
        Operator operator = generateOperator();
        assertNull(operatorDAO.findByEmail(operator.getEmail()));

        operatorDAO.insert(operator);
        Operator result = operatorDAO.findByEmail(operator.getEmail());

        assertNotNull(result);
        assertOperatorsEqual(operator, result);
    }

    /**
     * Test the update method by generating a random Operator, inserting it,
     * updating it, and comparing the original to the result.
     */
    @Test
    public void testUpdateOperator() {
        Operator operator = generateOperator();
        operatorDAO.insert(operator);

        Operator randomOperator = generateOperator();
        operator.transferProperties(randomOperator);
        operatorDAO.update(operator);

        Operator result = operatorDAO.find(operator.getIdentifier().toString());
        assertOperatorsEqual(operator, result);
    }

    /**
     * Test the insert method by generating a random Operator, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testInsertOperator() {
        Operator operator = generateOperator();
        operatorDAO.insert(operator);

        Operator result = operatorDAO.find(operator.getIdentifier().toString());

        assertNotNull(result);
        assertOperatorsEqual(operator, result);
    }

    /**
     * Test the delete method by generating and inserting two random Operators,
     * deleting the first, and checking which are present in the results.
     */
    @Test
    public void testDeleteOperator() {
        Operator operator1 = generateOperator();
        Operator operator2 = generateOperator();
        operatorDAO.insert(operator1);
        operatorDAO.insert(operator2);

        operatorDAO.delete(operator1);
        assertEquals(1, operatorDAO.listAll().size());
        List<String> results = operatorDAO.listAll().stream().
                map(Operator::toString).collect(Collectors.toList());

        assertFalse(results.contains(operator1.toString()));
        assertTrue(results.contains(operator2.toString()));
    }

}

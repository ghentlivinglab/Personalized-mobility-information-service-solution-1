package be.ugent.vopro5.backend.businesslayer.businessworkflow.waze;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.mock.MockDataAccessContext;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Jam;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Created by evert on 13/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/test-servlet.xml")
public class WazeEventImporterTest {

    @Autowired
    WazeEventImporter importer;

    @Autowired
    DataAccessProvider dataAccessProvider;

    @Autowired
    WazeInputStreamProvider inputStreamProvider;

    @Before
    public void setUp() throws Exception {
        DataAccessContext dataAccessContext = new MockDataAccessContext();
        when(dataAccessProvider.getDataAccessContext()).thenReturn(dataAccessContext);
    }

    @After
    public void tearDown() throws Exception {
        reset(inputStreamProvider);
    }

    @Test
    public void testFile1() throws Exception {
        InputStream file1 = WazeEventImporterTest.class.getResourceAsStream("/waze/JSON.json");
        when(inputStreamProvider.getInputStream()).thenReturn(file1);
        importer.importEvents();

        List<Event> events = dataAccessProvider.getDataAccessContext().getEventDAO().listAll();
        assertFalse(events.isEmpty());
    }

    @Test
    public void testFile2() throws Exception {
        InputStream file2 = WazeEventImporterTest.class.getResourceAsStream("/waze/TGeoRSS.json");
        when(inputStreamProvider.getInputStream()).thenReturn(file2);
        importer.importEvents();

        List<Event> events = dataAccessProvider.getDataAccessContext().getEventDAO().listAll();
        assertFalse(events.isEmpty());
    }

    @Test
    public void testUpdate() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeUpdate.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterUpdate.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertEquals("New description", dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getDescription());
    }

    @Test
    public void testNonActive() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeDelete.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterDelete.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertFalse(dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).isActive());
    }

    @Test
    public void testException() throws Exception {
        when(inputStreamProvider.getInputStream()).thenThrow(new IOException());
        importer.importEvents();
        assertEquals(0, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
    }

    @Test
    public void testChangeDescription() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeChangeDescription.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterChangeDescription.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertEquals("New description", dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getDescription());
    }

    @Test
    public void testChangeJamsSize() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeChangeJams.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterChangeJams.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().size());
    }

    @Test
    public void changeJamPoints() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeChangeJamPoints.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
        Jam jam = dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null);

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterChangeJamPoints.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertNotEquals(jam.getPoints(), dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null).getPoints());
    }

    @Test
    public void changeJamSpeed() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeChangeJamSpeed.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
        Jam jam = dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null);

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterChangeJamSpeed.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertNotEquals(jam.getSpeed(), dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null).getSpeed());
    }

    @Test
    public void changeJamDelay() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeChangeJamDelay.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
        Jam jam = dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null);

        InputStream after = WazeEventImporterTest.class.getResourceAsStream("/waze/AfterChangeJamDelay.json");
        when(inputStreamProvider.getInputStream()).thenReturn(after);
        importer.importEvents();
        assertNotEquals(jam.getDelay(), dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getJams().stream().findAny().orElse(null).getDelay());
    }

    @Test
    public void testNoChange() throws Exception {
        InputStream before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeUpdate.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
        LocalDateTime lastEditTime = dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getLastEditTime();

        before = WazeEventImporterTest.class.getResourceAsStream("/waze/BeforeUpdate.json");
        when(inputStreamProvider.getInputStream()).thenReturn(before);
        importer.importEvents();
        assertEquals(1, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().size());
        assertEquals(lastEditTime, dataAccessProvider.getDataAccessContext().getEventDAO().listAll().get(0).getLastEditTime());
    }

    @Test
    public void invalidEventType() throws Exception {
        InputStream file1 = WazeEventImporterTest.class.getResourceAsStream("/waze/InvalidEventType.json");
        when(inputStreamProvider.getInputStream()).thenReturn(file1);
        importer.importEvents();

        List<Event> events = dataAccessProvider.getDataAccessContext().getEventDAO().listAll();
        assertFalse(events.isEmpty());
    }

    @Test
    public void invalidEventSubtype() throws Exception {
        InputStream file1 = WazeEventImporterTest.class.getResourceAsStream("/waze/InvalidEventSubtype.json");
        when(inputStreamProvider.getInputStream()).thenReturn(file1);
        importer.importEvents();

        List<Event> events = dataAccessProvider.getDataAccessContext().getEventDAO().listAll();
        assertFalse(events.isEmpty());
    }
}
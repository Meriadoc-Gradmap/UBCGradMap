package org.datacollection;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MockitoJSoupWebScraperTests {


    @Test
    void throwRTExpFrom() throws IOException {
        // get get departments to give io exceptionIOException

        // we make a mocked connection -> this will make a io exception
        Connection mockedConnection = Mockito.mock(Connection.class);

        Mockito.when(mockedConnection.get()).thenThrow((new IOException()));
        Mockito.when(mockedConnection.ignoreContentType(true)).thenReturn(mockedConnection);
        Mockito.when(mockedConnection.execute()).thenThrow((new IOException()));

        try (MockedStatic<Jsoup> mockedJsoup = Mockito.mockStatic(Jsoup.class)) {
            mockedJsoup.when(() -> Jsoup.connect(Mockito.anyString())).thenReturn(mockedConnection);

            assertThrows(RuntimeException.class, WebScraper::getDepartments);
            assertThrows(RuntimeException.class, () -> WebScraper.getCoursesByDepartment("https://vancouver.calendar.ubc.ca/course-descriptions/subject/THROWSIO"));
            assertThrows(RuntimeException.class, () -> WebScraper.getCoursesByDepartmentCode("INVALID"));

            assertEquals(new HashMap<>(), WebScraper.getGradesByDepartment("INVL-100"));
        }


    }

    @Test
    void mockInvalidJson() {

    }

}

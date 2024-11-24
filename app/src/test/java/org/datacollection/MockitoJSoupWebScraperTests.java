package org.datacollection;


import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class MockitoJSoupWebScraperTests {


    @Test
    void throwRTExpFrom() throws IOException {
        // get get departments to give io exceptionIOException

        // we make a mocked connection -> this will make a io exception
        Connection mockedConnection = mock(Connection.class);

        Mockito.when(mockedConnection.get()).thenThrow((new IOException()));
        Mockito.when(mockedConnection.ignoreContentType(true)).thenReturn(mockedConnection);
        Mockito.when(mockedConnection.execute()).thenThrow((new IOException()));

        try (MockedStatic<Jsoup> mockedJsoup = Mockito.mockStatic(Jsoup.class)) {
            mockedJsoup.when(() -> Jsoup.connect(Mockito.anyString())).thenReturn(mockedConnection);

            assertThrows(RuntimeException.class, WebScraper::getDepartments);
            assertThrows(RuntimeException.class, () -> WebScraper.getCoursesByDepartment("https://vancouver.calendar.ubc.ca/course-descriptions/subject/THROWSIO"));
            assertThrows(RuntimeException.class, () -> WebScraper.getCoursesByDepartmentCode("INVALID"));

            assertEquals(new HashMap<>(), WebScraper.getGradesByDepartment("INVL"));
        }


    }

    @Test
    void mockInvalidJson() {
        try (MockedStatic<JsonParser> mockedJsonParse = Mockito.mockStatic(JsonParser.class)){
            mockedJsonParse.when(() -> JsonParser.parseString(Mockito.anyString())).thenThrow(new JsonParseException("Parsing Failed"));
            assertThrows(RuntimeException.class, () -> WebScraper.getGradesByDepartment("MATH"));
        }
    }

    @Test
    void invalidWebsite() throws IOException {

        Connection mockedConnection = mock(Connection.class);

        // there is a lamda that checks for <h3> and then <p>
        String dString ="<html><body><li><h3></h3><p></p></li><li><h3></h3></li><li><p></p></li></body></html>";
        Document d = Jsoup.parse(dString);

        Mockito.when(mockedConnection.get()).thenReturn(d);

        try (MockedStatic<Jsoup> mockedJsoup = Mockito.mockStatic(Jsoup.class)) {
            mockedJsoup.when(() -> Jsoup.connect(Mockito.anyString())).thenReturn(mockedConnection);

            assertNotNull(WebScraper.getCoursesByDepartment("https://vancouver.calendar.ubc.ca/course-descriptions/subject/"));
        }
    }

}

package org.datacollection;

import org.junit.jupiter.api.Test;

public class DataFormatterTests {


    @Test
    public void doAllDataFormatterWork(){
        DataFormatter.createJsonFromCache("courses", "grades");
    }
}

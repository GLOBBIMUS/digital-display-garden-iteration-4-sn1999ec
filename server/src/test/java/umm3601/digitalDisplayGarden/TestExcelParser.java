package umm3601.digitalDisplayGarden;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by benek020 on 3/6/17.
 */
public class TestExcelParser {

    private static String databaseName;

    public MongoClient mongoClient;
    public MongoDatabase testDB;
    public ExcelParser parser;
    public InputStream fromFile;

    @Before
    public void clearAndPopulateDatabase(){
        databaseName = "data-for-testing-only";

        mongoClient = new MongoClient();
        testDB = mongoClient.getDatabase(databaseName);
        testDB.drop();
        fromFile = this.getClass().getResourceAsStream("/AccessionList2016.xlsx");
        parser = new ExcelParser(fromFile, databaseName);
    }



    @Test
    public void testSpeadsheetToDoubleArray(){
        String[][] plantArray = parser.extractFromXLSX(fromFile);
        //printDoubleArray(plantArray);

        assertEquals(1000, plantArray.length);
        assertEquals(plantArray[40].length, plantArray[963].length);
        assertEquals("2016 Accession List: Steve's Design", plantArray[0][1]);
        assertEquals("Begonia", plantArray[6][1]);

    }

    @Test
    public void testCollapse(){
        String[][] plantArray = parser.extractFromXLSX(fromFile);
        //System.out.println(plantArray.length);
        //printDoubleArray(plantArray);

        plantArray = parser.collapseHorizontally(plantArray);
        plantArray = parser.collapseVertically(plantArray);

        //printDoubleArray(plantArray);

        assertEquals(362, plantArray.length);
        assertEquals(8, plantArray[30].length);
        assertEquals(8, plantArray[0].length);
        assertEquals(8, plantArray[3].length);
    }

    @Test
    public void testReplaceNulls(){
        String[][] plantArray = parser.extractFromXLSX(fromFile);
        plantArray = parser.collapseHorizontally(plantArray);
        plantArray = parser.collapseVertically(plantArray);
        parser.replaceNulls(plantArray);

        for (String[] row : plantArray){
            for (String cell : row){
                assertNotNull(cell);
            }
        }
    }

//    @Test
//    public void testGetId(){
//        parser.setLiveUploadId("newId");
//
//        assertEquals("newId", parser.getLiveUploadId());
//    }

    @Test
    public void testPopulateDatabase(){
        String[][] plantArray = parser.extractFromXLSX(fromFile);
        plantArray = parser.collapseHorizontally(plantArray);
        plantArray = parser.collapseVertically(plantArray);
        parser.replaceNulls(plantArray);

        parser.populateDatabase(plantArray, "an arbitrary ID");
        MongoCollection plants = testDB.getCollection("plants");


        assertEquals(286, plants.count());
        assertEquals(11, plants.count(eq("commonName", "Geranium")));
    }

    @Test
    public void testAddandUpdateDatabase() throws IOException{
        parser.parseExcel("Whatever");

        fromFile = this.getClass().getResourceAsStream("/TestUpdateAccessionList2016.xlsx");
        parser = new ExcelParser(fromFile, databaseName);



        parser.parseUpdatedSpreadsheet("new ID", "Whatever");
        MongoCollection plants = testDB.getCollection("plants");

        assertEquals(288, plants.count());

        Document filter = new Document("uploadId", "new ID");
        assertEquals(288, plants.count(filter));

        filter = new Document("cultivar", "iWasUpdated");
        assertEquals(2, plants.count(filter));
    }

    private static void printDoubleArray(String[][] input){
        for(int i = 0; i < input.length; i++){
            if (!(input[i] == (null))) {
                for (int j = 0; j < input[i].length; j++) {
                    //System.out.print(" | " + "i: " + i + " j: " + j + " value: " + input[i][j] );
                    System.out.print(" | " + input[i][j]);
                }
                System.out.println();
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }
        }
    }
}

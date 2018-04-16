import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import TestRailApi.APIException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Using several methods from the TestRail class, the program follows these steps:
 * 1. It reads a CSV file and creates a hashmap of keys and values
 * 2. It then uses the hashmap data to create a JSON file that is compatible with the TestRail API
 * 3. It then connects to the API and updates the TestRun.
 */
public class  Main {
    public static void main(String[] args) throws Exception {

        //TESTRAIL TEST RUN BULK UPDATE using CSV file
        TestRail testrail=new TestRail();

        //These are just some trial methods that get data from testrail
        //testrail.getCase("41898");
        //testrail.runNo("354");

        //instantiate class to read CSV file

        //create hashmap based on csv data
        HashMap testData=testrail.fromFile("tests.csv");
        System.out.println("Data has been read from file: "+testData.toString());

       //build TestRail compatible JSON object using hashmap data
        JSONObject data=testrail.buildJson(testData);

        //update the test run in TestRail, using the new JSON
        testrail.addResults(data, "354");
    }
}
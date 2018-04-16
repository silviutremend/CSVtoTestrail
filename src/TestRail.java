import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import TestRailApi.APIClient;
import TestRailApi.APIException;
import java.util.Date;
import java.util.HashMap;
import java.io.*;
import java.io.IOException;
import java.util.Iterator;

public class TestRail {

    private static String TestRailURL="https://tremend.testrail.net";
    private static String USER="sstefanescu@tremend.ro";
    private static String PASS="TestRail_0810";

    /**
     * Connects to TestRail and gets the results of the test run specified by the testNumber variable.
     * @param testNumber
     * @throws IOException
     * @throws APIException
     */
    public void runNo(String testNumber) throws IOException, APIException {
        APIClient client = new APIClient(TestRailURL);
        client.setUser(USER);
        client.setPassword(PASS);
        System.out.println("Test results for Test Run "+testNumber+":");
        JSONArray result = (JSONArray) client.sendGet("get_results_for_run/"+testNumber);
        System.out.println(result.toJSONString());
    }

    /**
     * Sets test results in TestRail, based on a JSON object that contains the results and a TestRun ID.
     * The Json is created from a CSV file using the CSV reader and the MakeJson classes.
     * @param json
     * @param runID
     * @throws IOException
     * @throws APIException
     */

    public void addResults(JSONObject json, String runID) throws IOException, APIException {
        APIClient client = new APIClient(TestRailURL);
        client.setUser(USER);
        client.setPassword(PASS);

        String postRequest = "add_results/" + runID;
        JSONArray r = (JSONArray) client.sendPost(postRequest, json);
        System.out.println("Update sent to test case " + runID);
    }

    /**
     * Connects to TestRail and gets the information related to a particular test case, specified via the id parameter
     * @param id
     * @throws IOException
     * @throws APIException
     */
    public void getCase(String id) throws IOException, APIException {
        APIClient client = new APIClient(TestRailURL);
        client.setUser(USER);
        client.setPassword(PASS);

        System.out.println("Test Case "+id+":");
        JSONObject c = (JSONObject) client.sendGet("get_case/"+id);

        System.out.println(c.get("title"));
        long updatedOn = (long) c.get("updated_on");
        Date d = new Date(updatedOn);
        System.out.println("Updated on: " + d.toString());

        JSONArray steps = (JSONArray) c.get("custom_steps_separated");
        JSONObject theSteps = (JSONObject) steps.get(0);

        System.out.println("Expected: " + theSteps.get("expected"));
        System.out.println("Content: " + theSteps.get("content"));
        System.out.println(c.toJSONString());
    }

    /**
     * Builds a JSON object with TestRail test run specs, to update the test cases in the respective test run.
     * The JSON is built using a HashMap which contains ID and status of the test cases.
     * The JSON is built from a CSV, using the CSVReader class.
     * @param results
     * @return
     */
    //primeste un hashmap si scoate un obiect json gata pentru a fi trimis la testcase
    public JSONObject buildJson(HashMap results){
        //aici stochez testele
        JSONArray list = new JSONArray();
        //aici va fi jsonul final
        JSONObject bulkData = new JSONObject();

        //iterez prin hashmap si construiesc obiectele json ale fiecarui testcase
        results.forEach((k,v)->{
            //aici stochez datele fiecarui test
            JSONObject test = new JSONObject();
            test.put("test_id", k);
            //System.out.println(k);
            test.put("status_id", v);
            //System.out.println(v);
            //pentru fiecare iteriatie prin hashmap adauga obiectul test la array
            list.add(test);
            //System.out.println("TEST:"+test.toJSONString());
            //System.out.println("LIST:"+list.toJSONString());
        });

        //construiesc json final
        bulkData.put("results", list);
        System.out.print(bulkData);
        return bulkData;
    }

    /**
     * Reads a CSV file and creates a HashMap object that will be made of ID-status pairs.
     * @param filename
     * @return
     */

    public HashMap fromFile(String filename) {
        String csvFile = "C:\\Users\\sstefanescu\\Dropbox\\Tremend\\Java\\CSVtoTestRail\\"+filename;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        HashMap testData = new HashMap();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] testDataString = line.split(cvsSplitBy);
                //System.out.println("Test case ID " + testDataString[0] + ". Status: " + testDataString[1]);
                testData.put(testDataString[0], testDataString[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //returnez un hash cu perechile ID-status.
        return testData;
    }

}

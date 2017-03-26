package umm3601;

import com.mongodb.util.JSON;
import org.joda.time.DateTime;
import umm3601.digitalDisplayGarden.PlantController;
import umm3601.digitalDisplayGarden.CommentWriter;

import java.io.IOException;
import java.util.Date;

import static spark.Spark.*;

import umm3601.digitalDisplayGarden.ExcelParser;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;


public class Server {

    private static String excelTempDir = "/tmp/digital-display-garden";

    public static void main(String[] args) throws IOException {

        port(2538);

//        ExcelParser parser = new ExcelParser("/AccessionList2016.xlsx");
//        parser.parseExel("Today's Database");

        // This users looks in the folder `public` for the static web artifacts,
        // which includes all the HTML, CSS, and JS files generated by the Angular
        // build. This `public` directory _must_ be somewhere in the classpath;
        // a problem which is resolved in `server/build.gradle`.
        staticFiles.location("/public");

        PlantController plantController = new PlantController();

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
 
            return "OK";
        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // Simple example route
        get("/hello", (req, res) -> "Hello World");

        // Redirects for the "home" page
        redirect.get("", "/");
        redirect.get("/", "http://localhost:9000");

        // List plants
        get("api/plants", (req, res) -> {
            res.type("application/json");
            return plantController.listPlants(req.queryMap().toMap());
        });

        //Get a plant
        get("api/plants/:plantID", (req, res) -> {
            res.type("application/json");
            String id = req.params("plantID");
            return plantController.getPlantByPlantID(id);
        });

        //List all Beds
        get("api/gardenLocations", (req, res) -> {
            res.type("application/json");
            return plantController.getGardenLocations();
        });

        // List all uploadIds
        get("api/uploadIds", (req, res) -> {
            res.type("application/json");
            return plantController.listUploadIds();
        });

        post("api/plants/rate", (req, res) -> {
            res.type("application/json");
            return plantController.addFlowerRating(req.body());
        });

        get("api/export", (req, res) -> {
            res.type("application/vnd.ms-excel");
            res.header("Content-Disposition", "attachment; filename=\"plant-comments.xlsx\"");
            // Note that after flush() or close() is called on
            // res.raw().getOutputStream(), the response can no longer be
            // modified. Since writeComments(..) closes the OutputStream
            // when it is done, it needs to be the last line of this function.
            plantController.writeComments(res.raw().getOutputStream(), req.queryMap().toMap().get("uploadId")[0]);
            return res;
        });

        get("api/liveUploadId", (req, res) -> {
            res.type("application/json");
            return JSON.serialize(plantController.getLiveUploadId());
        });

        // Posting a comment
        post("api/plants/leaveComment", (req, res) -> {
            res.type("application/json");
            return plantController.storePlantComment(req.body());
        });

        // Accept an xls file
        post("api/import", (req, res) -> {

            res.type("application/json");
            try {

                MultipartConfigElement multipartConfigElement = new MultipartConfigElement(excelTempDir);
                req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

                String fileName = Long.valueOf(System.currentTimeMillis()).toString();
                Part part = req.raw().getPart("file[]");

                ExcelParser parser = new ExcelParser(part.getInputStream());

                String id = plantController.getAvailableUploadId();

                parser.parseExel(id);
                plantController.setLiveUploadId(id);

                return JSON.serialize(id);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        });

        // Handle "404" file not found requests:
        notFound((req, res) -> {
            res.type("text");
            res.status(404);
            return "Sorry, we couldn't find that!";
        });

    }

}

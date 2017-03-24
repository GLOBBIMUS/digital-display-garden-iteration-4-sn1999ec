package umm3601.digitalDisplayGarden;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonInvalidOperationException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.fields;

import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class PlantController {

    private final MongoCollection<Document> plantCollection;
    private final MongoCollection<Document> commentCollection;

    public PlantController() throws IOException {
        // Set up our server address
        // (Default host: 'localhost', default port: 27017)
        // ServerAddress testAddress = new ServerAddress();

        // Try connecting to the server
        //MongoClient mongoClient = new MongoClient(testAddress, credentials);
        MongoClient mongoClient = new MongoClient(); // Defaults!

        // Try connecting to a database
        MongoDatabase db = mongoClient.getDatabase("test");

        plantCollection = db.getCollection("plants");
        commentCollection = db.getCollection("comments");
    }

    // List plants
    public String listPlants(Map<String, String[]> queryParams) {
        Document filterDoc = new Document();

        if (queryParams.containsKey("gardenLocation")) {
            String location =(queryParams.get("gardenLocation")[0]);
            filterDoc = filterDoc.append("gardenLocation", location);
        }


        if (queryParams.containsKey("commonName")) {
            String commonName =(queryParams.get("commonName")[0]);
            filterDoc = filterDoc.append("commonName", commonName);
        }

        FindIterable<Document> matchingPlants = plantCollection.find(filterDoc);

        return JSON.serialize(matchingPlants);
    }

    /**
     * Takes a String representing a hexadecimal ID number of a plant
     * and when the ID is found in the database returns a JSON document
     * as a String of the following form
     *
     * <code>
     * {
     *  "_id"        : { "$oid": String },
     *  "commonName" : String,
     *  "cultivar"   : String
     * }
     * </code>
     *
     * If the ID is invalid or not found, the following JSON value is
     * returned
     *
     * <code>
     *  null
     * </code>
     *
     * @param id a hexadecimal ID number of a plant in the DB
     * @return a string representation of a JSON value
     */
    public String getPlant(String id) {

        FindIterable<Document> jsonPlant;
        String returnVal;
        try {

            jsonPlant = plantCollection.find(eq("_id", new ObjectId(id)))
                    .projection(fields(include("commonName", "cultivar")));

            Iterator<Document> iterator = jsonPlant.iterator();

            if (iterator.hasNext()) {
                //incrementMetadata(id, "pageViews");
                returnVal = iterator.next().toJson();
            } else {
                returnVal = "null";
            }

        } catch (IllegalArgumentException e) {
            returnVal = "null";
        }

        return returnVal;

    }

    /**
     * Takes a String representing an ID number of a plant
     * and when the ID is found in the database returns a JSON document
     * as a String of the following form
     *
     * <code>
     * {
     *  "plantID"        : String,
     *  "commonName" : String,
     *  "cultivar"   : String
     * }
     * </code>
     *
     * If the ID is invalid or not found, the following JSON value is
     * returned
     *
     * <code>
     *  null
     * </code>
     *
     * @param plantID an ID number of a plant in the DB
     * @return a string representation of a JSON value
     */
    public String getPlantByPlantID(String plantID) {

        FindIterable<Document> jsonPlant;
        String returnVal;
        try {

            jsonPlant = plantCollection.find(eq("id", plantID))
                    .projection(fields(include("commonName", "cultivar")));

            Iterator<Document> iterator = jsonPlant.iterator();

            if (iterator.hasNext()) {
                //incrementMetadata(id, "pageViews");
                returnVal = iterator.next().toJson();
            } else {
                returnVal = "null";
            }

        } catch (IllegalArgumentException e) {
            returnVal = "null";
        }

        return returnVal;

    }

    public String getGardenLocations(){
        AggregateIterable<Document> documents
                = plantCollection.aggregate(
                Arrays.asList(
                        Aggregates.group("$gardenLocation"),
                        Aggregates.sort(Sorts.ascending("_id"))
                ));
        System.err.println(JSON.serialize(documents));
        return JSON.serialize(documents);
    }

    /**
     * Accepts string representation of JSON object containing
     * at least the following.
     * <code>
     *     {
     *         plantId: String,
     *         comment: String
     *     }
     * </code>
     * If either of the keys are missing or the types of the values are
     * wrong, false is returned.
     * @param json string representation of JSON object
     * @return true iff the comment was successfully submitted
     */
    public boolean storePlantComment(String json) {


        try {

            Document toInsert = new Document();
            Document parsedDocument = Document.parse(json);

            if (parsedDocument.containsKey("plantId") && parsedDocument.get("plantId") instanceof String) {
                toInsert.put("commentOnObjectOfId", new ObjectId(parsedDocument.getString("plantId")));
            } else {
                return false;
            }

            if (parsedDocument.containsKey("comment") && parsedDocument.get("comment") instanceof String) {
                toInsert.put("comment", parsedDocument.getString("comment"));
            } else {
                return false;
            }

            commentCollection.insertOne(toInsert);

        } catch (BsonInvalidOperationException e){
            e.printStackTrace();
            return false;
        } catch (org.bson.json.JsonParseException e){
            return false;
        }

        return true;
    }

}
package sap.escooters.adapters.infrastructure.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;

import sap.escooters.adapters.infrastructure.RideJsonMapper;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.repositories.RideRepository;
import java.util.Optional;

public class MongoRideRepository implements RideRepository {
    private final MongoCollection<Document> collection;
    private final RideJsonMapper rideJsonMapper;

    public MongoRideRepository(String connectionString, String dbName, String collectionName, RideJsonMapper rideJsonMapper) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
        this.rideJsonMapper = rideJsonMapper;
    }

    @Override
    public void save(Ride ride) {
        Document doc = Document.parse(rideJsonMapper.serialize(ride));
        String id = ride.getId(); // Assuming Ride has a getId() method
        collection.replaceOne(new Document("id", id), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public Optional<Ride> findById(String id) {
        Document doc = collection.find(new Document("id", id)).first();
        if (doc == null) {
            return Optional.empty();
        } else {
            return Optional.of(rideJsonMapper.deserialize(doc.toJson()));
        }
    }

    @Override
    public int getNumberOfOngoingRides() {
        return (int) collection.countDocuments(new Document("isOngoing", true));
    }
}
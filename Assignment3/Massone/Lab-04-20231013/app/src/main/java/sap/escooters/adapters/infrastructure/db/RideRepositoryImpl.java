package sap.escooters.adapters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.adapters.infrastructure.RideJsonMapper;
import sap.escooters.adapters.mappers.RideSerializer;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.repositories.RideRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RideRepositoryImpl implements RideRepository {
    private static final String RIDES_PATH = "rides";
    private String dbaseFolder = "resources";
    private RideSerializer rideSerializer;

    public RideRepositoryImpl(String dbaseFolder, RideSerializer rideSerializer) {
        this.dbaseFolder = dbaseFolder;
        this.rideSerializer = rideSerializer;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + RIDES_PATH);
    }

    @Override
    public void save(Ride ride) {
        JsonObject rideJson = new JsonObject(rideSerializer.serialize(ride));
        saveObj(RIDES_PATH, rideJson.getString("id"), rideJson);
    }

    @Override
    public Optional<Ride> findById(String id) {
        try {
            String path = dbaseFolder + File.separator + RIDES_PATH + File.separator + id + ".json";
            if (!Files.exists(Paths.get(path))) {
                return Optional.empty();
            }
            String content = new String(Files.readAllBytes(Paths.get(path)));
            System.out.println("Content: " + content);
            JsonObject rideJson = new JsonObject(content);
            Ride ride = rideSerializer.deserialize(rideJson.encode());

            System.out.println("Ride by id: " + ride.getId());
            System.out.println("Is ongoing ride in Repo: " + ride.isOngoing());
            return Optional.of(ride);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private void saveObj(String db, String id, JsonObject obj) {
        try {
            FileWriter fw = new FileWriter(dbaseFolder + File.separator + db + File.separator + id + ".json");
            BufferedWriter wr = new BufferedWriter(fw);
            wr.write(obj.encodePrettily());
            wr.flush();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void makeDir(String name) {
        try {
            File dir = new File(name);
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getNumberOfOngoingRides() {
        try {
            return (int) Files.list(Paths.get(dbaseFolder, RIDES_PATH))
                .filter(Files::isRegularFile)
                .map(path -> {
                    try {
                        return new String(Files.readAllBytes(path));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .map(JsonObject::new)
                .filter(ride -> ride.getString("startDate") != null && ride.getString("endDate") == null)
                .count();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
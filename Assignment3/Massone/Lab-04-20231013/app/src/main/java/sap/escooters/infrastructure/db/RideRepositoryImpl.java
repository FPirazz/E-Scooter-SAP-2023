package sap.escooters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.Ride;
import sap.escooters.ports.output.RideRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class RideRepositoryImpl implements RideRepository {
    private static final String RIDES_PATH = "rides";
    private String dbaseFolder = "resources";

    public RideRepositoryImpl(String dbaseFolder) {
        this.dbaseFolder = dbaseFolder;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + RIDES_PATH);
    }

    @Override
    public void save(Ride ride) {
        JsonObject rideJson = ride.toJson();
        saveObj(RIDES_PATH, rideJson.getString("id"), rideJson);
    }

    @Override
    public Optional<Ride> findById(String id) {
        // Implement the logic to find a ride by id.
        // This method is not implemented in the DataSourceLayerImpl class.
        return Optional.empty();
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
}
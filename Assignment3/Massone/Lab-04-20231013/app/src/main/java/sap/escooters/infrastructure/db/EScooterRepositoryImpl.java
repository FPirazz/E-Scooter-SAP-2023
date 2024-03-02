package sap.escooters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.ports.output.EScooterRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class EScooterRepositoryImpl implements EScooterRepository {
    private static final String ESCOOTERS_PATH = "escooters";
    private String dbaseFolder = "resources";

    public EScooterRepositoryImpl(String dbaseFolder) {
        this.dbaseFolder = dbaseFolder;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + ESCOOTERS_PATH);
    }

    @Override
    public void save(EScooter escooter) {
        JsonObject escooterJson = escooter.toJson();
        saveObj(ESCOOTERS_PATH, escooterJson.getString("id"), escooterJson);
    }

    @Override
    public Optional<EScooter> findById(String id) {
        // Implement the logic to find an escooter by id.
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
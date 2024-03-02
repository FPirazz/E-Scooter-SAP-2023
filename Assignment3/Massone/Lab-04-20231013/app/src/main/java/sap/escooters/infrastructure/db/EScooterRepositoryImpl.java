package sap.escooters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.ports.output.EScooterRepository;
import sap.escooters.ports.output.EScooterSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EScooterRepositoryImpl implements EScooterRepository {
    private static final String ESCOOTERS_PATH = "escooters";
    private String dbaseFolder = "resources";
    private EScooterSerializer escooterSerializer;

    public EScooterRepositoryImpl(String dbaseFolder, EScooterSerializer escooterSerializer) {
        this.dbaseFolder = dbaseFolder;
        this.escooterSerializer = escooterSerializer;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + ESCOOTERS_PATH);
    }

    @Override
    public void save(EScooter escooter) {
        JsonObject escooterJson = escooterSerializer.toJson(escooter);
        saveObj(ESCOOTERS_PATH, escooterJson.getString("id"), escooterJson);
    }


    @Override
    public Optional<EScooter> findById(String id) {
        try {
            String path = dbaseFolder + File.separator + ESCOOTERS_PATH + File.separator + id + ".json";
            if (!Files.exists(Paths.get(path))) {
                return Optional.empty();
            }
            String content = new String(Files.readAllBytes(Paths.get(path)));
            JsonObject escooterJson = new JsonObject(content);
            EScooter escooter = escooterSerializer.fromJson(escooterJson);
            return Optional.of(escooter);
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
}
package sap.escooters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.User;
import sap.escooters.ports.output.UserRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final String USERS_PATH = "users";
    private String dbaseFolder;

    public UserRepositoryImpl(String dbaseFolder) {
        this.dbaseFolder = dbaseFolder;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + USERS_PATH);
    }

    @Override
    public void save(User user) {
        JsonObject userJson = user.toJson();
        saveObj(USERS_PATH, userJson.getString("id"), userJson);
    }

    @Override
    public Optional<User> findById(String id) {
        // Implement the logic to find a user by id.
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
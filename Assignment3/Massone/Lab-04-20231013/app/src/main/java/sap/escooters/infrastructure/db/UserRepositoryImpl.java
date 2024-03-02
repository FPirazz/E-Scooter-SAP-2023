package sap.escooters.infrastructure.db;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.User;
import sap.escooters.ports.output.UserRepository;
import sap.escooters.ports.output.UserSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;


public class UserRepositoryImpl implements UserRepository {
    private static final String USERS_PATH = "users";
    private String dbaseFolder;
    private UserSerializer userSerializer;

    public UserRepositoryImpl(String dbaseFolder, UserSerializer userSerializer) {
        this.dbaseFolder = dbaseFolder;
        this.userSerializer = userSerializer;
        makeDir(dbaseFolder);
        makeDir(dbaseFolder + File.separator + USERS_PATH);
    }

    @Override
    public void save(User user) {
        JsonObject userJson = userSerializer.toJson(user);
        saveObj(USERS_PATH, userJson.getString("id"), userJson);
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            String path = dbaseFolder + File.separator + USERS_PATH + File.separator + id + ".json";
            if (!Files.exists(Paths.get(path))) {
                return Optional.empty();
            }
            String content = new String(Files.readAllBytes(Paths.get(path)));
            JsonObject userJson = new JsonObject(content);
            User user = userSerializer.fromJson(userJson);
            return Optional.of(user);
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
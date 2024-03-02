package sap.escooters.infrastructure;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.ports.output.EScooterSerializer;

import java.util.Optional;

public class EScooterSerializerImpl implements EScooterSerializer {

    @Override
    public JsonObject toJson(EScooter escooter) {
        JsonObject scooterObj = new JsonObject();
        scooterObj.put("id", escooter.getId());
        scooterObj.put("state", escooter.getState().toString());
        Optional<Location> loc = escooter.getCurrentLocation();
        if (loc.isPresent()) {
            JsonObject locObj = new JsonObject();
            locObj.put("latitude", loc.get().getLatitude());
            locObj.put("longitude", loc.get().getLongitude());
            scooterObj.put("location", locObj);            
        } else {
            scooterObj.putNull("location");            
        }            
        return scooterObj;
    }
}
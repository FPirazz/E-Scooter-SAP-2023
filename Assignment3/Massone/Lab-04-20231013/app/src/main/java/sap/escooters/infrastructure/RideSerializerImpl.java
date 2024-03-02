package sap.escooters.infrastructure;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.Ride;
import sap.escooters.ports.output.RideSerializer;

import java.util.Optional;
import java.util.Date;

public class RideSerializerImpl implements RideSerializer {

    @Override
    public JsonObject toJson(Ride ride) {
        JsonObject rideObj = new JsonObject();
        rideObj.put("id", ride.getId());
        rideObj.put("userId", ride.getUser().getId());
        rideObj.put("escooterId", ride.getEScooter().getId());
        rideObj.put("startDate", ride.getStartedDate().toString());
        Optional<Date> endDate = ride.getEndDate();
        
        if (endDate.isPresent()) {
            rideObj.put("endDate", endDate.get().toString());            
        } else {
            rideObj.putNull("location");            
        }            
        return rideObj;
    }
}
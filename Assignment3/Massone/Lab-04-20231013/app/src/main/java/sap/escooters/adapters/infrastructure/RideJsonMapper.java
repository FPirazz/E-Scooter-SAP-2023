package sap.escooters.adapters.infrastructure;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import sap.escooters.adapters.mappers.RideSerializer;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.entities.User;
import sap.escooters.domain.repositories.EScooterRepository;
import sap.escooters.domain.repositories.UserRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class RideJsonMapper implements RideSerializer {
    private final UserRepository userRepository;
    private final EScooterRepository eScooterRepository;
    private final Gson gson = new Gson();

    public RideJsonMapper(UserRepository userRepository, EScooterRepository eScooterRepository) {
        this.userRepository = userRepository;
        this.eScooterRepository = eScooterRepository;
    }

    @Override
    public String serialize(Ride ride) {
        JsonObject rideObj = new JsonObject();
        rideObj.addProperty("id", ride.getId());
        rideObj.addProperty("userId", ride.getUser().getId());
        rideObj.addProperty("onGoing", ride.isOngoing());
        rideObj.addProperty("escooterId", ride.getEScooter().getId());
        rideObj.addProperty("startDate", ride.getStartedDate().toString());
        Optional<Date> endDate = ride.getEndDate();

        if (endDate.isPresent()) {
            rideObj.addProperty("endDate", endDate.get().toString());
        } else {
            rideObj.add("location", null);
        }
        return rideObj.toString();
    }

    @Override
    public Ride deserialize(String rideData) {
        JsonObject rideJson = gson.fromJson(rideData, JsonObject.class);
        String id = rideJson.get("id").getAsString();
        String userId = rideJson.get("userId").getAsString();
        String escooterId = rideJson.get("escooterId").getAsString();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date startDate = null;
        try {
            startDate = formatter.parse(rideJson.get("startDate").getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Optional<Date> endDate = Optional.empty();
        if (rideJson.has("endDate")) {
            try {
                endDate = Optional.of(formatter.parse(rideJson.get("endDate").getAsString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Assuming you have a User and EScooter repository to fetch the respective objects
        User user = userRepository.findById(userId).get();
        EScooter escooter = eScooterRepository.findById(escooterId).get();

        Ride ride = new Ride(id,user, escooter);
        ride.setStartedDate(startDate);
        ride.setEndDate(endDate);
        ride.setOngoing(rideJson.get("onGoing").getAsBoolean());

        return ride;
    }
}
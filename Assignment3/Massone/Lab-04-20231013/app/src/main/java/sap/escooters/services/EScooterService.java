package sap.escooters.application.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.application.ports.input.EScooterUseCases;
import sap.escooters.domain_layer.DomainLayer;
import sap.escooters.domain_layer.EScooter;

import java.util.Optional;

public class EScooterService implements EScooterUseCases {
    private DomainLayer domainLayer;

    public EScooterService(DomainLayer domainLayer) {
        this.domainLayer = domainLayer;
    }

    @Override
    public void registerNewEScooter(String id) throws UserIdAlreadyExistingException {
        domainLayer.addNewEScooter(id);
    }

    @Override
    public JsonObject getEScooterInfo(String id) throws EScooterNotFoundException {
        Optional<EScooter> escooter = domainLayer.getEScooter(id);
        if (escooter.isPresent()) {
            return escooter.get().toJson();
        } else {
            throw new EScooterNotFoundException();
        }
    }
}
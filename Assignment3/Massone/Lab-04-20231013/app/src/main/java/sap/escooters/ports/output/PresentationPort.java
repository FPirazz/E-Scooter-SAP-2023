package sap.escooters.ports.output;

import java.util.Optional;
import sap.layers.Layer;

public interface PresentationPort {
    void init(Optional<Layer> layer);
}
package uk.ac.ic.doc.fleet.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "fleet")
public interface FleetProperties {
    String aggregatorPath();
}

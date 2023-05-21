package uk.ac.ic.doc.fltee.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "fltee")
public interface FlteeProperties {
    String aggregatorPath();
}

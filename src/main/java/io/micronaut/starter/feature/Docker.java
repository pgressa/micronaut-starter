package io.micronaut.starter.feature;

import io.micronaut.starter.command.MicronautCommand;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Docker implements DefaultFeature {

    @Override
    public String getName() {
        return "docker";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean shouldApply(MicronautCommand micronautCommand, List<Feature> selectedFeatures) {
        return false;
    }
}

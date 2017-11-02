package com.toggle.annotations;

import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@FeatureGroup
@Label("Client Toggles")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientsToggleGroup {
}

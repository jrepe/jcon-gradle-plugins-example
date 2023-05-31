package com.whimsical.profiler;

import org.gradle.api.provider.Property;

/**
 * @author jure.repe
 */
public abstract class GcpProfilerExtension {

    public abstract Property<String> getDestination();
}

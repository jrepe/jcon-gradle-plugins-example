package com.whimsical.profiler;

import org.gradle.api.provider.Property;

/**
 * @author jure.repe
 */
abstract class GcpProfilerExtension {

    abstract Property<String> target();
}

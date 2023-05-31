package com.whimsical.profiler;

import de.undercouch.gradle.tasks.download.Download;
import java.io.File;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author jure.repe
 */
public class GcpProfilerPlugin implements Plugin<Project> {

    private static final String PROFILER_SOURCE =
            "https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz";

    @Override
    public void apply(Project project) {
        // configure extension
        GcpProfilerExtension extension =
                project.getExtensions().create("gcpProfilerConfig", GcpProfilerExtension.class);
        extension.getDestination().convention("%s/docker".formatted(project.getProjectDir()));

        // configure download task
        TaskProvider<Download> download = project.getTasks().register("download", Download.class, task -> {
            task.src(PROFILER_SOURCE);
            task.dest("%s/profiler_java_agent.tar.gz"
                    .formatted(extension.getDestination().get()));
            task.onlyIf(a -> isFileMissing(extension.getDestination().get()));
        });

        // configure copy task
        TaskProvider<Copy> copy = project.getTasks().register("copy", Copy.class, task -> {
            task.dependsOn(download);
            task.from(project.tarTree(download.get().getDest()));
            task.into(extension.getDestination().get());
            task.doLast(cleanupAfterDownload(extension.getDestination().get()));
            task.onlyIf(a -> isFileMissing(extension.getDestination().get()));
        });

        // create custom task
        project.getTasks().register("downloadProfiler", task -> {
            task.setDescription("Downloads GCP profiler to a target folder.");
            task.setGroup("Profiler");
            task.dependsOn(copy);
        });
    }

    private boolean isFileMissing(String target) {
        File f = new File("%s/profiler_java_agent.so".formatted(target));
        return !f.isFile();
    }

    private Action<Task> cleanupAfterDownload(String target) {
        return task -> task.getProject()
                .delete(List.of(
                        "%s/profiler_java_agent.tar.gz".formatted(target),
                        "%s/NOTICES".formatted(target),
                        "%s/version.txt".formatted(target)));
    }
}

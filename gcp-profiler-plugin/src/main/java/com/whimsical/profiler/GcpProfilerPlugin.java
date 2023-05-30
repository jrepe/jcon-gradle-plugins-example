package com.whimsical.profiler;

import de.undercouch.gradle.tasks.download.Download;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.util.List;

/**
 * @author jure.repe
 */
public class GcpProfilerPlugin implements Plugin<Project> {

    private static final String DOWNLOAD_PROFILER = "downloadProfiler";
    private static final String CONFIGURE_PROFILER = "configureProfiler";
    private static final String DOWNLOAD_PLUGIN_ID = "de.undercouch.download";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(DOWNLOAD_PLUGIN_ID);
        GcpProfilerExtension extension = project.getExtensions().getByType(GcpProfilerExtension.class);
        TaskProvider<Copy> profilerTask = configureProfilerTasksAndGetTheCopyTask(project, extension);

        project.afterEvaluate(p -> {
            p.getTasks()
                .named(JavaBasePlugin.BUILD_TASK_NAME)
                .configure(t -> t.dependsOn(profilerTask));
        });
    }

    private static TaskProvider<Copy> configureProfilerTasksAndGetTheCopyTask(Project project, GcpProfilerExtension extension) {
        // todo: handle missing property
        String target = extension.target().get();
        TaskProvider<Download> downloadTask = project.getTasks().register(DOWNLOAD_PROFILER, Download.class, t -> {
            t.setDescription("Downloads GCP profiler to a set folder.");
            // this can't be hardcoded, it can be an input option for the users (only the version should be customizable though)
            t.src("https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz");
            t.dest("%s/profiler_java_agent.tar.gz".formatted(target));
            t.onlyIf(a -> filePrecondition(target));
        });

        return project.getTasks().register(CONFIGURE_PROFILER, Copy.class, t -> {
            t.dependsOn(downloadTask);
            t.from(project.tarTree(downloadTask.get().getDest()));
            // this can't be hardcoded; it can be an input option for the users
            t.into(target);
            t.doLast(a -> project.delete(List.of(
                "%s/profiler_java_agent.tar.gz".formatted(target),
                "%s/NOTICES".formatted(target),
                "%s/version.txt".formatted(target))));
            t.onlyIf(a -> filePrecondition(target));
        });
    }

    private static boolean filePrecondition(String target) {
        File f = new File("%s/profiler_java_agent.so".formatted(target));
        return !f.isFile();
    }
}

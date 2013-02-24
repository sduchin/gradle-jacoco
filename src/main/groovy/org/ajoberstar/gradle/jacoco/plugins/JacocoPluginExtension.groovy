package org.ajoberstar.gradle.jacoco.plugins

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.process.JavaForkOptions
import org.gradle.api.tasks.TaskCollection
import org.ajoberstar.gradle.jacoco.JacocoAgentJar
import org.ajoberstar.gradle.jacoco.tasks.JacocoTaskExtension

/**
 * Extension including common properties and methods for Jacoco.
 */
@Slf4j
class JacocoPluginExtension {
	static final String TASK_EXTENSION_NAME = 'jacoco'
	
	/**
	 * Version of Jacoco JARs to use.
	 */
	String toolVersion = '0.6.2.201302030002'

	protected final Project project
	private final JacocoAgentJar agent

	/**
	 * Creates a Jacoco plugin extension.
	 * @param project the project the extension is attached to
	 * @param agent the agent JAR to be used by Jacoco
	 */
	JacocoPluginExtension(Project project, JacocoAgentJar agent) {
		this.project = project
		this.agent = agent
	}

	/**
	 * Applies Jacoco to the given task. Configuration options will be
	 * provided on a task extension named {@link #TASK_EXTENSION_NAME}.
	 * Jacoco will be run as an agent during the execution of the task.
	 * @param task the task to apply Jacoco to.
	 */
	void applyTo(JavaForkOptions task) {
		log.debug "Applying Jacoco to $task.name"
		JacocoTaskExtension extension = task.extensions.create(TASK_EXTENSION_NAME, JacocoTaskExtension, project, agent)
		task.jacoco.destPath = { "${project.buildDir}/jacoco/${task.name}.exec" }
		task.doFirst {
			//add agent
			if (extension.enabled) {
				task.jvmArgs extension.asJvmArg
			}
		}
	}

	/**
	 * Applies Jacoco to all of the given tasks.
	 * @param tasks the tasks to apply Jacoco to
	 * @see #applyTo(JavaForkOptions)
	 */
	void applyTo(TaskCollection tasks) {
		tasks.withType(JavaForkOptions) {
			applyTo(it)
		}
	}
}

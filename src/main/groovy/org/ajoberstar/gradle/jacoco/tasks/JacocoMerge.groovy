/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.gradle.jacoco.tasks

import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskCollection

/**
 * Task to merge multiple execution data files into one.
 */
class JacocoMerge extends JacocoBase {
	/**
	 * Collection of execution data files to merge.
	 */
	@InputFiles
	FileCollection executionData

	/**
	 * Path to write merged execution data to. Defaults to {@code build/jacoco/<task name>.exec}
	 */
	Object destPath = "${getProject().getBuildDir()}/jacoco/${getName()}.exec"

	@TaskAction
	void merge() {
		getAnt().taskdef(name:'merge', classname:'org.jacoco.ant.MergeTask', classpath:getJacocoClasspath().asPath)
		getAnt().merge(destfile:getDestFile()) {
			getExecutionData().addToAntBuilder(ant, 'resources')
		}
	}

	/**
	 * Path to write merged execution daat to.
	 */
	@OutputFile
	File getDestFile() {
		return getProject().file(destPath)
	}

	/**
	 * Adds execution data files to be merged.
	 * @param files one or more files to merge
	 */
	void executionData(Object... files) {
		if (this.executionData == null) {
			this.executionData = getProject().files(files)
		} else {
			this.executionData += getProject().files(files)
		}
	}

	/**
	 * Adds execution data generated by a task to the list
	 * of those to merge. Only tasks
	 * with a {@link JacocoTaskExtension} will be included;
	 * all others will be ignored.
	 * @param tasks one or more tasks to merge
	 */
	void executionData(Task... tasks) {
		tasks.each { task ->
			JacocoTaskExtension extension = task.extensions.findByType(JacocoTaskExtension)
			if (extension != null) {
				executionData({ extension.destFile })
				this.executionData.builtBy task
			}
		}
	}

	/**
	 * Adds execution data generated by the given tasks to
	 * the list of those merged.
	 * Only tasks with a {@link JacocoTaskExtension} will
	 * be included; all others will be ignored.
	 * @param tasks one or more tasks to merge
	 */
	void executionData(TaskCollection tasks) {
		tasks.all {	executionData(it) }
	}
}

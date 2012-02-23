/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.data.hadoop.cascading;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import cascading.cascade.Cascade;
import cascading.flow.Flow;
import cascading.management.UnitOfWork;
import cascading.stats.CascadingStats;

/**
 * Simple runner for executing {@link Cascade}s or {@link Flow}s. By default, the runner waits for the jobs to finish and returns its status.
 * 
 * @author Costin Leau
 */
public class CascadeRunner implements InitializingBean, DisposableBean, FactoryBean<CascadingStats> {

	private CascadingStats stats;
	private boolean waitToComplete = true;
	private boolean runAtStartup = true;
	private boolean executed = false;
	private UnitOfWork<CascadingStats> uow;


	@Override
	public void afterPropertiesSet() {
		if (runAtStartup) {
			getObject();
		}
	}

	@Override
	public void destroy() {
		if (uow != null) {
			uow.stop();
		}
	}

	@Override
	public CascadingStats getObject() {
		if (!executed) {
			executed = true;
			stats = Runner.run(uow, waitToComplete);
		}

		return stats;
	}

	@Override
	public Class getObjectType() {
		return (stats != null ? stats.getClass() : CascadingStats.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets the unit of work. Can be of type {@link Flow} or {@link Cascade}.
	 *
	 * @param uow the new unit of work.
	 */
	public void setUnitOfWork(UnitOfWork<CascadingStats> uow) {
		this.uow = uow;
	}

	/**
	 * Sets the run at startup.
	 *
	 * @param runAtStartup The runAtStartup to set.
	 */
	public void setRunAtStartup(boolean runAtStartup) {
		this.runAtStartup = runAtStartup;
	}
}
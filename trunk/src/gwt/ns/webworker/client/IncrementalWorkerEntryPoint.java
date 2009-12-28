/*
 * Copyright 2009 Brendan Kenny
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.ns.webworker.client;

import com.google.gwt.user.client.Timer;

/**
 * Entry point class for an incremental Worker.
 * onWorkerLoad() is called when worker is first initialized, then update() is
 * called every getDelay() milliseconds until the worker calls close(), is
 * terminated, or flags itself as complete with isComplete().
 */
public abstract class IncrementalWorkerEntryPoint extends DedicatedWorkerEntryPoint {
	private Timer t;
	private final int DEFAULT_DELAY = 25;
	private int updateDelay = DEFAULT_DELAY;
	
	/**
	 * This method is repeatedly called until worker is closed or terminated or
	 * method returns false.
	 * Work done within execute(), unless known with certainty to be within a
	 * Web Worker, should be limited in duration so platforms that do not
	 * support Web Workers natively stay responsive.
	 * 
	 * @return True if work remains to be done, false if complete
	 */
	public abstract boolean execute();
	
	/**
	 * @return The current delay, in milliseconds, between successive calls of
	 * {@link #execute()}.
	 */
	public int getDelay() {
		return updateDelay;
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		
		// TODO: something more lightweight than Timer? what overhead
		// does it bring?
		// TODO: investigate close() behavior. According to spec, should have
		// the same behavior (wait until execution of current task complete
		// before closing Worker) as conditional gives here
		t = new Timer() {
			@Override
			public void run() {
				if (execute())
					t.schedule(updateDelay);
				else
					close();
			}
		};
		t.schedule(updateDelay);
	}
	
	/**
	 * Set the delay between successive calls of {@link #execute()}.
	 * 
	 * @param delayMillis The delay in milliseconds
	 */
	public void setDelay(int delayMillis) {
		if (delayMillis >= 0)
			updateDelay = delayMillis;
	}
}

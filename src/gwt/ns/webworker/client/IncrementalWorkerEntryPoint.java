/*
 * Copyright 2010 Brendan Kenny
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
 * onWorkerLoad() is called when worker is first initialized, then execute() is
 * called as long as a positive integer delay is returned, until the worker
 * calls close(), is terminated, or returns a non-positive number from execute().
 */
public abstract class IncrementalWorkerEntryPoint extends DedicatedWorkerEntryPoint {
	private Timer t;
	
	/**
	 * This method is called repeatedly until worker is closed, terminated, or
	 * method returns a negative int. System will schedule (though not
	 * necessarily execute) the next call to execute() in returned number of
	 * milliseconds.<br><br>
	 * 
	 * Next call will be scheduled in a minimum of 1 millisecond (for IE) or
	 * in the number of milliseconds returned. If a negative number is
	 * returned, execution is completed and Worker is close()d.<br><br>
	 * 
	 * Work done within execute() will not be performed in a Web Worker on
	 * platforms that don't offer that feature. Execution time should therefore
	 * be limited in duration so the application stays responsive.
	 * 
	 * @return The number of milliseconds in which to call execute() again, -1
	 * if finished.
	 */
	public abstract int execute();
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		
		// TODO: something more lightweight than Timer? what overhead
		// does it bring? (cancel() each time newly scheduled, etc)
		// TODO: investigate close() behavior. According to spec, should have
		// the same behavior (wait until execution of current task complete
		// before closing Worker) as conditional gives here
		t = new Timer() {
			@Override
			public void run() {
				int newdelay = execute();
				
				// must be at least 1 for IE
				newdelay = (newdelay == 0) ? 1 : newdelay;
				
				if (newdelay > 0) {
					t.schedule(newdelay);
				} else {
					close();	// finished
				}
			}
		};
		t.schedule(1);
	}
}

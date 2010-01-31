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

// This code originated in part from code from SpeedTracer, r3
// http://code.google.com/p/speedtracer/source/detail?r=3

package gwt.ns.webworker.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Timer;

/**
 * Entry point class for an incremental Worker.
 * onWorkerLoad() is called when insideWorker is first initialized, then execute() is
 * called as long as a positive integer delay is returned, until the insideWorker
 * calls close(), is terminated, or returns a non-positive number from execute().
 * 
 * Most instance methods call on methods of the same name on the Worker's
 * inherent global scope object.
 * 
 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/'>Current WHATWG Web Worker Draft Spec</a>
 */
public abstract class IncrementalWorkerEntryPoint implements EntryPoint {
	WorkerGlobalScope selfImpl = new WorkerGlobalScopeImplNative();
	private Timer t;
	private boolean terminate = false;
	
	// TODO: be able to restart timer?
	
	
	/**
	 * @see {@link WorkerGlobalScope#close()}
	 */
	public final void close() {
		getGlobalScope().close();
	}
	
	/**
	 * Provides a best-effort shutdown of IncrementalWorker execution loop.
	 * TODO: this shouldn't be exposed
	 */
	public final void terminateLoop() {
		terminate = true;
		t.cancel();
	}
	
	/**
	 * This method is called repeatedly until insideWorker is closed, terminated, or
	 * method returns a negative int. System will schedule (though not
	 * necessarily execute due to the nature of the event queue) the next call
	 * to execute() in returned number of milliseconds.<br><br>
	 * 
	 * If a negative number is returned, execution is completed and Worker is
	 * close()d.<br><br>
	 * 
	 * Work done within execute() will not be performed in a Web Worker on
	 * platforms that don't offer that feature. Execution time should therefore
	 * be limited in duration so the application stays responsive.
	 * 
	 * @return The number of milliseconds in which to call execute() again, a
	 * negative value if finished.
	 */
	public abstract int execute();
	
	/**
	 * @see {@link WorkerGlobalScope#getLocation()}
	 */
	public final WorkerLocation getLocation() {
		return getGlobalScope().getLocation();
	}
	
	/**
	 * @see {@link WorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScript(String url) {
		getGlobalScope().importScript(url);
	}
	
	/**
	 * @see {@link WorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScripts(JsArrayString urls) {
		getGlobalScope().importScripts(urls);
	}
	
	/**
	 * @see {@link WorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScripts(String[] urls) {
		getGlobalScope().importScripts(urls);
	}
	
	@Override
	public void onModuleLoad() {
		onWorkerLoad();
		
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
				
				if (!terminate && newdelay > 0) {
					t.schedule(newdelay);
				} else {
					close();	// finished
				}
			}
		};
		t.schedule(1); // call execute() immediately
	}
	
	public void onWorkerShutdown() {
		
	}
	
	/**
	 * The entry point of execution for DedicatedWorkers. Subclasses will
	 * generally override this method (not {@link #onModuleLoad()}).
	 */
	public abstract void onWorkerLoad();
	
	/**
	 * Retrieve a reference to the global scope of this Worker.
	 * 
	 * @return A reference to the global scope.
	 */
	public WorkerGlobalScope getGlobalScope() {
		return selfImpl;
	}
	
	/**
	 * @see {@link WorkerGlobalScope#postMessage(String)}
	 */
	protected final void postMessage(String message) {
		getGlobalScope().postMessage(message);
	}
	
	/**
	 * @see {@link WorkerGlobalScope#setMessageHandler(MessageHandler)}
	 */
	protected final void setMessageHandler(MessageHandler messageHandler) {
		getGlobalScope().setMessageHandler(messageHandler);
	}
	
	/**
	 * @see {@link WorkerGlobalScope#setErrorHandler(ErrorHandler)}
	 */
	protected final void setErrorHandler(ErrorHandler errorHandler) {
		getGlobalScope().setErrorHandler(errorHandler);
	}
}

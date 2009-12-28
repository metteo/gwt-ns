/*
 * Copyright 2009 Brendan Kenny
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gwt.ns.webworker.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArrayString;

/**
 * Entry point class for implementing a DedicatedWorker.
 * 
 * Runs inside the webworker, so this class cannot assume that Window or
 * Document exist.<br><br>
 * 
 * Most instance methods call on methods of the same name on the Worker's
 * inherent global scope object.
 * 
 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/'>Current W3C Web Worker Draft Spec</a>
 */
public abstract class DedicatedWorkerEntryPoint implements EntryPoint {

	/**
	 * @see {@link DedicatedWorkerGlobalScope#close()}
	 */
	public final void close() {
		getGlobalScope().close();
	}

	/**
	 * @see {@link DedicatedWorkerGlobalScope#getLocation()}
	 */
	public final WorkerLocation getLocation() {
		return getGlobalScope().getLocation();
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScript(String url) {
		getGlobalScope().importScript(url);
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScripts(JsArrayString urls) {
		getGlobalScope().importScripts(urls);
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#importScripts(JsArrayString)}
	 */
	public final void importScripts(String[] urls) {
		getGlobalScope().importScripts(urls);
	}

	/**
	 * Used for Worker initialization. Subclasses will generally override
	 * {@link #onWorkerLoad()}.
	 */
	public void onModuleLoad() {
		onWorkerLoad();
	}

	/**
	 * The entry point of execution for DedicatedWorkers. Override this method
	 * (not {@link #onModuleLoad()}) in subclasses.
	 */
	public abstract void onWorkerLoad();
	
	
	/**
	 * Retrieve a reference to the global scope of this Worker.
	 * 
	 * @return A reference to the global scope.
	 */
	protected DedicatedWorkerGlobalScope getGlobalScope() {
		return DedicatedWorkerGlobalScope.get();
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#postMessage(String)}
	 */
	protected final void postMessage(String message) {
		getGlobalScope().postMessage(message);
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#setMessageHandler(MessageHandler)}
	 */
	protected final void setMessageHandler(MessageHandler messageHandler) {
		getGlobalScope().setMessageHandler(messageHandler);
	}
	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#setErrorHandler(ErrorHandler)}
	 */
	protected final void setErrorHandler(ErrorHandler errorHandler) {
		getGlobalScope().setErrorHandler(errorHandler);
	}
}

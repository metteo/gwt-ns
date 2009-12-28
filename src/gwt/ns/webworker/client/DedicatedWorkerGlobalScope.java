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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * The Dedicated worker has these methods in its global scope.
 * 
 * Runs inside the webworker, so code cannot assume that Window or
 * Document exists.
 * 
 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#workerglobalscope'>WorkerGlobalScope in current W3C Web Worker Draft Spec</a>
 */
public class DedicatedWorkerGlobalScope extends JavaScriptObject {
	protected DedicatedWorkerGlobalScope() {
		// Constructors must be protected in JavaScriptObject overlays.
	}
	
	/**
	 * When called, any tasks currently queued for this Worker are discarded
	 * and further tasks cannot be queued. According to the current spec, the
	 * current task is not discontinued (as opposed to
	 * {@link Worker#terminate()}).
	 * 
	 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#dom-workerglobalscope-close'>Specified close() routine</a>
	 */
	public final native void close() /*-{
		this.close();
	}-*/;
	
	/**
	 * Retrieve a reference to the global scope of this Worker.
	 * 
	 * @return A reference to the global scope.
	 */
	public static native DedicatedWorkerGlobalScope get() /*-{
		return self;
	}-*/;
	
	/**
	 * Return the WorkerLocation object specific for this Worker.
	 * 
	 * @return WorkerLocation for this Worker
	 */
	public final native WorkerLocation getLocation() /*-{
		return this.location;
	}-*/;

	/**
	 * Convenience method for importing and executing a single script.
	 * 
	 * @see {@link DedicatedWorkerGlobalScope#importScripts(JsArrayString) importScripts(JSArrayString)}
	 */
	public final native void importScript(String url) /*-{
		this.importScripts(url);
	}-*/;

	/**
	 * Import a set of scripts into Worker and executes them. Accepts 0 or more
	 * arguments. Execution of scripts is guaranteed to be in order specified.
	 * This method is synchronous.
	 * 
	 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#dom-workerglobalscope-importscripts'>Specified importScripts() routine</a>
	 * 
	 * @param urls JSArray of URLs (relative to Worker creation script) of
	 * 		scripts to import.
	 */
	public final native void importScripts(JsArrayString urls) /*-{
		this.importScripts.apply(null, urls);
	}-*/;

	
	/**
	 * @see {@link DedicatedWorkerGlobalScope#importScripts(JsArrayString) importScripts(JSArrayString)}
	 */
	public final void importScripts(String[] urls) {
		JsArrayString jsUrls = JsArrayString.createArray().cast();
		for (int i = 0, l = urls.length; i < l; ++i) {
			jsUrls.set(i, urls[i]);
		}
		importScripts(jsUrls);
	}
	
	/**
	 * Sends a message to the worker's creator. This accepts a
	 * single parameter, which is the data to send to the creator.<br><br>
	 * 
	 * Accepts only a String (for cross-browser compatibility, for now). Use
	 * {@link gwt.ns.json.client.Json} to pass subclasses of {@link JavaScriptObject}.
	 * 
	 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#dom-dedicatedworkerglobalscope-postmessage'>DedicatedWorkerGlobalScope's postMessage() specification</a>
	 * @param message Message to pass to worker's creator.
	 */
	public final native void postMessage(String message) /*-{
		this.postMessage(message);
	}-*/;

	/**
	 * Set the {@link ErrorHandler} for {@link ErrorEvent}s within this worker.
	 * Replaces any existing handler.
	 * 
	 * @param handler The error handler
	 */
	// TODO(zundel): use UncaughtExceptionHandler... chain more than one handler?
	// May not be needed until hosted mode can support webworkers.
	public final native void setErrorHandler(ErrorHandler handler) /*-{
		this.onerror = function(event) {
			handler.@gwt.ns.webworker.client.ErrorHandler::onError(Lgwt/ns/webworker/client/ErrorEvent;)(event);
		}
	}-*/;
	
	/**
	 * Set the {@link MessageHandler} for {@link MessageEvent}s within this
	 * worker. Replaces any existing handler.
	 * 
	 * @param messageHandler The message handler
	 */
	// see todo on setErrorHandler()
	public final native void setMessageHandler(MessageHandler messageHandler) /*-{
	    this.onmessage = function(event) {
			messageHandler.@gwt.ns.webworker.client.MessageHandler::onMessage(Lgwt/ns/webworker/client/MessageEvent;)(event);
		}
	}-*/;
}

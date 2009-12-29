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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

/**
 * An overlay class for HTML5's Web Worker.
 * 
 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/'>Current W3C Web Worker Draft Spec</a>
 */
public class Worker extends JavaScriptObject {
	protected Worker() {
		// constructors must be protected in JavaScriptObject overlays.
	}
	
	/**
	 * Create a Web Worker from the script located at the passed URL
	 * 
	 * @param url URL of worker script, relative to calling script's URL
	 * @return The created worker
	 */
	public static native Worker create(String url) /*-{
		return new Worker(url);
  	}-*/;

	/**
	 * Takes care of reporting exceptions to the console in hosted mode.
	 * 
	 * @param listener the listener object to call back.
	 * @param port argument from the callback.
	 */
	@SuppressWarnings("unused")
	private static void onErrorImpl(ErrorHandler errorHandler, ErrorEvent event) {
		UncaughtExceptionHandler ueh = GWT.getUncaughtExceptionHandler();
		if (ueh != null) {
			try {
				errorHandler.onError(event);
			} catch (Exception ex) {
				ueh.onUncaughtException(ex);
			}
		} else {
			errorHandler.onError(event);
		}
	}
	
	/**
	 * Takes care of reporting exceptions to the console in hosted mode.
	 * 
	 * @param listener the listener object to call back.
	 * @param port argument from the callback.
	 */
	@SuppressWarnings("unused")
	private static void onMessageImpl(MessageHandler messageHandler,
			MessageEvent event) {
		UncaughtExceptionHandler ueh = GWT.getUncaughtExceptionHandler();
		if (ueh != null) {
			try {
				messageHandler.onMessage(event);
			} catch (Exception ex) {
				ueh.onUncaughtException(ex);
			}
		} else {
			messageHandler.onMessage(event);
		}
	}

	/**
	 * From MDC: Sends a message to the worker's inner scope.  This accepts a
	 * single parameter, which is the data to send to the worker.<br><br>
	 * 
	 * Accepts only a String (for cross-browser compatibility, for now). Use
	 * {@link gwt.ns.json.client.Json} to pass subclasses of
	 * {@link JavaScriptObject}.
	 * 
	 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#dom-worker-postmessage'>Worker postMessage() specification</a>
	 * @see <a href='https://developer.mozilla.org/En/DOM/Worker'>MDC Worker reference</a>
	 * @param message Message to pass to worker.
	 */
	public final native void postMessage(String message) /*-{
		this.postMessage(message);
	}-*/;

	/**
	 * Set the {@link ErrorHandler} for {@link ErrorEvent}s from this worker.
	 * Replaces any existing handler.
	 * 
	 * @param handler The error handler
	 */
	public final native void setErrorHandler(ErrorHandler handler) /*-{
		this.onerror = function(event) {
			@gwt.ns.webworker.client.Worker::onErrorImpl(Lgwt/ns/webworker/client/ErrorHandler;Lgwt/ns/webworker/client/ErrorEvent;)(handler, event);
		}
	}-*/;
	
	/**
	 * Set the {@link MessageHandler} for {@link MessageEvent}s from this worker.
	 * Replaces any existing handler.
	 * 
	 * @param messageHandler The message handler
	 */
	public final native void setMessageHandler(MessageHandler messageHandler) /*-{
		this.onmessage = function(event) {
			@gwt.ns.webworker.client.Worker::onMessageImpl(Lgwt/ns/webworker/client/MessageHandler;Lgwt/ns/webworker/client/MessageEvent;)(messageHandler, event);
		}
	}-*/;
	
	/**
	 * From MDC: Immediately terminates the worker. This does not offer the
	 * worker an opportunity to finish its operations; it is simply stopped at
	 * once.
	 * 
	 * @see <a href='http://www.whatwg.org/specs/web-workers/current-work/#terminate-a-worker'>Terminate a worker specification</a>
	 * @see <a href='https://developer.mozilla.org/En/DOM/Worker'>MDC Worker reference</a>
	 */
	public final native void terminate() /*-{
		this.terminate();
	}-*/;
}

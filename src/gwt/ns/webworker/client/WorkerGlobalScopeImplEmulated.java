/*
 * Copyright 2010 Brendan Kenny
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

import com.google.gwt.core.client.JsArrayString;


/**
 * This class emulates the global scope of a Worker, including passing messages
 * to the "outside" via a proxy. Additional logic is in {@link WorkerImplProxy}
 * The split is necessitated (currently) by the fact that the interface of the
 * "inside" of a Worker is largely the same as that of the "outside". Most of
 * the emulation logic happens in the proxy.
 */
public class WorkerGlobalScopeImplEmulated implements WorkerGlobalScope {
	private MessageHandler insideMessageHandler;
	private WorkerImplProxy outsideProxy;
	
	public WorkerGlobalScopeImplEmulated(WorkerImplProxy proxy) {
		outsideProxy = proxy;
	}
	
	/**
	 * Flag for when worker has been terminated
	 */
	private boolean terminated = false;
	
	/* (non-Javadoc)
	 * @see gwt.ns.webworker.client.WorkerGlobalScope#close()
	 */
	@Override
	public void close() {
		// let proxy handle shutdown
		if (outsideProxy != null)
			outsideProxy.terminate();
	}
	
	/**
	 * Termination of worker scope. Called from Worker proxy.
	 */
	protected void emulatedScopeTerminate() {
		terminated = true;
		
		// break references
		insideMessageHandler = null;
		outsideProxy = null;
	}
	
	@Override
	public native WorkerLocation getLocation() /*-{
		return $wnd.location;
	}-*/;

	@Override
	public void importScript(String url) {
		// TODO: emulated importScripts
	}

	@Override
	public void importScripts(JsArrayString urls) {
		// TODO: emulated importScripts

	}

	@Override
	public void importScripts(String[] urls) {
		// TODO: emulated importScripts

	}
	
	/**
	 * Pass a message into Worker scope.
	 * 
	 * @param message
	 */
	protected void onMessage(MessageEvent event) {
		if (terminated)	//guarded for termination
			return;
		
		if (insideMessageHandler != null)
			insideMessageHandler.onMessage(event);
	}
	
	@Override
	public void postMessage(String message) {
		if (terminated) // guard for termination
			return;
		
		// TODO: full emulation of MessageEvent
		MessageEvent event = MessageEvent.createEmulated(message);
		
		// pass to outside scope
		outsideProxy.onMessage(event);
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		// no-op unless reason to pass errors
	}

	@Override
	public void setMessageHandler(MessageHandler messageHandler) {
		if (!terminated)
			insideMessageHandler = messageHandler;
	}
}

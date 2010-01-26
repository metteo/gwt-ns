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

package gwt.ns.webworker.client.impl;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import gwt.ns.webworker.client.ErrorHandler;
import gwt.ns.webworker.client.IncrementalWorkerEntryPoint;
import gwt.ns.webworker.client.MessageEvent;
import gwt.ns.webworker.client.MessageHandler;
import gwt.ns.webworker.client.Worker;

/**
 * A class to act as proxy between the Worker interface and an emulated Web
 * Worker. Wherever possible, emulation logic happens here. For example,
 * messages passed between this class and the Worker's
 * {@link WorkerGlobalScopeImplEmulated} are placed on the event queue to
 * simulate Worker message posting process.
 */
public class WorkerImplProxy implements Worker {
	private IncrementalWorkerEntryPoint entryPoint;
	private WorkerGlobalScopeImplEmulated workerScope;
	
	/**
	 * Registered recipient of messages posted by the Worker
	 */
	MessageHandler outsideMessageHandler;
	
	private boolean terminated = false;
	
	// TODO: is there a need for or even a source of error events?
	// TODO: refactor.
	// TODO: Message Events as true GwtEvents?
	
	/**
	 * A command to pass a message from Worker's scope to the 
	 * outsideMessageHandler, if registered. Discarded if no
	 * messageHandler.
	 */
	class PassMessageOut implements ScheduledCommand {
		MessageEvent message;
		
		public PassMessageOut(MessageEvent event) {
			message = event;
		}
		
		@Override
		public void execute() {
			if (outsideMessageHandler != null) {
				outsideMessageHandler.onMessage(message);
			}
		}
	}
	
	/**
	 * A command to pass a message from postMessage()--received from
	 * the outside--to the worker's scope. Discarded if worker has been
	 * terminated.
	 */
	class PassMessageIn implements ScheduledCommand {
		MessageEvent message;
		
		public PassMessageIn(MessageEvent event) {
			message = event;
		}
		
		@Override
		public void execute() {
			if (workerScope != null) {
				workerScope.onMessage(message);
			}
		}
	}
	
	/**
	 * Create a proxy from a class extending IncrementalWorkerEntryPoint. Cast
	 * to Worker interface to approximate a native Worker.
	 * 
	 * @param entryPoint EntryPoint of Worker
	 */
	public WorkerImplProxy(IncrementalWorkerEntryPoint entryPoint) {
		// TODO: need a more elegant initialization.
		// reliant on GWT.create() call in IncrementalWorkerEntryPoint
		workerScope = (WorkerGlobalScopeImplEmulated) entryPoint.getGlobalScope();
		workerScope.setProxy(this);
		
		this.entryPoint = entryPoint;
	}

	/**
	 * Add a scheduled command to the end of the event queue.
	 * 
	 * TODO: not sure about making the plunge to full event yet, but
	 * other command queues (eg scheduleDeferred) add way too much overhead
	 * for something called so often. performance is still poor in dev mode
	 * though.
	 * 
	 * @param cmd Command to put on event queue
	 */
	private native void enqueueCommand(ScheduledCommand cmd) /*-{
		setTimeout(function(){
			cmd.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
  		}, 1);
	}-*/;
	
	/**
	 * Receives message events from emulated Worker. Queue command to pass
	 * event to outside messageHandler.
	 */
	protected void onMessage(MessageEvent event) {
		if (terminated) // discard if terminated
			return;
		
		PassMessageOut passOutCmd = new PassMessageOut(event);
		enqueueCommand(passOutCmd);
	}
	
	@Override
	public void postMessage(String message) {
		if (terminated) // discard if terminated. TODO: throw exception?
			return;
		
		// TODO: full emulation of MessageEvent
		MessageEvent event = MessageEvent.createEmulated(message);
		
		// queue a command to send event into worker scope
		PassMessageIn passInCmd = new PassMessageIn(event);
		enqueueCommand(passInCmd);
	}

	/**
	 * Starts the emulated Worker. Invoke only once.
	 */
	public void runWorker() {
		// TODO: guard call?
		entryPoint.onModuleLoad();
	}
	
	@Override
	public void setErrorHandler(ErrorHandler handler) {
		// no-op without reason to add error passing
	}

	@Override
	public void setMessageHandler(MessageHandler messageHandler) {
		if (!terminated)
			outsideMessageHandler = messageHandler;
	}

	@Override
	public void terminate() {
		/* 
		 * Attempt to halt worker.
		 * spec algorithms are slightly different for terminate() vs close()
		 * (immediate halt vs pause for possible task completion, then halt),
		 * but should be close enough for emulation.
		 */
		terminated = true;
		
		// shutdown scope
		if (workerScope != null)
			workerScope.emulatedScopeTerminate();
		
		
		
		// sever references
		workerScope = null;
		entryPoint = null;
		outsideMessageHandler = null;
	}
}

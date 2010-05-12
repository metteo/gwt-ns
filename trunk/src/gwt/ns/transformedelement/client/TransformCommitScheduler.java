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

package gwt.ns.transformedelement.client;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Manages the queue of elements that need transformations applied to them.
 * Allows one {@link Scheduler} callback for all elements.
 */
class TransformCommitScheduler {
	/**
	 * wrapper to allow use of native JS array for commit queue
	 */
	static final class CommitTask extends JavaScriptObject {
		
		public static native CommitTask create(TransformedElement element) /*-{
			return {el: element};
		}-*/;

		protected CommitTask() { }

		/**
		 * Has implicit cast.
		 */
		public native TransformedElement getTransformedElement() /*-{
			return this.el;
		}-*/;
	}
	
	private static JsArray<CommitTask> createJsQueue() {
		return JavaScriptObject.createArray().cast();
	}
	
	/**
	 * The single command scheduled. Calls back on scheduleFinally() to flush
	 * commit queue.
	 */
	private ScheduledCommand commitFlusher = new ScheduledCommand() {
		public void execute() {
			if (scheduled)
				flushQueue();
			scheduled = false;
		}
	};
	
	// TODO: this is ugly but decently fast. compiler output is plain and
	// simple due to nature of isScript(). Regardless, replace with a
	// lightweight collection as soon as they are available.
	private JsArray<CommitTask> jsQueue;
	private Stack<TransformedElement> jreQueue;
	
	// only schedule once per execution context
	private boolean scheduled;
	
	/**
	 * Create a scheduler and queue for {@link TransformedElement} style commits.
	 */
	public TransformCommitScheduler() {
		createQueue();
	}
	
	/**
	 * Schedule a transform commit for a TransformedElement. For
	 * performance, this method doesn't eliminate duplicates. If called
	 * multiple times on a single element before that element's executeCommit()
	 * is itself called, the transform will be committed multiple times.
	 * 
	 * @param element Element to add to commit queue
	 */
	public void scheduleCommit(TransformedElement element) {
		pushElement(element);
		if (!scheduled) {
			Scheduler.get().scheduleFinally(commitFlusher);
			scheduled = true;
		}
	}
	
	// create an environment appropriate queue
	private void createQueue() {
		if (GWT.isScript()) {
			jsQueue = createJsQueue();
		} else {
			jreQueue = new Stack<TransformedElement>();
		}
	}
	
	
	/**
	 * Callback from {@link Scheduler}. Executes queued commits.
	 */
	private void flushQueue() {
		while (!isQueueEmpty()) {
			popElement().executeCommit();
		}
	}
	
	// an environment appropriate queue pop method
	private TransformedElement popElement() {
		if (GWT.isScript()) {
			return popCommitTask(jsQueue).getTransformedElement();
		} else {
			return jreQueue.pop();
		}
	}
	
	private static final native CommitTask popCommitTask(JsArray<CommitTask> queue) /*-{
		return queue.pop();
	}-*/;
	
	// an environment appropriate queue push method
	private void pushElement(TransformedElement element) {
		if (GWT.isScript()) {
			pushCommitTask(jsQueue, CommitTask.create(element));
		} else {
			jreQueue.push(element);
		}
	}
	
	private static final native void pushCommitTask(JsArray<CommitTask> queue, CommitTask task) /*-{
		queue.push(task);
	}-*/;
	
	// an environment appropriate queue isEmpty method
	private boolean isQueueEmpty() {
		if (GWT.isScript()) {
			return jsQueue.length() == 0;
		} else {
			return jreQueue.isEmpty();
		}
	}
}

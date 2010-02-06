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

package gwt.ns.sample.rationals.client;

import gwt.ns.json.client.Json;
import gwt.ns.webworker.client.MessageEvent;
import gwt.ns.webworker.client.MessageHandler;
import gwt.ns.webworker.client.Worker;
import gwt.ns.webworker.client.WorkerFactory;
import gwt.ns.webworker.client.WorkerModuleDef;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This module creates a Worker object that will return a sequence of
 * rational numbers. Upon receipt of an entry, the value is displayed.
 */
public class Rationals implements EntryPoint, MessageHandler {
	Element numerator;
	Element denominator;
	
	// Worker module definition
	@WorkerModuleDef("gwt.ns.sample.rationals.RationalsWorker")
	interface RationalWorkerFactory extends WorkerFactory { }
	
	@Override
	public void onModuleLoad() {
		numerator = RootPanel.get("numerator").getElement();
		denominator = RootPanel.get("denominator").getElement();
		
		// Worker creation
		RationalWorkerFactory factory = GWT.create(RationalWorkerFactory.class);
		Worker worker = factory.createAndStart();

		worker.setMessageHandler(this);
	}

	@Override
	public void onMessage(MessageEvent event) {
		RationalNumber newNum = Json.parse(event.getData());
		numerator.setInnerHTML(newNum.getNumerator() + "");
		denominator.setInnerHTML(newNum.getDenominator() + "");
	}
}

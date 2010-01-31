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

package gwt.ns.webworker.linker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.impl.StandardCompilationResult;
import com.google.gwt.dev.util.Util;

@LinkerOrder(Order.PRE)
public class WorkerCompilationLinker extends AbstractLinker {

	@Override
	public String getDescription() {
		return "Worker Compiler";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts) throws UnableToCompleteException {
		
		ArtifactSet toReturn = new ArtifactSet(artifacts);
		
		// get set of requests for insideWorker compilations from artifacts
		SortedSet<WorkerRequestArtifact> workerRequests = toReturn.find(WorkerRequestArtifact.class);
		if (workerRequests.size() == 0) {
			// TODO: for now, warning on no change made; remove later
			logger.log(TreeLogger.INFO, "No Worker compilations requested. No action taken.");
			return toReturn; // early exit, sorry
		}
		
		// compile all requested workers
		// if this is a recursive call, requests were passed up to parent so returned value is null
		SortedMap<WorkerRequestArtifact, String> workerScripts = WorkerCompiler.exec(logger, workerRequests);
		
		// if they exist, deal with compiled scripts:
		if (workerScripts != null) {
			// directory strong name from all scripts
			byte[][] bytejs = Util.getBytes(workerScripts.values().toArray(new String[0]));
			String scriptDirStrongName = Util.computeStrongName(bytejs);
			
			// emit worker scripts
			for (Map.Entry<WorkerRequestArtifact, String> script : workerScripts.entrySet()) {
				WorkerRequestArtifact req = script.getKey();
				toReturn.add(emitString(logger, script.getValue(), req.getRelativePath(scriptDirStrongName, File.separator)));
			}
			
			// get the set of current compilation results
			SortedSet<CompilationResult> compResults = toReturn.find(CompilationResult.class);
			
			/*
			 * Reading the js from and writing it to a new (if altered)
			 * CompilationResult is expensive if disk cached, so read once and
			 * write only if altered.
			 */
			for (CompilationResult compRes : compResults) {
				// assume all need modification
				// TODO: rule out emulated permutations via properties
				toReturn.remove(compRes);
				
				CompilationResult altered = replacePlaceholder(logger, compRes, WorkerRequestArtifact.getPlaceholderStrongName(), scriptDirStrongName);
				toReturn.add(altered);
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Searches for all instances of a placeholder String in a
	 * CompilationResult. If found, they are replaced as specified and a
	 * new CompilationResult, with a newly generated strong name, is returned.
	 * If no occurrences were found, the original CompilationResult is returned.
	 * 
	 * @param logger
	 * @param result CompilationResult to process
	 * @param placeholder String to be replaced
	 * @param replacement String to insert in place of placeholder
	 * 
	 * @return A CompilationResult suitable for emitting
	 */
	public CompilationResult replacePlaceholder(TreeLogger logger,
			CompilationResult result, String placeholder, String replacement) {
		
		boolean needsMod = false;
		
		String[] js = result.getJavaScript();
		StringBuffer[] jsbuf = new StringBuffer[js.length];
		for (int i = 0; i < jsbuf.length; i++) {
			jsbuf[i] = new StringBuffer(js[i]);
		}
			
		// search and replace in all fragments
		for (StringBuffer fragment : jsbuf) {
			needsMod |= replaceAll(fragment, placeholder, replacement);
		}
		
		// by default, returning unaltered result
		CompilationResult toReturn = result;
		
		// code has been altered, need to create new CompilationResult
		if (needsMod) {
			//logger.log(TreeLogger.INFO, "CompilationResult being modified.");
			
			byte[][] newcode = new byte[jsbuf.length][];
			for (int i = 0; i < jsbuf.length; i++) {
				newcode[i] = Util.getBytes(jsbuf[i].toString());
			}
			
			String strongName = Util.computeStrongName(newcode);
			
			// symbolMap a little more complicated
			// can only get deserialized version, need to reserialize
			// code from com.google.gwt.dev.jjs.JavaToJavaScriptCompiler
			SymbolData[] symbolMap = result.getSymbolMap();
			byte[] serializedSymbolMap;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Util.writeObjectToStream(baos, (Object) symbolMap);
				serializedSymbolMap = baos.toByteArray();
			} catch (IOException e) {
				// should still never happen
				logger.log(TreeLogger.ERROR, "IOException while reserializing "
						+ "SymbolMap.");
				throw new RuntimeException("Should never happen with in-memory stream",
						e);
			}

			StandardCompilationResult altered = new StandardCompilationResult(strongName, newcode, serializedSymbolMap, result.getStatementRanges(), result.getPermutationId());
			
			// need to copy permutation properties to new result
			for (Map<SelectionProperty, String> propertyMap : result.getPropertyMap()) {
				altered.addSelectionPermutation(propertyMap);
			}
			
			toReturn = altered;
			logger.log(TreeLogger.INFO, "Compilation permuation "
					+ toReturn.getPermutationId()
					+ " altered to include path to worker script(s).");
		}
		
		return toReturn;
	}
	
	
	/**
	 * Searches StringBuffer for all occurrences of search and replaces them
	 * with replace.
	 * 
	 * @param buf StringBuffer to search
	 * @param search Substring to search for
	 * @param replace Replacement String if search is found
	 * 
	 * @return True if substring search was found.
	 */
	protected static boolean replaceAll(StringBuffer buf, String search,
			String replace) {
		int len = search.length();
		boolean searchFound = false;
		for (int pos = buf.indexOf(search); pos >= 0; pos = buf.indexOf(search,
				pos + 1)) {
			buf.replace(pos, pos + len, replace);
			searchFound = true;
		}
		
		return searchFound;
	}
}

/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.eol.execute.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.eclipse.epsilon.commons.parse.AST;
import org.eclipse.epsilon.eol.parse.EolParser;
import org.eclipse.epsilon.eol.types.EolMap;


/**
 * A FrameStack is a stack of frames that store
 * the variables created during the execution
 * of an EOL program
 * @author Dimitrios Kolovos
 * @version 1.0
 * @see org.eclipse.epsilon.eol.execute.context.Frame
 */
public class FrameStack {
	
	protected ArrayList<Frame> frames = new ArrayList<Frame>();
	protected Frame global = null;
	protected HashMap<String, Variable> builtInVariables = new HashMap<String, Variable>();
	/**
	 * Creates a new frame stack
	 */
	
	public FrameStack(){
		global = enter(FrameType.UNPROTECTED, null);
		builtInVariables.put("null", Variable.createReadOnlyVariable("null", null));
		//enter(FrameType.LOCAL, null); 
	}
	
	public void dispose() {
		for (Frame frame : frames) {
			frame.dispose();
		}
		global.dispose();
		frames.clear();
		//global = null;
		//frames = null;
	}
	
	/**
	 * Enters a new frame
	 * @param type The type of the frame
	 * @param entryPoint The AST from which the entry is performed
	 */
	public Frame enter(FrameType type, AST entryPoint){
		
		Frame frame = new Frame(type, entryPoint);
		frames.add(0, frame);
		
		//System.err.println("Depth : " + getDepth());
		
		return frame;
	}
	
	/**
	 * Enters a new frame
	 * @param type The type of the frame
	 * @param entryPoint The AST from which the entry is performed
	 */
	//public void enter(FrameType type, AST entryPoint, String label){
	//	frames.add(0, new Frame(type, entryPoint, label));
	//}
	
	/**
	 * Leaves the current frame and 
	 * returns to the previous frame in 
	 * the stack
	 */
	public void leave(AST entryPoint, boolean dispose){
		
		if (frames.size() > 1 ) {
			Frame top = (Frame) frames.get(0);
			while (top.getEntryPoint() != entryPoint){
				frames.get(0).dispose();
				frames.remove(0);
				top = (Frame) frames.get(0);
			}
			if (dispose) { 
				frames.get(0).dispose(); 
			}
			frames.remove(0);
		}
		//while (frames.size() > 1 && ((Frame)frames.get(1)).getType() != FrameType.OPERATION){
		//	frames.remove(frames.get(0));
		//}
	}
	
	public void leave(AST entryPoint) {
		leave(entryPoint, true);
	}
	
	/**
	 * Puts a new variable in the topmost frame
	 * of the scope
	 * @param name Then name of the variable
	 * @param variable The variable 
	 */
	public void put(Variable variable){
		((Frame) frames.get(0)).put(variable);
	}
	
	/**
	 * Returns the variable with the specified
	 * name and if it does not exist returns <code>null</code>
	 * @param name The name of the variable
	 * @return The variable with the specified name or <code>null</code>
	 */
	public Variable get(String name){
		
		if (builtInVariables.containsKey(name)) return builtInVariables.get(name);
		
		//Profiler.INSTANCE.start("Variable");
		ListIterator li = frames.listIterator();
		
		boolean protectedFrameFound = false;
		// Then look into the variable stack
		while (li.hasNext() && !protectedFrameFound){
			Frame frame = (Frame) li.next();
			if (frame.getType() == FrameType.PROTECTED){
				protectedFrameFound = true;
			}
			if (frame.contains(name))
				//Profiler.INSTANCE.stop();
				return frame.get(name);
		}
		//Profiler.INSTANCE.stop();
		return global.get(name);
	}
	
	public boolean isInLoop() {
		return getLoopDepth() > 0;
	}
	
	public int getLoopDepth() {
		int loopDepth = 0;
		ListIterator li = frames.listIterator();
		boolean protectedFrameFound = false;
		while (li.hasNext() && !protectedFrameFound) {
			Frame frame = (Frame) li.next();
			if (frame.getType() == FrameType.PROTECTED){
				protectedFrameFound = true;
			}
			else {
				if (isLoopAst(frame.getEntryPoint())){
					loopDepth ++;
				}
			}
		}
		return loopDepth;
	}
	
	protected boolean isLoopAst(AST ast) {
		return ast!=null && (ast.getType() == EolParser.FOR ||
			ast.getType() == EolParser.WHILE);
	}
	
	/**
	 * Returns if a variable with the 
	 * specified name exists in the scope
	 * @param name
	 * @return
	 */
	public boolean contains(String name){
		return get(name) != null;
	}
	
	/**
	 * Returns the bottom frame of the scope
	 * which stores the global variables
	 * @return 
	 */
	public Frame getGlobals(){
		return (Frame) frames.get(frames.size()-1);
	}
	
	public void printTrace(){
		ListIterator li = frames.listIterator();
		while (li.hasNext()){
			Frame frame = (Frame) li.next();
			if (frame.getType() == FrameType.PROTECTED){
				System.out.println(frame.getLabel() + " (Line:" + frame.getEntryPoint().getLine() + " Column: " + frame.getEntryPoint().getColumn() + ")");
			}
		}
		System.out.println("");
	}
	
	public ListIterator getFrames(){
		return frames.listIterator();
	}
	
	public int getDepth() {
		return frames.size();
	}
	
	@Override
	public FrameStack clone() {
		FrameStack scope = new FrameStack();
		ArrayList frames = new ArrayList();
		
		assert this.frames != null;
		
		for (Frame frame : this.frames) {
			if (frame!=null) frames.add(frame.clone());
		}
		scope.frames = frames;
		scope.global = global.clone();
		return scope;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("-----------SCOPE------------\r\n");
		ListIterator li = frames.listIterator();
		while (li.hasNext()){
			Frame frame = (Frame) li.next();
			buffer.append(frame.toString());
		}
		return buffer.toString();
	}
	
}

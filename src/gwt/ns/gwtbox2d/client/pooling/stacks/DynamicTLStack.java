package gwt.ns.gwtbox2d.client.pooling.stacks;

import gwt.ns.gwtbox2d.client.pooling.TLStack;

import java.util.Stack;


public abstract class DynamicTLStack<T> {

	private final TLStack<T> tlStack = new TLStack<T>();
	
	public T get(){
		Stack<T> stack = tlStack.get();
		
		if(stack.isEmpty()){
			stack.push(newObjectInstance());
			stack.push(newObjectInstance());
			stack.push(newObjectInstance());
		}
		
		return stack.pop();
	}
	
	public void recycle(T argObject){
		tlStack.get().push(argObject);
	}
	
	protected abstract T newObjectInstance();
}

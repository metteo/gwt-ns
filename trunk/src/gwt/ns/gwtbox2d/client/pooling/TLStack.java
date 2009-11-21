package gwt.ns.gwtbox2d.client.pooling;

import java.util.Stack;

//XXX change for gwt
public class TLStack<T> extends notThreadLocal<Stack<T>> {
	protected Stack<T> initialValue(){
		return new Stack<T>();
	}
}

//public class TLStack<T> extends ThreadLocal<Stack<T>> {
//	protected Stack<T> initialValue(){
//		return new Stack<T>();
//	}
//}
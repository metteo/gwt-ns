package gwt.ns.gwtbox2d.client.pooling;

//a very simple replacement for threadlocal since there is no threading in javascript
// (or support in gwt). As with ThreadLocal, notThreadLocal will need to be subclassed
// and initialvalue overridden
public class notThreadLocal<T> {
	private T var;
	
	public T get() {
		if (var == null)
			var = this.initialValue();
		
		return var;
	}
	
	protected T initialValue() {
		return null;
	}
}

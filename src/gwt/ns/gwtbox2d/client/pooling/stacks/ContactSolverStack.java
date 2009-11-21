package gwt.ns.gwtbox2d.client.pooling.stacks;

import gwt.ns.gwtbox2d.client.dynamics.contacts.ContactSolver;

public class ContactSolverStack extends DynamicTLStack<ContactSolver> {
	@Override
	protected ContactSolver newObjectInstance() {
		return new ContactSolver();
	}
	
}

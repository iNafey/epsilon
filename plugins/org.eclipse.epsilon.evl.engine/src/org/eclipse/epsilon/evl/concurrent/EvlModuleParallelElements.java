package org.eclipse.epsilon.evl.concurrent;

import java.util.Collection;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.erl.execute.concurrent.executors.ErlExecutorService;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.execute.context.concurrent.*;

public class EvlModuleParallelElements extends EvlModuleParallel {

	public EvlModuleParallelElements() {
		this(0);
	}
	
	public EvlModuleParallelElements(int parallelism) {
		// No need for the base FrameStack to be thread-safe in this implementation!
		super(parallelism, false);
	}
	
	@Override
	protected void checkConstraints() throws EolRuntimeException {
		IEvlContextParallel context = getContext();
		ErlExecutorService executor = context.getExecutor();
		
		for (ConstraintContext constraintContext : getConstraintContexts()) {
			Collection<Constraint> constraintsToCheck = preProcessConstraintContext(constraintContext);
			
			for (Object object : constraintContext.getAllOfSourceKind(context)) {
				executor.execute(() -> {
					// Lambdas are probably faster for this kind of work
					// @see https://stackoverflow.com/a/24294935/5870336
					// @see http://www.oracle.com/technetwork/java/jvmls2013kuksen-2014088.pdf
					try {
						if (constraintContext.shouldBeChecked(object, context)) {
							for (Constraint constraint : constraintsToCheck) {
								constraint.execute(object, context);
							}
						}
					}
					catch (EolRuntimeException ex) {
						context.handleException(ex, executor);
					}
				});
			}
		}
		
		executor.awaitCompletion();
	}

}

package org.eclipse.emf.refactor.smells.papyrus.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

public class FindModelSmellHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) { }

	@Override
	public void dispose() { }

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Here we go ...");
		return null;
	}

	@Override
	public boolean isEnabled() { return true; }

	@Override
	public boolean isHandled() { return true; }

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) { }

}
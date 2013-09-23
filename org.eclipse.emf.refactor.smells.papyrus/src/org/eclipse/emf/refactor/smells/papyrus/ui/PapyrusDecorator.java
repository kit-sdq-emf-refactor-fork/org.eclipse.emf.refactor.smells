package org.eclipse.emf.refactor.smells.papyrus.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.refactor.smells.papyrus.managers.HighlightManager;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.core.listener.DiagramEventBroker;
import org.eclipse.gmf.runtime.diagram.core.listener.NotificationListener;
//import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.AbstractDecorator;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

public class PapyrusDecorator extends AbstractDecorator {
	
	private HighlightManager manager = HighlightManager.getInstance();
	
	private Map<PolylineConnection, Style> decoratedLines = null;
	
	private LayoutListener layoutListener = new LayoutListener() {		
		@Override
		public void setConstraint(IFigure child, Object constraint) {}
		
		@Override
		public void remove(IFigure child) {}
		
		@Override
		public void postLayout(IFigure container) {
			refresh();
		}
		
		@Override
		public boolean layout(IFigure container) { return false; }
		
		@Override
		public void invalidate(IFigure container) {}};
	
	private NotificationListener notificationListener = new NotificationListener() {
		public void notifyChanged(Notification notification) {
            refresh();
        }};


	public PapyrusDecorator(IDecoratorTarget decoratorTarget) {
		super(decoratorTarget);
		decoratedLines = new HashMap<PolylineConnection, Style>();
	}

	@Override
	public void activate() {
		IGraphicalEditPart gep = (IGraphicalEditPart) getDecoratorTarget().getAdapter(IGraphicalEditPart.class);
		assert gep != null;
		DiagramEventBroker.getInstance(gep.getEditingDomain()).addNotificationListener(gep.getNotationView(), 
				NotationPackage.eINSTANCE.getDescriptionStyle_Description(), notificationListener);
		
		GraphicalEditPart editPart = (GraphicalEditPart) getDecoratorTarget().getAdapter(GraphicalEditPart.class);
		editPart.getFigure().addLayoutListener(layoutListener);
		
		manager.registerDecorator(this, getDecoratorTarget());
	}
	
	@Override
	public void deactivate() {
		removeDecoration();

		IGraphicalEditPart gep = (IGraphicalEditPart) getDecoratorTarget().getAdapter(IGraphicalEditPart.class);
		assert gep != null;
		DiagramEventBroker.getInstance(gep.getEditingDomain()).removeNotificationListener(gep.getNotationView(), 
				NotationPackage.eINSTANCE.getDescriptionStyle_Description(), notificationListener);
		
		GraphicalEditPart editPart = (GraphicalEditPart) getDecoratorTarget().getAdapter(GraphicalEditPart.class);
		editPart.getFigure().removeLayoutListener(layoutListener);
		
		manager.unregisterDecorator(this);
	}
	
	@Override
	protected void removeDecoration() {
		super.removeDecoration();	
		for (PolylineConnection connection : decoratedLines.keySet()){
			connection.setForegroundColor(decoratedLines.get(connection).color);
			connection.setLineWidth(decoratedLines.get(connection).lineWidth);
		}
	}

	@Override
	public void refresh() {
//		System.out.println("Start REFRESH");
		removeDecoration();
		IGraphicalEditPart editPart = (IGraphicalEditPart) getDecoratorTarget().getAdapter(EditPart.class);
		
		View view = (View) getDecoratorTarget().getAdapter(View.class);
		
		if (manager.getPrefferedDecoratorTarget(view.getElement()) == getDecoratorTarget()){
			if(selectionContains(view.getElement())) {
				int x = editPart.getFigure().getBounds().x;
				int y = editPart.getFigure().getBounds().y;
				FigureCanvas canvas = (FigureCanvas) editPart.getViewer().getControl();
				canvas.scrollSmoothTo(x, y);
				if (view instanceof Node) {
					IFigure figure = editPart.getFigure();
					IFigure decoration = new PapyrusDecorationFigure();
					decoration.setSize(figure.getSize());
					setDecoration(getDecoratorTarget().addShapeDecoration(decoration, IDecoratorTarget.Direction.CENTER, 0, false));
				} 
				if (view instanceof Edge) {
					PolylineConnection connection = (PolylineConnection) editPart.getFigure();
					decoratedLines.put(connection, new Style(connection.getForegroundColor(), connection.getLineWidth()));
					connection.setForegroundColor(ColorConstants.red);
					connection.setLineWidth(2);
				} 
			}
		}
//		System.out.println("End REFRESH");
	}
	
	private boolean selectionContains(EObject element){
		boolean contained = false;
		if (element != null) {
			for(EObject selected : manager.getSelected()){
				String fragmentA = EcoreUtil.getURI(selected).fragment();
				String fragmentB = EcoreUtil.getURI(element).fragment();
				contained |= fragmentA.equals(fragmentB);
			}
		}
		return contained;
	}
	
	private static class Style {
		
		public Color color = null;
		public int lineWidth = 0;
	
		public Style(Color c, int lw){
			color = c;
			lineWidth = lw;
		}
	}

}

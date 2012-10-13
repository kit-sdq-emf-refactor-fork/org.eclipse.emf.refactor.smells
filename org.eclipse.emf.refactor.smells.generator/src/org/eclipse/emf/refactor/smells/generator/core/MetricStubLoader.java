package org.eclipse.emf.refactor.smells.generator.core;

import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Loader class for <i>MetricData</i> objects.
 * 
 * @author Matthias Burhenne
 *
 */

public class MetricStubLoader {
	private static final String EMF_METRICS_EXTENSION_POINT_NAME = "org.eclipse.emf.refactor.metrics";
	private static final String METRIC_TAG = "metric";
	private static final String METRIC_NAME_TAG = "metric_name";
	private static final String METRIC_ID_TAG = "id";
	private static final String METRIC_DESCRIPTION_TAG = "metric_description";
	private static final String METRIC_METAMODEL_TAG = "metric_metamodel";
	
	/**
	 * Loads a LinkedList of <i>MetricData</i> representing all metrics currently
	 * <br>- installed on the Eclipse instance.
	 * @return - all metrics currently installed
	 */
	public static LinkedList<MetricData> loadModelMetricData() {
		LinkedList<MetricData> metricStubs = new LinkedList<MetricData>();
		IConfigurationElement[] metricEntry = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EMF_METRICS_EXTENSION_POINT_NAME);
		for (IConfigurationElement element : metricEntry) {
			try {
				if (element.getName().equals(METRIC_TAG)) {	
					final String name = element.getAttribute(METRIC_NAME_TAG);
					final String id = element.getAttribute(METRIC_ID_TAG);
					final String description = element
							.getAttribute(METRIC_DESCRIPTION_TAG);
					final String metamodel = element.getAttribute(METRIC_METAMODEL_TAG);
					MetricData metricData = new MetricData(id, name, description, metamodel);
					if (null != metricData) {
						metricStubs.add(metricData);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return metricStubs;
	}
}

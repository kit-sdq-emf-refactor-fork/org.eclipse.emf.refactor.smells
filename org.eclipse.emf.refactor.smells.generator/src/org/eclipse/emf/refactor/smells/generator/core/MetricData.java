package org.eclipse.emf.refactor.smells.generator.core;

/**
 * Stub class for a plugin based on EMF Metrics. Contains all the data that
 * <br>- make up a metric except the calculation class.
 * 
 * @author Matthias Burhenne
 *
 */
public class MetricData {
	
	String metricID;
	String metricName;
	String metricDescription;
	String metamodel;
		
	public MetricData(String metricID, String metricName, String metricDescription, String metamodel){
		this.metricID = metricID;
		this.metricName = metricName;
		this.metricDescription = metricDescription;
		this.metamodel = metamodel;
	}
	
	public String getMetricID() {
		return metricID;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public String getMetricDescription() {
		return metricDescription;
	}
	
	public String getMetamodel(){
		return metamodel;
	}
}

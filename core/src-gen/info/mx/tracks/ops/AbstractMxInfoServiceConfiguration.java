/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.ops;

import com.robotoworks.mechanoid.ops.OperationServiceConfiguration;
import com.robotoworks.mechanoid.ops.OperationConfigurationRegistry;

public abstract class AbstractMxInfoServiceConfiguration extends OperationServiceConfiguration {
	private OperationConfigurationRegistry mOperationConfigurationRegistry = new MxInfoServiceOperationConfigurationRegistry();
	
	@Override
	public OperationConfigurationRegistry getOperationConfigurationRegistry() {
		return mOperationConfigurationRegistry;
	}
}

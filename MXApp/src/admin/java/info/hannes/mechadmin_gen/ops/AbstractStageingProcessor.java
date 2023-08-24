/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.ops;

import com.robotoworks.mechanoid.ops.Operation;
import com.robotoworks.mechanoid.ops.OperationProcessor;
import com.robotoworks.mechanoid.ops.OperationService;

public abstract class AbstractStageingProcessor extends OperationProcessor {

	@Override
	protected Operation createOperation(String action) {
		return StageingService.CONFIG
			.getOperationConfigurationRegistry()
			.getOperationConfiguration(action)
			.createOperation();
	}
	
	public AbstractStageingProcessor(OperationService service, boolean enableLogging) {
		super(service, enableLogging);
	}
}

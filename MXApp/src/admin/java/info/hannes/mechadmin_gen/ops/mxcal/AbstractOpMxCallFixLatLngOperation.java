/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.ops.mxcal;

import android.content.Intent;
import android.os.Bundle;

import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.ops.Operation;
import com.robotoworks.mechanoid.ops.OperationConfiguration;
import com.robotoworks.mechanoid.ops.OperationContext;
import com.robotoworks.mechanoid.ops.OperationResult;
import com.robotoworks.mechanoid.ops.OperationServiceBridge;

public abstract class AbstractOpMxCallFixLatLngOperation extends Operation {
	public static final String ACTION_OP_MX_CALL_FIX_LAT_LNG = "info.hannes.mechadmin.ops.mxcal.MxCalService.actions.OP_MX_CALL_FIX_LAT_LNG";

	public static final String EXTRA_ALLE = "info.hannes.mechadmin.ops.mxcal.MxCalService.extras.ALLE";

	static class Args {
		public boolean alle;
	}
	
	static class Configuration extends OperationConfiguration {
		@Override 
		public Operation createOperation() {
			return new OpMxCallFixLatLngOperation();
		}
		
		@Override
		public Intent findMatchOnConstraint(OperationServiceBridge bridge, Intent intent) {
			Intent existingRequest = bridge.findPendingRequestByActionWithExtras(AbstractOpMxCallFixLatLngOperation.ACTION_OP_MX_CALL_FIX_LAT_LNG, intent.getExtras());
			
			return existingRequest;
			
		}
	}
	
	public static final Intent newIntent(boolean alle) {
		Intent intent = new Intent(ACTION_OP_MX_CALL_FIX_LAT_LNG);
		intent.setClass(Mechanoid.getApplicationContext(), MxCalService.class);
		
		Bundle extras = new Bundle();
		extras.putBoolean(EXTRA_ALLE, alle);
		
		intent.putExtras(extras);
		
		return intent;
		
	}

	@Override
	public OperationResult execute(OperationContext context) {
		Args args = new Args();
		
		Bundle extras = context.getIntent().getExtras();
		args.alle = extras.getBoolean(EXTRA_ALLE);
		
		return onExecute(context, args);
	}
			
	protected abstract OperationResult onExecute(OperationContext context, Args args);
}

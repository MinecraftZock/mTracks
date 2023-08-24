/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.ops;

import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.ops.Operation;
import com.robotoworks.mechanoid.ops.OperationContext;
import com.robotoworks.mechanoid.ops.OperationResult;
import com.robotoworks.mechanoid.ops.OperationServiceBridge;
import com.robotoworks.mechanoid.ops.OperationConfiguration;
import android.content.Intent;
import android.os.Bundle;

public abstract class AbstractOpSyncFromServerOperation extends Operation {
	public static final String ACTION_OP_SYNC_FROM_SERVER = "info.mx.tracks.ops.MxInfoService.actions.OP_SYNC_FROM_SERVER";

	public static final String EXTRA_UPDATE_PROVIDER = "info.mx.tracks.ops.MxInfoService.extras.UPDATE_PROVIDER";
	public static final String EXTRA_FLAVOR = "info.mx.tracks.ops.MxInfoService.extras.FLAVOR";

	public static class Args {
		public boolean updateProvider;
		public String flavor;
	}
	
	static class Configuration extends OperationConfiguration {
		@Override 
		public Operation createOperation() {
			return new OpSyncFromServerOperation();
		}
		
		@Override
		public Intent findMatchOnConstraint(OperationServiceBridge bridge, Intent intent) {
			Intent existingRequest = bridge.findPendingRequestByActionWithExtras(AbstractOpSyncFromServerOperation.ACTION_OP_SYNC_FROM_SERVER, intent.getExtras());
			
			return existingRequest;
			
		}
	}
	
	public static final Intent newIntent(boolean updateProvider, String flavor) {
		Intent intent = new Intent(ACTION_OP_SYNC_FROM_SERVER);
		intent.setClass(Mechanoid.getApplicationContext(), MxInfoService.class);
		
		Bundle extras = new Bundle();
		extras.putBoolean(EXTRA_UPDATE_PROVIDER, updateProvider);
		extras.putString(EXTRA_FLAVOR, flavor);
		
		intent.putExtras(extras);
		
		return intent;
		
	}

	@Override
	public OperationResult execute(OperationContext context) {
		Args args = new Args();
		
		Bundle extras = context.getIntent().getExtras();
		args.updateProvider = extras.getBoolean(EXTRA_UPDATE_PROVIDER);
		args.flavor = extras.getString(EXTRA_FLAVOR);
		
		return onExecute(context, args);
	}
			
	protected abstract OperationResult onExecute(OperationContext context, Args args);
}

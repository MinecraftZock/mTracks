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

public abstract class AbstractOpGetLatLngOperation extends Operation {
	public static final String ACTION_OP_GET_LAT_LNG = "info.mx.tracks.ops.MxInfoService.actions.OP_GET_LAT_LNG";

	public static final String EXTRA_COUNTRY_LONG = "info.mx.tracks.ops.MxInfoService.extras.COUNTRY_LONG";

	static class Args {
		public String countryLong;
	}
	
	static class Configuration extends OperationConfiguration {
		@Override 
		public Operation createOperation() {
			return new OpGetLatLngOperation();
		}
		
		@Override
		public Intent findMatchOnConstraint(OperationServiceBridge bridge, Intent intent) {
			Intent existingRequest = bridge.findPendingRequestByActionWithExtras(AbstractOpGetLatLngOperation.ACTION_OP_GET_LAT_LNG, intent.getExtras());
			
			return existingRequest;
			
		}
	}
	
	public static final Intent newIntent(String countryLong) {
		Intent intent = new Intent(ACTION_OP_GET_LAT_LNG);
		intent.setClass(Mechanoid.getApplicationContext(), MxInfoService.class);
		
		Bundle extras = new Bundle();
		extras.putString(EXTRA_COUNTRY_LONG, countryLong);
		
		intent.putExtras(extras);
		
		return intent;
		
	}

	@Override
	public OperationResult execute(OperationContext context) {
		Args args = new Args();
		
		Bundle extras = context.getIntent().getExtras();
		args.countryLong = extras.getString(EXTRA_COUNTRY_LONG);
		
		return onExecute(context, args);
	}
			
	protected abstract OperationResult onExecute(OperationContext context, Args args);
}

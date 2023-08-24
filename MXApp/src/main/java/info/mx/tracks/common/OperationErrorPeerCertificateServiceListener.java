package info.mx.tracks.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.ops.OperationResult;
import com.robotoworks.mechanoid.ops.OperationServiceListener;

import info.mx.tracks.R;
import info.mx.tracks.ops.OpSyncFromServerOperation;
import info.mx.tracks.sqlite.MxInfoDBContract;

public class OperationErrorPeerCertificateServiceListener extends OperationServiceListener {

    private final Context context;
    private static final long WAIT_TO_ASK = 60 * 5 * 1000; //5 minutes
    private long lastErrorTime = 0;

    public OperationErrorPeerCertificateServiceListener(Context context) {
        this.context = context;
    }

    @Override
    public void onOperationComplete(int requestId, OperationResult result) {
        if (!result.isOk() && result.getError() != null && result.getError().getMessage() != null) {
            onError(result.getError().getMessage());
        }
        if (result.isOk() && result.getData() != null && result.getData().containsKey(OpSyncFromServerOperation.COUNTRY_RESULT)) {
            String countryMsg = result.getData().getString(OpSyncFromServerOperation.COUNTRY_RESULT);
            if (countryMsg != null && !countryMsg.equals("")) {
                showInfoDlg(context, context.getString(R.string.enable_europe));
            }
        }
    }

    private void onError(String error) {
        if (lastErrorTime + WAIT_TO_ASK < System.currentTimeMillis()) {
            int trackCount = SQuery.newQuery().count(MxInfoDBContract.Tracks.CONTENT_URI);
            if (error.contains("No peer certificate")) {
                showDlg(context, context.getString(R.string.PEER_ERROR));
                lastErrorTime = System.currentTimeMillis();
            } else if (error.contains("Unable to resolve host ") && trackCount < 1000) {
                showDlg(context, context.getString(R.string.HOST_ERROR));
                lastErrorTime = System.currentTimeMillis();
            }
        }
    }

    private void showDlg(final Context context, String text) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(text)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> context
                        .startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS)));
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showInfoDlg(final Context context, String text) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(text)
                .setTitle(R.string.info)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

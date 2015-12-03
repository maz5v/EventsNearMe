package cs4720.cs.virginia.edu.eventsnearme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    static ConfirmDeleteDialogFragment newInstance(String eventId) {
        ConfirmDeleteDialogFragment f = new ConfirmDeleteDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String eventId = getArguments().getString("eventId");
        Log.d("Event ID in fragment:", eventId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // deletion code goes here
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
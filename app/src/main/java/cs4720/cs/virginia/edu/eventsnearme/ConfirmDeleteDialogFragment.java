package cs4720.cs.virginia.edu.eventsnearme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

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
        final String eventId = getArguments().getString("eventId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // deletion code goes here
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
                        query.whereEqualTo("eventId", eventId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objectList, ParseException e) {
                                if (e == null) {
                                    for (ParseObject o : objectList) {
                                        o.deleteInBackground();
                                    }
                                }
                            }
                        });
                        mListener.onConfirmDeleteDialogPositiveClick(ConfirmDeleteDialogFragment.this);
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

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConfirmDeleteDialogListener {
        public void onConfirmDeleteDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ConfirmDeleteDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
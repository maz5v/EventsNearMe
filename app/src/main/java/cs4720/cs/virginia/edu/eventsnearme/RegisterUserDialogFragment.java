package cs4720.cs.virginia.edu.eventsnearme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Student on 12/2/2015.
 */
public class RegisterUserDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View myView = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(myView)
                // Add action buttons
                .setPositiveButton(R.string.log_in, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText usernameView = (EditText)myView.findViewById(R.id.username);
                        EditText passwordView = (EditText)myView.findViewById(R.id.password);
                        mListener.onRegisterUserDialogPositiveClick(RegisterUserDialogFragment.this, usernameView.toString(), passwordView.toString());
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterUserDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface RegisterUserDialogListener {
        public void onRegisterUserDialogPositiveClick(DialogFragment dialog, String username, String password);
        //public void onRegisterDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    RegisterUserDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RegisterUserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

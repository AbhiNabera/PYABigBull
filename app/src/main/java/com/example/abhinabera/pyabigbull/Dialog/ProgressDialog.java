package com.example.abhinabera.pyabigbull.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;


public class ProgressDialog extends DialogFragment {

    AlertDialog Adialog;
    OptionSelectListener optionSelectListener;

    public interface OptionSelectListener{
        public void onPositive();
        public void onNegative();
        public void onOption(String args);
    }

    public void setOptionSelectListener(OptionSelectListener optionSelectListener){
        this.optionSelectListener = optionSelectListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedIntsanceState) {
        super.onCreateDialog(savedIntsanceState);

        String message = getArguments().getString("dialog_msg","");

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.progress_dialog_box, null);

        TextView messageTv = (TextView) dialogView.findViewById(R.id.dialog_message);

        messageTv.setText(message);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Adialog = builder.setView(dialogView).create();
        Adialog.setCancelable(false);
        Adialog.setCanceledOnTouchOutside(false);
        Adialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Adialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return Adialog;
    }
}

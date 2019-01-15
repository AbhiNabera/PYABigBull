package nabera.ranjan.abhinabera.pyabigbull.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.R;


public class MyDialog extends DialogFragment {

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
        String header = getArguments().getString("dialog_header", "");

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_box, null);

        TextView headerTv = (TextView) dialogView.findViewById(R.id.dialog_header);
        TextView messageTv = (TextView) dialogView.findViewById(R.id.dialog_message);
        Button positive = (Button) dialogView.findViewById(R.id.positive);
        Button negative = (Button) dialogView.findViewById(R.id.negative);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group);

        //splash screen conditions
        if(header.contains("NO INTERNET")|| header.contains("ENTER OTP") || header.contains("BALANCE") ){
            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("OKAY");
            negative.setText("LATER");

            positive.setVisibility(View.VISIBLE);
            negative.setVisibility(View.GONE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });

        }else if(header.contains("WARNING")){
            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("REPLACE");
            negative.setText("EXIT");

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onNegative();
                }
            });

        }else if (header.contains("OPTIONS")) {

            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("OKAY");
            negative.setText("CANCEL");

            radioGroup.setVisibility(View.VISIBLE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();

                    if(radioGroup.getCheckedRadioButtonId() == R.id.edit){
                        optionSelectListener.onOption("edit");
                    }else if(radioGroup.getCheckedRadioButtonId() == R.id.delete){
                        optionSelectListener.onOption("delete");
                    }else {
                        optionSelectListener.onOption("none");
                    }

                }
            });

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onNegative();
                }
            });


        }else if(header.contains("ACCOUNT")){
            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("OKAY");
            negative.setText("EXIT");

            positive.setVisibility(View.VISIBLE);
            negative.setVisibility(View.GONE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onNegative();
                }
            });

        }else if(header.contains("SESSION")) {

            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("OKAY");
            negative.setText("LATER");

            positive.setVisibility(View.GONE);
            negative.setVisibility(View.GONE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });

        }else if(header.contains("UPDATE")){

            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("UPDATE");
            negative.setText("LATER");

            positive.setVisibility(View.VISIBLE);
            negative.setVisibility(View.VISIBLE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onNegative();
                }
            });

        }else {

            headerTv.setText(header);
            messageTv.setText(message);
            positive.setText("OKAY");
            negative.setText("LATER");

            positive.setVisibility(View.VISIBLE);
            negative.setVisibility(View.GONE);

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionSelectListener.onPositive();
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Adialog = builder.setView(dialogView).create();
        Adialog.setCanceledOnTouchOutside(false);
        Adialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Adialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return Adialog;
    }
}

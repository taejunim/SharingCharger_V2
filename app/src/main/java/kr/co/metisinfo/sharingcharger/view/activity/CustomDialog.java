package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.metisinfo.sharingcharger.R;

public class CustomDialog extends Dialog {

    String message;

    public CustomDialog(@NonNull Context context, String message) {

        super(context);
        this.message = message;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        TextView textView = findViewById(R.id.dialog_txt);
        textView.setText(message);

        findViewById(R.id.dialog_no_btn).setOnClickListener(view -> this.dismiss());
    }

}

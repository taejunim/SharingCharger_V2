package kr.co.metisinfo.sharingcharger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;

/**
 * @ Class Name   : EmailDialog.java
 * @ Modification : EMAIL DIALOG CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2021.09.27.    임태준
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class EmailDialog extends Dialog  {

    Context context;
    CommonUtils commonUtils = new CommonUtils();

    TextView emailText;
    TextView closeButton;

    public EmailDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_dialog);
        emailText = findViewById(R.id.email_text);
        closeButton = findViewById(R.id.close_button);

        emailText.setOnClickListener(view -> sendEmail());
        closeButton.setOnClickListener(v -> {
            this.dismiss();
        });
    }

    /*
    * DIALOG FINISH
    *
    * */
    public void sendEmail(){

        this.dismiss();
        commonUtils.sendEmailToAdmin(context, "[몬딱충전 문의] : 제목을 입력해주세요.", new String[]{emailText.getText().toString()});
    }

}

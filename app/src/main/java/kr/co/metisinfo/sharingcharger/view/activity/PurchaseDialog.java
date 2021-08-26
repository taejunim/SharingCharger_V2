package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;

public class PurchaseDialog extends Dialog {

    private PurchaseDialogListener purchaseDialogListener;

    //직접 입력 여부
    public boolean isOwnPrice = false;
    String result="";

    TextView textView;

    ArrayList<Button> priceButtonList = new ArrayList<>();

    public PurchaseDialog(@NonNull Context context) {

        super(context);
        //this.purchaseDialogListener = purchaseDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_dialog);

        textView = findViewById(R.id.priceEditText);
        textView.setText("10,000");

        priceButtonList.add(findViewById(R.id.btn_price10000));
        priceButtonList.add(findViewById(R.id.btn_price30000));
        priceButtonList.add(findViewById(R.id.btn_price50000));
        priceButtonList.add(findViewById(R.id.btn_priceOwn));
        setOnClickListenr();

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String inputText = textView.getText().toString();

                if(isOwnPrice) {
                    //직접입력시 천자리수 컴마 찍기
                    if(!inputText.equals("") && !charSequence.toString().equals(result)) {
                        long data = Long.parseLong(inputText.replace(",", ""));
                        DecimalFormat df = new DecimalFormat("###,###");
                        result = df.format(data);
                        textView.setText(result);
                        Editable etext = (Editable) textView.getText();
                        Selection.setSelection(etext, etext.length());

                    } else {
                        Log.d("metis","언제탐..");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }

    private void setOnClickListenr(){

        //확인버튼
        findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {
            String cost =  textView.getText().toString();

            purchaseDialogListener.onPurchaseButtonClicked(cost.replace(",", ""));
            this.dismiss();

        });
        //취소버튼
        findViewById(R.id.dialog_no_btn).setOnClickListener(view -> this.dismiss());

        findViewById(R.id.btn_price10000).setOnClickListener(view -> onPriceButtonClick(findViewById(R.id.btn_price10000)));
        findViewById(R.id.btn_price30000).setOnClickListener(view -> onPriceButtonClick(findViewById(R.id.btn_price30000)));
        findViewById(R.id.btn_price50000).setOnClickListener(view -> onPriceButtonClick(findViewById(R.id.btn_price50000)));
        findViewById(R.id.btn_priceOwn).setOnClickListener(view -> onPriceButtonClick(findViewById(R.id.btn_priceOwn)));

    }

    //인터페이스 설정
    public interface PurchaseDialogListener{
        void onPurchaseButtonClicked(String cost);
    }
    //호출할 리스너 초기화
    public void setDialogListener(PurchaseDialogListener purchaseDialogListener){
        this.purchaseDialogListener = purchaseDialogListener;
    }

    public void onPriceButtonClick(TextView priceButton){

        EditText priceEditText = findViewById(R.id.priceEditText);

        for(int i = 0; i < priceButtonList.size(); i++ ){
            if(priceButton == priceButtonList.get(i))
                priceButton.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.purple));
            else priceButton.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.gray));
        }

        //직접 입력이 아닐때
        if(priceButton != priceButtonList.get(3)){
            isOwnPrice = false;
            priceEditText.setEnabled(false);
            Log.d("metis",priceButton.getText().toString().replace("원",""));
            priceEditText.setText(priceButton.getText().toString().replace("원",""));

        } else {
            isOwnPrice = true;
            priceEditText.setEnabled(true);
            priceEditText.setText("");
        }

    }

}

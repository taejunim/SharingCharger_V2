package kr.co.metisinfo.sharingcharger;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.view.activity.AdminChargerRegisterStep1Fragment;

public class ChargerDialogAdapter extends RecyclerView.Adapter<ChargerDialogAdapter.ViewHolder> {

    private ArrayList<String> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.charger_text); // 각 item View에 대해
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bleText = mData.get(getAdapterPosition());
                    AdminChargerRegisterStep1Fragment.selectedChargerBLEText = bleText;
                    AdminChargerRegisterStep1Fragment.dialog.dismiss(); // dialog 종료
                }
            });
        }
    }

    public ChargerDialogAdapter(ArrayList<String> list) {
        mData = list; // 입력받은 list를 저장
    }

    @Override
    public ChargerDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext(); // parent로부터 content 받음
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.charger_search_dialog_item, parent, false);
        // 각 item의 View는 이전에 정의했던 item layout을 불러옴
        ChargerDialogAdapter.ViewHolder vh = new ChargerDialogAdapter.ViewHolder(view);
        return vh; // ViewHolder 반환
    }

    @Override
    public void onBindViewHolder(ChargerDialogAdapter.ViewHolder holder, int position) {
        String text = mData.get(position); // 어떤 포지션의 텍스트인지 조회
        holder.textView.setText(text); // 해당 포지션의 View item에 텍스트 입힘
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

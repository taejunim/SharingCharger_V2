package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.view.activity.AdminChargerRegisterStep1Fragment;

public class IdDialogAdapter extends RecyclerView.Adapter<IdDialogAdapter.ViewHolder> {

    private List<UserModel> idList = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.id_text); // 각 item View에 대해
        }
    }

    public IdDialogAdapter(List<UserModel> list) {
        idList = list; // 입력받은 list를 저장
    }

    @Override
    public IdDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext(); // parent로부터 content 받음
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.id_dialog_item, parent, false);
        // 각 item의 View는 이전에 정의했던 item layout을 불러옴
        IdDialogAdapter.ViewHolder vh = new IdDialogAdapter.ViewHolder(view);
        return vh; // ViewHolder 반환
    }

    @Override
    public void onBindViewHolder(IdDialogAdapter.ViewHolder holder, int position) {
        String text = idList.get(position).getUsername(); // 어떤 포지션의 텍스트인지 조회
        holder.textView.setText(text); // 해당 포지션의 View item에 텍스트 입힘
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}

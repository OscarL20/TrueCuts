package e.l2040.truecuts;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomsViewHolder>{

    private Context ctx;
    private List<MetaDataChat> metaDataChatList;

    private OnRecyclerListener mOnRecyclerListener;

    public ChatRoomsAdapter(Context ctx, List<MetaDataChat> metaDataChatList, OnRecyclerListener onRecyclerListener) {
        this.ctx = ctx;
        this.metaDataChatList = metaDataChatList;
        this.mOnRecyclerListener = onRecyclerListener;
    }

    @NonNull
    @Override
    public ChatRoomsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.chatroom_layout,viewGroup, false);
        return new ChatRoomsViewHolder(view, mOnRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomsViewHolder chatRoomsViewHolder, int position) {
        MetaDataChat metaDataChat = metaDataChatList.get(position);

        chatRoomsViewHolder.otherPersonsName.setText(metaDataChat.getOtherPersonsName());
        chatRoomsViewHolder.lastText.setText(metaDataChat.getMessage());

        Uri profileImageUri = Uri.parse(metaDataChat.getOtherPersonsImage());
        Picasso.with(ctx).load(profileImageUri).resize(400, 400).centerCrop().into(chatRoomsViewHolder.otherPersonsImage);
    }

    @Override
    public int getItemCount() {
        return metaDataChatList.size();
    }

    class ChatRoomsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView otherPersonsName;
        TextView lastText;
        CircleImageView otherPersonsImage;

        OnRecyclerListener onRecyclerListener;

        public ChatRoomsViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener){
            super(itemView);


            otherPersonsName = itemView.findViewById(R.id.otherPersonsName);
            lastText = itemView.findViewById(R.id.lastText);
            otherPersonsImage= itemView.findViewById(R.id.otherPersonsImage);
            this.onRecyclerListener = onRecyclerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerListener.onRecyclerClick(getAdapterPosition());
        }
    }

    public interface OnRecyclerListener{
        void onRecyclerClick(int position);
    }
}
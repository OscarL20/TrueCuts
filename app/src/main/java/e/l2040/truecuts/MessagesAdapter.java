package e.l2040.truecuts;

import android.content.Context;
import android.drm.DrmStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT =1;

    private Context ctx;
    private List<Message> messageList;

    private OnRecyclerListener mOnRecyclerListener;

    FirebaseUser fUser;

    public MessagesAdapter(Context ctx, List<Message> messageList, OnRecyclerListener onRecyclerListener) {
        this.ctx = ctx;
        this.messageList = messageList;
        this.mOnRecyclerListener = onRecyclerListener;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.send_message,viewGroup, false);
            return new MessagesViewHolder(view, mOnRecyclerListener);
        }else{
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.receiving_message,viewGroup, false);
            return new MessagesViewHolder(view, mOnRecyclerListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder messagesViewHolder, int position) {
        Message message = messageList.get(position);

        messagesViewHolder.textMessage.setText(message.getMessage());
        messagesViewHolder.dateAndTime.setText(message.getDateAndTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textMessage;
        TextView dateAndTime;

        OnRecyclerListener onRecyclerListener;

        public MessagesViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener){
            super(itemView);


            textMessage = itemView.findViewById(R.id.textMessage);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            this.onRecyclerListener = onRecyclerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerListener.onRecyclerClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageList.get(position).getSenderId().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public interface OnRecyclerListener{
        void onRecyclerClick(int position);
    }
}
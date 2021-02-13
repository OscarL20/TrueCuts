package e.l2040.truecuts;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentAppointmentAdapter extends RecyclerView.Adapter<RecentAppointmentAdapter.RecentAppointmentViewHolder>{

    private Context ctx;
    private List<RecentAppointment> recentAppointmentList;

    private OnRecyclerListener mOnRecyclerListener;

    public RecentAppointmentAdapter(Context ctx, List<RecentAppointment> recentAppointmentList, OnRecyclerListener onRecyclerListener) {
        this.ctx = ctx;
        this.recentAppointmentList = recentAppointmentList;
        this.mOnRecyclerListener = onRecyclerListener;
    }

    @NonNull
    @Override
    public RecentAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.items_layout,viewGroup, false);
        return new RecentAppointmentViewHolder(view, mOnRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentAppointmentViewHolder recentAppointmentViewHolder, int position) {
        RecentAppointment recentAppointment = recentAppointmentList.get(position);

        recentAppointmentViewHolder.barberShopName.setText(recentAppointment.getBarberShop());
        recentAppointmentViewHolder.barberName.setText(recentAppointment.getBarber());
        recentAppointmentViewHolder.barberAddress.setText(recentAppointment.getAddress());
        Picasso.with(ctx).load(recentAppointment.getUri()).resize(100,100).centerCrop().into(recentAppointmentViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return recentAppointmentList.size();
    }

    class RecentAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView barberShopName;
        TextView barberName;
        TextView barberAddress;
        ImageView image;

        OnRecyclerListener onRecyclerListener;

        public RecentAppointmentViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener){
            super(itemView);


            barberShopName = itemView.findViewById(R.id.barberShopName);
            barberName = itemView.findViewById(R.id.barberName);
            barberAddress = itemView.findViewById(R.id.theAddress);
            image = itemView.findViewById(R.id.image);
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

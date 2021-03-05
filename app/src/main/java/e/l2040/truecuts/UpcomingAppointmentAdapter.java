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

public class UpcomingAppointmentAdapter extends RecyclerView.Adapter<UpcomingAppointmentAdapter.UpcomingAppointmentViewHolder>{

    private Context ctx;
    private List<UpcomingAppointment> upcomingAppointmentList;
    private OnHorizontalRecyclerListener mOnHorizontalRecyclerListener;

    public UpcomingAppointmentAdapter(Context ctx, List<UpcomingAppointment> upcomingAppointmentList, OnHorizontalRecyclerListener onHorizontalRecyclerListener) {
        this.ctx = ctx;
        this.upcomingAppointmentList = upcomingAppointmentList;
        this.mOnHorizontalRecyclerListener = onHorizontalRecyclerListener;
    }

    @NonNull
    @Override
    public UpcomingAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.horizontal_layout,viewGroup, false);
        return new UpcomingAppointmentViewHolder(view, mOnHorizontalRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingAppointmentViewHolder upcomingAppointmentViewHolder, int position) {
        UpcomingAppointment upcomingAppointment = upcomingAppointmentList.get(position);

        upcomingAppointmentViewHolder.barberShopName.setText(upcomingAppointment.getName());
        upcomingAppointmentViewHolder.time.setText(upcomingAppointment.getTime());
        upcomingAppointmentViewHolder.date.setText(upcomingAppointment.getDate());
        Picasso.with(ctx).load(upcomingAppointment.getUri()).fit().centerInside().into(upcomingAppointmentViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return upcomingAppointmentList.size();
    }



    class UpcomingAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView barberShopName;
        TextView time;
        TextView date;
        ImageView image;

        OnHorizontalRecyclerListener onHorizontalRecyclerListener;

        public UpcomingAppointmentViewHolder(@NonNull View itemView, OnHorizontalRecyclerListener onHorizontalRecyclerListener) {
            super(itemView);

            barberShopName = itemView.findViewById(R.id.barberShopName);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            image = itemView.findViewById(R.id.image);
            this.onHorizontalRecyclerListener = onHorizontalRecyclerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onHorizontalRecyclerListener.onHorizontalRecyclerClick(getAdapterPosition());
        }
    }


    public interface OnHorizontalRecyclerListener{
        void onHorizontalRecyclerClick(int position);
    }
}

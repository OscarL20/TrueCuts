package e.l2040.truecuts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BarberUpcomingAppointmentAdapter extends RecyclerView.Adapter<BarberUpcomingAppointmentAdapter.BarberUpcomingAppointmentViewHolder>{

    private Context ctx;
    private List<UpcomingAppointment> upcomingAppointmentList;

    private OnRecyclerListener mOnRecyclerListener;

    public BarberUpcomingAppointmentAdapter(Context ctx, List<UpcomingAppointment> upcomingAppointmentList, OnRecyclerListener onRecyclerListener) {
        this.ctx = ctx;
        this.upcomingAppointmentList = upcomingAppointmentList;
        this.mOnRecyclerListener = onRecyclerListener;
    }

    @NonNull
    @Override
    public BarberUpcomingAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.upcoming_appointment_layout,viewGroup, false);
        return new BarberUpcomingAppointmentViewHolder(view, mOnRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberUpcomingAppointmentViewHolder barberUpcomingAppointmentViewHolder, int position) {
        UpcomingAppointment upcomingAppointment = upcomingAppointmentList.get(position);

        barberUpcomingAppointmentViewHolder.customerName.setText(upcomingAppointment.getName());
        barberUpcomingAppointmentViewHolder.date.setText(upcomingAppointment.getDate());
        barberUpcomingAppointmentViewHolder.time.setText(upcomingAppointment.getTime());
        Picasso.with(ctx).load(upcomingAppointment.getUri()).resize(100,100).centerCrop().into(barberUpcomingAppointmentViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return upcomingAppointmentList.size();
    }

    class BarberUpcomingAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView customerName;
        TextView date;
        TextView time;
        ImageView image;

        OnRecyclerListener onRecyclerListener;

        public BarberUpcomingAppointmentViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener){
            super(itemView);


            customerName = itemView.findViewById(R.id.customerName);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
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
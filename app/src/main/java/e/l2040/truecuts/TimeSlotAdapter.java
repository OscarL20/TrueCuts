package e.l2040.truecuts;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    int rowIndex;

    private Context ctx;
    private List<TimeSlot> timeSlotList;
    private OnHorizontalRecyclerListener mOnHorizontalRecyclerListener;

    public TimeSlotAdapter(Context ctx, List<TimeSlot> timeSlotList, OnHorizontalRecyclerListener onHorizontalRecyclerListener) {
        this.ctx = ctx;
        this.timeSlotList = timeSlotList;
        this.mOnHorizontalRecyclerListener = onHorizontalRecyclerListener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.times_layout,viewGroup, false);
        return new TimeSlotViewHolder(view, mOnHorizontalRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimeSlotViewHolder timeSlotViewHolder, final int position) {
        TimeSlot timeSlot = timeSlotList.get(position);

        timeSlotViewHolder.time.setText(timeSlot.getTime());

        if (rowIndex == position){
            timeSlotViewHolder.time.setTextColor(Color.RED);
        }
        else{
            timeSlotViewHolder.time.setTextColor(Color.GRAY);
        }

    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView time;

        OnHorizontalRecyclerListener onHorizontalRecyclerListener;

        public TimeSlotViewHolder(@NonNull View itemView, OnHorizontalRecyclerListener onHorizontalRecyclerListener) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            this.onHorizontalRecyclerListener = onHorizontalRecyclerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onHorizontalRecyclerListener.onHorizontalRecyclerClick(getAdapterPosition());

            rowIndex = getAdapterPosition();
            notifyDataSetChanged();
        }
    }


    public interface OnHorizontalRecyclerListener{
        void onHorizontalRecyclerClick(int position);


    }

}

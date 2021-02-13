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

public class ChangeBarberShopAdapter extends RecyclerView.Adapter<ChangeBarberShopAdapter.ChangeBarberShopViewHolder>{

    private Context ctx;
    private List<BarberShop> barberShopList;

    private OnRecyclerListener mOnRecyclerListener;

    public ChangeBarberShopAdapter(Context ctx, List<BarberShop> barberShopList, OnRecyclerListener onRecyclerListener) {
        this.ctx = ctx;
        this.barberShopList = barberShopList;
        this.mOnRecyclerListener = onRecyclerListener;
    }

    @NonNull
    @Override
    public ChangeBarberShopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.barbershop,viewGroup, false);
        return new ChangeBarberShopViewHolder(view, mOnRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeBarberShopViewHolder changeBarberShopViewHolder, int position) {
        BarberShop barberShop = barberShopList.get(position);

        changeBarberShopViewHolder.barberShopName.setText(barberShop.getBarberShopName());
        changeBarberShopViewHolder.address.setText(barberShop.getAddress());
    }

    @Override
    public int getItemCount() {
        return barberShopList.size();
    }

    class ChangeBarberShopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView barberShopName;
        TextView address;

        OnRecyclerListener onRecyclerListener;

        public ChangeBarberShopViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener){
            super(itemView);


            barberShopName = itemView.findViewById(R.id.barberShopName);
            address = itemView.findViewById(R.id.theAddress);
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
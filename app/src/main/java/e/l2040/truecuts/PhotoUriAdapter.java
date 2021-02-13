package e.l2040.truecuts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoUriAdapter extends RecyclerView.Adapter<PhotoUriAdapter.PhotoViewHolder>{

    private Context ctx;
    private List<PhotoUri> photoList;

    public PhotoUriAdapter(Context ctx, List<PhotoUri> photoList) {
        this.ctx = ctx;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.photo_layout,viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int position) {
        PhotoUri photoUri = photoList.get(position);

        Picasso.with(ctx).load(photoUri.getUri()).into(photoViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }



    class PhotoViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imagePhoto);
        }
    }
}
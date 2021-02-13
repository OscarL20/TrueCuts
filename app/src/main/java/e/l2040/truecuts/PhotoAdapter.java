package e.l2040.truecuts;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

    private Context ctx;
    private List<Photo> photoList;

    public PhotoAdapter(Context ctx, List<Photo> photoList) {
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
        Photo photo = photoList.get(position);

        photoViewHolder.image.setBackground(ctx.getResources().getDrawable(photo.getImage()));

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
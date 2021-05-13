package ca.uwaterloo.cs349;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StrokeAdapter extends
        RecyclerView.Adapter<StrokeAdapter.ViewHolder>{

    private ArrayList<OneStroke> strokes;
    private OnItemListener onItemListener;

    public StrokeAdapter(OnItemListener onItemListener) {
        strokes = SharedViewModel.savedStrokes;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stroke_item_view, parent, false);

        return new ViewHolder(view,onItemListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final OneStroke stroke = strokes.get(position);

        holder.strokeName.setText(stroke.strokeName);
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, strokes.size());
            }
        });
        holder.strokeImage.setImageBitmap(stroke.bitmap);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return strokes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView strokeName;
        public Button delButton;
        public ImageView strokeImage;
        public Button editButton;
        public OnItemListener onItemListener;

        public ViewHolder(View itemView, final OnItemListener onItemListener) {
            super(itemView);

            strokeName = (TextView) itemView.findViewById(R.id.stroke_name);
            delButton = (Button) itemView.findViewById(R.id.stroke_delete);
            strokeImage =  (ImageView) itemView.findViewById(R.id.stroke_image);
            editButton = (Button) itemView.findViewById(R.id.edit_button);
            this.onItemListener = onItemListener;
        }

    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}

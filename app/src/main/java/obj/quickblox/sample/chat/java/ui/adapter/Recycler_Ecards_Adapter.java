package obj.quickblox.sample.chat.java.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Model.CardsModel;
import obj.quickblox.sample.chat.java.ui.activity.EcardsWebView;
import obj.quickblox.sample.chat.java.ui.fragments.EcardsHomePage;

public class Recycler_Ecards_Adapter extends RecyclerView.Adapter<Recycler_Ecards_Adapter.MyViewHolder> {

    private ArrayList<CardsModel> horizontalList;
    private Context context;
    private RecyclerView recyler_view;
//    private View itemView;


    public Recycler_Ecards_Adapter(Context context, ArrayList<CardsModel> horizontalList, RecyclerView recyclerView) {
        this.horizontalList = horizontalList;
        this.context = context;
        recyler_view = recyclerView;
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iamge;

        public MyViewHolder(View view) {
            super(view);
            iamge = (ImageView) view.findViewById(R.id.pagerImg1);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecards_pager, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        int lastPosition = -1;
        Animation animateIn = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.view_recycler : R.anim.view_recycler);
        holder.itemView.startAnimation(animateIn);
        lastPosition = position;
        try {
            Picasso.get().load(horizontalList.get(position).getCards_images()).into(holder.iamge);
//            setAnimation(holder.itemView , position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animateIn = AnimationUtils.loadAnimation(context, R.anim.zoom_cards);
                holder.itemView.startAnimation(animateIn);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(context, EcardsWebView.class);
                        i.putExtra("type" , "simple");
                        i.putExtra("category", horizontalList.get(position).getCategories());
                        i.putExtra("card_id", horizontalList.get(position).getId());
                        context.startActivity(i);

                    }
                }, 500);
                Toast.makeText(context, "" + context.getString(R.string.ecards_msg), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
//    @Override
//    public void onViewDetachedFromWindow(final MyViewHolder holder)
//    {
//        ((MyViewHolder)holder).itemView.clearAnimation();
//    }

}

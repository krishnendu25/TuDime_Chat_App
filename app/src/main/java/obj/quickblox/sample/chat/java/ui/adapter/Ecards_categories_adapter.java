package obj.quickblox.sample.chat.java.ui.adapter;

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

/**
 * Created by mahak on 7/15/2017.
 */

public class Ecards_categories_adapter extends RecyclerView.Adapter<Ecards_categories_adapter.MyViewHolder> {

    private ArrayList<CardsModel> horizontalList;
    private Context context;
    private int lastPosition = -1;
    private RecyclerView recyler_view;
    private View itemView;
    private int item_position;
//    private View itemView;


    public Ecards_categories_adapter(Context context, ArrayList<CardsModel> horizontalList, RecyclerView recyclerView) {
        this.horizontalList = horizontalList;
        this.context = context;
        recyler_view = recyclerView;
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public int getItem_position(){
        return item_position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iamge;

        public MyViewHolder(View view) {
            super(view);
            iamge = (ImageView) view.findViewById(R.id.pagerImg1);
        }
    }

    @Override
    public Ecards_categories_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecards_pager_category, parent, false);
        return new Ecards_categories_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Ecards_categories_adapter.MyViewHolder holder, final int position) {
        item_position = position;
        int lastPosition = -1;
        Animation animateIn = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.horizontal_recyclar_view_anim : R.anim.horizontal_recyclar_view_anim);
        holder.itemView.startAnimation(animateIn);
        try {
            Picasso.get().load(horizontalList.get(position).getCards_images()).into(holder.iamge);
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
                        Intent i = new Intent(context , EcardsWebView.class);
                        i.putExtra("type" , "simple");
                        i.putExtra("category" , horizontalList.get(position).getCategories() );
                        i.putExtra("card_id" , horizontalList.get(position).getId() );
                        context.startActivity(i);
//                        context.startActivity(new Intent(context , EcardsWebView.class));
                    }
                },500);
                Toast.makeText(context, "" + context.getString(R.string.ecards_msg), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

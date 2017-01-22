package com.arpaul.pinterest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arpaul.filedownloader.PinDownloader;
import com.arpaul.pinterest.MainActivity;
import com.arpaul.pinterest.R;
import com.arpaul.pinterest.common.AppConstants;
import com.arpaul.pinterest.dataobject.ProfileDO;
import com.arpaul.utilitieslib.ColorUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARPaul on 22-01-2017.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<ProfileDO> arrProfileDO = new ArrayList<>();

    public ProfileAdapter(Context context, List<ProfileDO> arrDashboard) {
        this.context = context;
        this.arrProfileDO = arrDashboard;
    }

    public void refresh(List<ProfileDO> arrFarms) {
        this.arrProfileDO = arrFarms;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProfileDO objProfileDO = arrProfileDO.get(position);

        holder.tvProfile.setText(objProfileDO.UserName);

        if(!TextUtils.isEmpty(objProfileDO.ProfileImage))
            Picasso.with(context).load(objProfileDO.ProfileImage).into(holder.ivProfileImage);

        Picasso.with(context).load(objProfileDO.UserImage).into(holder.ivImage);
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MainActivity)
                    ((MainActivity) context).downloadImage(objProfileDO);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(arrProfileDO != null)
            return arrProfileDO.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView tvProfile;
        public final ImageView ivImage;
        public final ImageView ivProfileImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvProfile           = (TextView) view.findViewById(R.id.tvProfile);
            ivImage             = (ImageView) view.findViewById(R.id.ivImage);
            ivProfileImage      = (ImageView) view.findViewById(R.id.ivProfileImage);
        }

        @Override
        public String toString() {
            return "";
        }
    }
}

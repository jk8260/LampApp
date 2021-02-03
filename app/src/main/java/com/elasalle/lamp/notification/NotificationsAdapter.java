package com.elasalle.lamp.notification;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.notification.Notification;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends ArrayAdapter<Notification> {

    private final List<Notification> notificationList;
    private DateFormat dateFormat;
    private boolean isSelectionMode;

    public NotificationsAdapter(Activity activity, List<Notification> notificationList) {
        super(activity, R.layout.notification_item, notificationList);
        this.notificationList = notificationList;
        this.dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notification notification = notificationList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(notification.title);
        try {
            Date date = DateUtils.parseDate(notification.createdDate, "yyyy-MM-dd'T'HH:mm:ss'Z'");
            holder.date.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText(notification.createdDate);
        }
        if (isSelectionMode) {
            if (DisplayHelper.isTablet() && holder.container != null) {
                if(holder.isSelected) {
                    holder.container.setBackground(ResourcesUtil.getDrawable(R.drawable.rectangle_background_selected, null));
                } else {
                    holder.container.setBackground(ResourcesUtil.getDrawable(R.drawable.rectangle_background, null));
                }
            } else {
                if(holder.isSelected) {
                    holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.grey2, null));
                } else {
                    holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.grey1, null));
                }
            }
        } else {
            if(notification.read) {
                holder.unreadIcon.setVisibility(View.INVISIBLE);
                if (DisplayHelper.isTablet()) {
                    if (holder.container != null) {
                        holder.container.setBackground(ResourcesUtil.getDrawable(R.drawable.rectangle_background, null));
                    }
                } else {
                    holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.white, null));
                }
            } else {
                holder.unreadIcon.setVisibility(View.VISIBLE);
                if (DisplayHelper.isTablet()) {
                    if (holder.container != null) {
                        holder.container.setBackground(ResourcesUtil.getDrawable(R.drawable.rectangle_background_unread, null));
                    }
                } else {
                    holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.grey1, null));
                }
            }
        }
        return convertView;
    }

    public void setSelectionMode(final boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
    }

    class ViewHolder {

        @BindView(R.id.unread) ImageView unreadIcon;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.date) TextView date;
        @Nullable @BindView(R.id.container) ViewGroup container;
        View itemView;
        boolean isSelected;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}

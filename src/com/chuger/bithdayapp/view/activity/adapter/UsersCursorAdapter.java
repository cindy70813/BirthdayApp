package com.chuger.bithdayapp.view.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuger.R;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 13:01
 */
public class UsersCursorAdapter extends CursorAdapter {
    private final UserInfoLoader userLoader;

    public UsersCursorAdapter(final Activity context, final Cursor cursor) {
        super(context, cursor);
        userLoader = new UserInfoLoader(context);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        final View view = LayoutInflater.from(context).inflate(R.layout.row_layout, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) view.findViewById(R.id.icon);
        viewHolder.userInfo = (TextView) view.findViewById(R.id.userInfo);
        viewHolder.birthday = (TextView) view.findViewById(R.id.birthday);
        viewHolder.dayCount = (TextView) view.findViewById(R.id.dayCount);
        viewHolder.yearCount = (TextView) view.findViewById(R.id.yearCount);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        userLoader.displayUser(viewHolder, cursor);
    }
}

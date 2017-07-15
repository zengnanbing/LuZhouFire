package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import JavaBean.CheckInfo;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<CheckInfo.ResultBean> result;

    public CustomExpandableListAdapter(Context context, List<CheckInfo.ResultBean> result) {
        this.context = context;
        this.result = result;
    }

    @Override
    public CheckInfo.ResultBean.ChildrenBean getChild(int listPosition, int expandedListPosition) {
        return result.get(listPosition).
                getChildren().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        CheckInfo.ResultBean.ChildrenBean child = getChild(listPosition, expandedListPosition);
        String name = child.getTitle();
        int day = child.getDay();
        String state = child.getState();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.fragment2_list_child, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        TextView expandedListDay = (TextView) convertView.findViewById(R.id.expandedListItemData);
        expandedListDay.setText("" + day + "天");
        expandedListTextView.setText(name);
        if ("01".equals(state)) {
            expandedListTextView.setTextColor(context.getResources().getColor(R.color.text_child));
            expandedListDay.setVisibility(View.GONE);
        } else {
            expandedListTextView.setTextColor(Color.RED);
            if (day >= 0) {
                expandedListDay.setVisibility(View.VISIBLE);
                expandedListDay.setTextColor(context.getResources().getColor(R.color.text_child));
            } else {
                expandedListDay.setVisibility(View.VISIBLE);
                expandedListDay.setTextColor(Color.RED);
            }
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return result.get(listPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return result.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return result.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = result.get(listPosition).getType();
        int childCount = getCount(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.fragment2_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        TextView listTitleChildCount = (TextView) convertView.findViewById(R.id.item_count);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        listTitleChildCount.setText(childCount + "");
        return convertView;
    }

    //得到未检查店铺数量，重新排序
    public int getCount(int listPosition) {

        List<CheckInfo.ResultBean.ChildrenBean> children = result.get(listPosition).getChildren();
        List<CheckInfo.ResultBean.ChildrenBean> children2 = new ArrayList<>();
        int count = children.size();
        for (int j = children.size() - 1; j >= 0; j--) {
            CheckInfo.ResultBean.ChildrenBean bean = children.get(j);
            String state = bean.getState();
            if ("01".equals(state)) {
                count--;
                children2.add(children.get(j));
                children.remove(j);
            }
        }
        children.addAll(children2);
        return count;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
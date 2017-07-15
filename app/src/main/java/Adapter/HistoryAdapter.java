package Adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myapplication.R;

import java.util.List;

import JavaBean.HistoryInfo;

/**
 * @Description:
 * @author: cyq7on
 * @date: 2016/8/1 10:41
 * @version: V1.0
 */
public class HistoryAdapter extends BaseQuickAdapter<HistoryInfo.ResultBean.CheckBean> {

    public HistoryAdapter(List<HistoryInfo.ResultBean.CheckBean> data) {
        super(R.layout.item_history,data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, HistoryInfo.ResultBean.CheckBean checkBean) {
        baseViewHolder.setText(R.id.tvDate, "提交时间：" + checkBean.getCheckdate());
    }
}

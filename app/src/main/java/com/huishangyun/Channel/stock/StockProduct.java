package com.huishangyun.Channel.stock;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.StockProductAdapter;
import com.huishangyun.model.StockBean;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 选择产品界面（xsl：这个界面不需要了，统一到了客户选择界面）
 * 
 * @author 熊文娟
 * 
 */
public class StockProduct extends BaseActivity {

	private LinearLayout backLayout;//返回
	private ListView listView;
	private List<StockBean> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_product);
		initView();
		list= new ArrayList<StockBean>();
		StockProductAdapter adapter=new StockProductAdapter(this, list);
		listView.setAdapter(adapter);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		// 获取资源
		backLayout = (LinearLayout) findViewById(R.id.root_back);
		listView=(ListView) findViewById(R.id.product_list);
		// 添加事件
		backLayout.setOnClickListener(new myOnClickListener());
	}

	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.root_back:// 返回
				finish();
				break;

			default:
				break;
			}

		}

	}
}

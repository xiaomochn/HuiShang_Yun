package com.huishangyun.Channel.Plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.ClassModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Departments;
import com.huishangyun.model.Managers;
import com.huishangyun.model.MemberGroups;
import com.huishangyun.model.Members;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.yun.R;

public class CustomersListActivity extends BaseActivity {
	protected static final String TAG = null;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private EditText mClearEditText;
	int count = 0;// 记录选中个数
	private TextView confirm;// 确定显示数量
	private LinearLayout sure;// 确定
	private LinearLayout sure_bar;//确定条
    private String groupName;//组名
    private String Code;
    private String Tittle;
    private TextView title;//标题
    private int select=1;//选择单选还是多选列表
    private int isWrite = 0;//判断是否进行了搜索
    private LinearLayout nodata;//搜索无数据
    
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<SortModel> dataList = new ArrayList<SortModel>();
	private List<SortModel> SearchList = new ArrayList<SortModel>();
	public static List<SortModel> Img_List = new ArrayList<SortModel>();
	public static ArrayList<Integer> List = new ArrayList<Integer>();

	public static ArrayList<String> list_id = new ArrayList<String>();
	public static List<Integer> list_position = new ArrayList<Integer>();
	Members aList = new Members();//全部数据列表
	//客户
	List<Members> mList = new ArrayList<Members>();	
	List<MemberGroups> mGroups = new ArrayList<MemberGroups>();
	//人员
	List<Managers> MangersList = new ArrayList<Managers>();
	List<Departments> DepartmentList = new ArrayList<Departments>();
	//产品 
	List<ClassModel> Com_Class = new ArrayList<ClassModel>();
	List<Order_GoodsList>  Com_Product = new ArrayList<Order_GoodsList>();
	private List<PlanList> Tlist = new ArrayList<PlanList>();

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private HorizontalListView horizon_listview;// 横向listview
	private HorizontalListViewAdapter hListViewAdapter;

	private RelativeLayout back;// 返回键
	private boolean haveType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_customerslist);
		selectMode();
		//清除一次不保存
		Img_List.clear();
		list_id.clear();
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			haveType = false;
		} else {
			haveType = true;
		}
		
		if (Code.equals("1")) {
			getData(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0), false, Code, haveType);
		} else {
			getData(0, false, Code, haveType);
		}
		initViews();
		title.setText(Tittle);
		setResult(RESULT_CANCELED);
		
		if (Code.equals("0")&& select == 0) {
			list_id.clear();
			Img_List.clear();
			for (int i = 0; i < Tlist.size(); i++) {
				list_id.add(Tlist.get(i).getMember_ID()+"");
			}
			
			for (int i = 0; i < list_id.size(); i++) {
				SortModel sortModel = new SortModel();
				//获得客户信息
				getPerson(Integer.parseInt(list_id.get(i)));								
						sortModel.setPerson_img(aList.getPhoto());
						sortModel.setCompany_name(aList.getRealName());
						sortModel.setManger_Name(aList.getManager_Name());	
						L.i("图片" + aList.getPhoto());
					sortModel.setID(Integer.parseInt(list_id.get(i)));
					Img_List.add(sortModel);						
			}
			hListViewAdapter.notifyDataSetChanged();
			adapter.notifyDataSetChanged();
			confirm.setText("确定" + "( " + list_id.size() + " )");
			
		}
	}

	/**
	 * 实例化组件
	 */
	private void initViews() {
		back = (RelativeLayout) findViewById(R.id.back);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		title = (TextView) findViewById(R.id.title);
		pinyinComparator = new PinyinComparator();
		confirm = (TextView) findViewById(R.id.queding);
		nodata = (LinearLayout) findViewById(R.id.nodata);

		mClearEditText = (EditText) findViewById(R.id.filter_edit);
		
		if (Code.equals("0")) {
			mClearEditText.setHint("请输入客户名称");
		}else if (Code.equals("1")) {
			mClearEditText.setHint("请输入人员名称");
		}else if (Code.equals("2")) {
			mClearEditText.setHint("请输入产品名称");
		}else if (Code.equals("3")) {
			mClearEditText.setHint("请输入经销商名称");
		}

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		sure = (LinearLayout) findViewById(R.id.sure);
		horizon_listview = (HorizontalListView) findViewById(R.id.horizon_listview);
		hListViewAdapter = new HorizontalListViewAdapter(
				CustomersListActivity.this, Img_List);
		horizon_listview.setAdapter(hListViewAdapter);
		//确认按钮
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    //数据是使用Intent返回
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
//                bundle.putStringArrayList("result", list_id);
                bundle.putSerializable("result", (Serializable) Img_List);
                //把返回数据存入Intent
                intent.putExtra("bundle", bundle);
                //设置返回数据码
                CustomersListActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                CustomersListActivity.this.finish();

			
			}
		});

		// 确认单击事件
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		// 返回单击事件
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);// 跳转到相应位置
				}

			}
		});

		// 图片浏览单击跳转
		horizon_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				sortListView.setSelection(Integer.parseInt(list_id.get(position)));// 跳转到相应位置
			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		
		if (Code.equals("0")||Code.equals("3")) {//客户or上级经销商
			SourceDateList = filledData(mList);
			dataList = fillegroupsdData(mGroups);
		}else if (Code.equals("1")) {//人员
			SourceDateList = filledData1(MangersList);
			dataList = fillegroupsdData1(DepartmentList);
		}else if (Code.equals("2")) {//产品
			SourceDateList = filledData2(Com_Product);
			dataList = fillegroupsdData2(Com_Class);
		
		}

		

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);

		for (int j = 0; j < SourceDateList.size(); j++) {
			dataList.add(SourceDateList.get(j));
		}

		adapter = new SortAdapter(this, dataList, Code);
		sortListView.setAdapter(adapter);

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 组点击，做另外的操作
				if ((dataList.get(position).getSortLetters()).equals(groupName)) {
					// 点击组进入组员列表(更新listview)
					L.i("id:" + dataList.get(position).getID());
					List.clear();
					List.add(dataList.get(position).getParentID());
					getData(dataList.get(position).getID(), false, Code, haveType);
					changeListView();
			
				} else {
					
					if (Code.equals("0") || Code.equals("3")) {
						// 取id作为唯一标识
						if (list_id.contains(dataList.get(position).getID()+"")) {
							// 删除集合里面某一数据的方法，list_id.remove()是删除里面某个下标的方法;
							Iterator<String> iterator = list_id.iterator();
							while (iterator.hasNext()) {
								String e = iterator.next();
								if (e.equals(dataList.get(position).getID()+"")) {
									iterator.remove();
								}
							}

						} else {
							list_id.add(dataList.get(position).getID()+"");
						}

						// 单击一次，清除一次列表，
						Img_List.clear();
						for (int i = 0; i < list_id.size(); i++) {
							SortModel sortModel = new SortModel();
							//获得客户信息
							getPerson(Integer.parseInt(list_id.get(i)));								
									sortModel.setPerson_img(aList.getPhoto());
									sortModel.setCompany_name(aList.getRealName());
									sortModel.setManger_Name(aList.getManager_Name());	
									L.e("图片" + aList.getPhoto());
								sortModel.setID(Integer.parseInt(list_id.get(i)));
								Img_List.add(sortModel);						
						}
						hListViewAdapter.notifyDataSetChanged();
						adapter.notifyDataSetChanged();
						confirm.setText("确定" + "( " + list_id.size() + " )");
						if (select==1) {
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putSerializable("result",
									(Serializable) Img_List);
							// 把返回数据存入Intent
							intent.putExtra("bundle", bundle);
							// 设置返回数据
							CustomersListActivity.this.setResult(RESULT_OK, intent);
							// 关闭Activity
							CustomersListActivity.this.finish();
						}else {
							//不做任何操作
						}
						
					}else if (Code.equals("1")) {
						
						Img_List.clear();
						SortModel sortModel = new SortModel();
						sortModel.setPerson_img(dataList.get(position)
								.getPerson_img());
						sortModel.setCompany_name(dataList.get(position)
								.getName());
						sortModel.setManger_Name(dataList.get(position)
								.getManger_Name());
						sortModel.setID(dataList.get(position).getID());
						sortModel.setGroup_ID(dataList.get(position).getGroup_ID());
						sortModel.setMobile(dataList.get(position).getMobile().trim());
						sortModel.setOFUserName(dataList.get(position).getOFUserName());
						Img_List.add(sortModel);
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("result",
								(Serializable) Img_List);
						// 把返回数据存入Intent
						intent.putExtra("bundle", bundle);
						// 设置返回数据
						CustomersListActivity.this.setResult(RESULT_OK, intent);
						// 关闭Activity
						CustomersListActivity.this.finish();
						
					}else if (Code.equals("2")) {
						
						SortModel sortModel = new SortModel();
						sortModel.setPerson_img(dataList.get(position)
								.getPerson_img());
						sortModel.setCompany_name(dataList.get(position)
								.getName());
						sortModel.setManger_Name(dataList.get(position)
								.getManger_Name());
						sortModel.setID(dataList.get(position).getID());
//						Log.e(TAG, "id----------:" + dataList.get(position).getID());
						sortModel.setProduct_Unit_Name(dataList.get(position).getProduct_Unit_Name());
						sortModel.setProduct_Unit_ID(dataList.get(position).getProduct_Unit_ID());
						Img_List.add(sortModel);
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("result",
								(Serializable) Img_List);
						// 把返回数据存入Intent
						intent.putExtra("bundle", bundle);
						// 设置返回数据
						CustomersListActivity.this.setResult(RESULT_OK, intent);
						// 关闭Activity
						CustomersListActivity.this.finish();
					}
					
				}

			}
		});

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
//				SearchList = dataList;
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				isWrite = 1;
//				filterData(s.toString());
				
				if (s.toString().trim().length()==0) {
					nodata.setVisibility(View.GONE);
					dataList.clear();
					if (Code.equals("1")) {
//						dataList.clear();
						getData(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0), false, Code, haveType);
						changeListView();
					} else {
						if (Code.equals("2")) {
							Com_Class.clear();
							Com_Product.clear();
							getData(0, false, Code, haveType);
							changeListView();
						}else {
							
							getData(0, false, Code, haveType);
							changeListView();
						}
						
					}
				}else {
					searchGetData(s.toString(), 1);
					
				}
			}
		});
	}

	/**
	 * 为ListView填充数据
	 *
	 * @return
	 */
	private List<SortModel> fillegroupsdData(List<MemberGroups> mGroups2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < mGroups2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(mGroups2.get(i).getName().trim());
			sortModel.setParentID(mGroups2.get(i).getParentID());
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			// 组不取id
			sortModel.setID(mGroups2.get(i).getID());
			sortModel.setSortLetters(groupName);
//			sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			sortModel.setPerson_img("0");
			mSortList.add(sortModel);
		}

		return mSortList;

	}
	
	/**
	 * 为ListView填充数据
	 * 人员部门
	 * @paramdate
	 * @return
	 */
	private List<SortModel> fillegroupsdData1(java.util.List<Departments> departmentList2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < departmentList2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(departmentList2.get(i).getName().trim());
			sortModel.setParentID(departmentList2.get(i).getParentID());
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			sortModel.setID(departmentList2.get(i).getID());
			sortModel.setSortLetters(groupName);
//			sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			sortModel.setPerson_img("0");
			mSortList.add(sortModel);
		}

		return mSortList;

	}
	
	/**
	 * 为ListView填充数据
	 * 
	 * @param com_Class2  产品分类
	 * @return
	 */
	private List<SortModel> fillegroupsdData2(java.util.List<ClassModel> com_Class2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < com_Class2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(com_Class2.get(i).getName().trim());
			sortModel.setParentID(com_Class2.get(i).getParentID());
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			sortModel.setID(com_Class2.get(i).getID());
			sortModel.setSortLetters(groupName);
			sortModel.setGroup_ID("");
//			sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			sortModel.setPerson_img("0");
			mSortList.add(sortModel);
		}

		return mSortList;

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param mList2
	 * @return
	 */
	private List<SortModel> filledData(List<Members> mList2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < mList2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(mList2.get(i).getRealName().trim());//公司名称
			sortModel.setPerson_img(mList2.get(i).getPhoto() != null ? mList2.get(i).getPhoto().trim() : "");//客户头像
			sortModel.setID(mList2.get(i).getID());
			sortModel.setGroup_ID(mList2.get(i).getGroup_ID());//组id
			sortModel.setManger_Name(mList2.get(i).getManager_Name().trim());//责任人或者联系人
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			// sortModel.setName(mList2[i]);
//			 sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(mList2.get(i)
					.getRealName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	
	/**
	 * 为ListView填充数据
	 * 
	 * @param mangersList2
	 * @return
	 */
	private List<SortModel> filledData1(java.util.List<Managers> mangersList2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < mangersList2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(mangersList2.get(i).getRealName().trim());//人员名称
			sortModel.setPerson_img(mangersList2.get(i).getPhoto().trim());//人员头像
			sortModel.setID(mangersList2.get(i).getID());
			sortModel.setGroup_ID(mangersList2.get(i).getDepartment_ID());//部门id
			sortModel.setManger_Name(mangersList2.get(i).getRole_Name() == null ? "": mangersList2.get(i).getRole_Name().trim());//职位名称
			sortModel.setDepartment_Name(mangersList2.get(i).getDepartment_Name().trim());//部门名称
			sortModel.setNote(mangersList2.get(i).getNote() != null ? mangersList2.get(i).getNote().trim() : "");//动态
			sortModel.setMobile(mangersList2.get(i).getMobile());
			sortModel.setOFUserName(mangersList2.get(i).getOFUserName());
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			// sortModel.setName(mList2[i]);
			// sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(mangersList2.get(i)
					.getRealName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	
	/**
	 * 为ListView填充数据
	 * 
	 * @param com_Product2 产品
	 * @return
	 */
	private List<SortModel> filledData2(java.util.List<Order_GoodsList> com_Product2) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < com_Product2.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(com_Product2.get(i).getName());//产品名称
			sortModel.setPerson_img(com_Product2.get(i).getSmallImg());//产品图片
			sortModel.setID(com_Product2.get(i).getID());//产品id
			sortModel.setGroup_ID(com_Product2.get(i).getClass_ID()+"");//组id
			sortModel.setProduct_Unit_ID(com_Product2.get(i).getUnit_ID());//产品单位id
			sortModel.setProduct_Unit_Name(com_Product2.get(i).getUnit_Name());	//产品单位
			sortModel.setCode(Code);
			sortModel.setSelect(select);
			// sortModel.setName(mList2[i]);
			// sortModel.setPerson_img("http://www.baidu.com/img/bd_logo.png");
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(com_Product2.get(i)
					.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		SearchList.clear();
		SearchList = dataList;
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (filterStr.equals("")) {
			filterDateList = SearchList;
			adapter.updateListView(filterDateList);
		} else {
			filterDateList.clear();
			for (SortModel sortModel : dataList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
			
			// 根据a-z进行排序
			Collections.sort(filterDateList, pinyinComparator);
			dataList = filterDateList;
			adapter.updateListView(filterDateList);

		}

	}

	/**
	 * 读取本地数据库数据
	 * 
	 * @param ID
	 *            传入group ID
	 * @param isParent
	 *            false为查询本级数据，true返回对应id数据列表
	 */
	private void getData(int ID, boolean isParent, String Code, boolean haveType) {
		dataList.clear();
		if (Code.equals("0")) {
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				mList = MemberManager.getInstance(this).getMembersForManager(ID, isParent,
						Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
			} else {
				mList = MemberManager.getInstance(this).getMembersForDepartment(ID, isParent, 
						preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
			}
			
			mGroups = MemberManager.getInstance(this).getGroups(ID, isParent);
			
		}else if (Code.equals("1")) {
			DepartmentList.clear();
			MangersList.clear();
			if (haveType) {
				DepartmentList = DepartmentManager.getInstance(this).getDepartments(ID, isParent);
				MangersList = DepartmentManager.getInstance(this).getmManagers(ID, isParent);
			} else {
				DepartmentList = new ArrayList<Departments>();
				MangersList.add(DepartmentManager.getInstance(this).getManagerInfo(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"))));
			}
			
		}else if (Code.equals("2")) {
			//产品分类
			Com_Class = ProductManager.getInstance(this).getClassModels(ID, isParent);
			if (Com_Class == null || Com_Class.size()<=0) {
				//产品列表
				Com_Product = ProductManager.getInstance(this).getProducts(ID);
				Log.e(TAG, "======>" + Com_Product.size());
				for (int i = 0; i < Com_Product.size(); i++) {
					Log.e(TAG, "======>" + Com_Product.get(i).getUnit_ID());
				}
			}
		}else if (Code.equals("3")) {
			//获取上级经销商
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				mList = MemberManager.getInstance(this).getTopMembersForDepartment(ID, isParent, 
						preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
				L.e("mList111:" + mList.size());
			} else {
				mList = MemberManager.getInstance(this).getTopMemberForManager(ID,isParent,
						Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
				L.e("mList22222:" + mList.size());
			}
			   mGroups = MemberManager.getInstance(this).getGroups(ID, isParent);
		}

	}
	
	/**
	 * 搜索功能
	 * @param keywords 关键字
	 * @param pageIndex 页码
	 */
	private void searchGetData(String keywords,  int pageIndex){
		
		// 用户编号
		int manger_id = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,"0"));
		// 所属部门id
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		
		if (Code.equals("0")||Code.equals("3")) {
			try {
			
				if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
					mList = MemberManager.getInstance(this).searchMemberForMID(keywords, manger_id, pageIndex);
					
				}else {
					
					mList = MemberManager.getInstance(this).searchMemberForDID(keywords, department_id, pageIndex);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				L.e("输入了特殊字符等原因查询数据库报错MX4会出现这状况");
			}
			dataList.clear();
			SourceDateList = filledData(mList);
			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);

			for (int j = 0; j < SourceDateList.size(); j++) {
				dataList.add(SourceDateList.get(j));
			}
			Log.e(TAG, "mList.s" + mList.size());
			if (dataList.size()<=0) {
				nodata.setVisibility(View.VISIBLE);
			}else {
				nodata.setVisibility(View.GONE);
			}
			adapter.updateListView(dataList);
			
		}else if (Code.equals("1")) {
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				//权限低，不搜索，就显示他自己
			}else {
				try {	
			     MangersList = DepartmentManager.getInstance(this).searchManagers(keywords, department_id);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					L.e("输入了特殊字符等原因查询数据库报错MX4会出现这状况");
				}
			  dataList.clear();
				SourceDateList = filledData1(MangersList);
				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);

				for (int j = 0; j < SourceDateList.size(); j++) {
					dataList.add(SourceDateList.get(j));
				}
				Log.e(TAG, "mList.s" + mList.size());
				if (dataList.size()<=0) {
					nodata.setVisibility(View.VISIBLE);
				}else {
					nodata.setVisibility(View.GONE);
				}
				adapter.updateListView(dataList);
			}
			
			
			
		}else if (Code.equals("2")) {
			try {
				Com_Product = ProductManager.getInstance(this).searchGoodsLists(keywords);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				L.e("输入了特殊字符等原因查询数据库报错MX4会出现这状况");
			}
			dataList.clear();
			SourceDateList = filledData2(Com_Product);
			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);

			for (int j = 0; j < SourceDateList.size(); j++) {
				dataList.add(SourceDateList.get(j));
			}
			Log.e(TAG, "mList.s" + mList.size());
			if (dataList.size()<=0) {
				nodata.setVisibility(View.VISIBLE);
			}else {
				nodata.setVisibility(View.GONE);
			}
			adapter.updateListView(dataList);
		}
	}

	/**
	 * 获取个人信息
	 * 
	 * @param ID
	 */
	private void getPerson(int ID) {
		aList = MemberManager.getInstance(this).getMembers(ID);
	}
	
	/**
	 * 点击分组数据listview数据改变
	 */
	private void changeListView() {
		if (Code.equals("0")||Code.equals("3")) {
			SourceDateList = filledData(mList);
			dataList = fillegroupsdData(mGroups);
		}else if (Code.equals("1")) {
			SourceDateList = filledData1(MangersList);
			dataList = fillegroupsdData1(DepartmentList);
		}else if (Code.equals("2")) {
			SourceDateList = filledData2(Com_Product);
			dataList = fillegroupsdData2(Com_Class);
		}
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);

		for (int j = 0; j < SourceDateList.size(); j++) {
			dataList.add(SourceDateList.get(j));
		}
		adapter = new SortAdapter(this, dataList, Code);
		sortListView.setAdapter(adapter);
	}

	/**
	 * 返回键重写
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		int keyCode;
		switch (Integer.parseInt(Code)) {
		case 0:// 客户
			if (isWrite == 0) {
				if (mGroups.size() > 0) {
					keyCode = dataList.get(0).getParentID();
					if (keyCode == 0) {
						finish();
					} else {
						getData(keyCode, true, Code, haveType);
						changeListView();
					}

				} else {
					if (List.size() > 0) {
						keyCode = List.get(0);
						getData(keyCode, false, Code, haveType);
						changeListView();
					} else {
						finish();
					}

				}
			} else {// 搜索后按返回键
//				dataList.clear();
//				getData(0, true, Code, haveType);
//				changeListView();
				mClearEditText.setText("");
				isWrite = 0;
			}
			break;
		case 3:// 客户
			if (isWrite == 0) {
				if (mGroups.size() > 0) {
					keyCode = dataList.get(0).getParentID();
					if (keyCode == 0) {
						finish();
					} else {
						getData(keyCode, true, Code, haveType);
						changeListView();
					}

				} else {
					if (List.size() > 0) {
						keyCode = List.get(0);
						getData(keyCode, false, Code, haveType);
						changeListView();
					} else {
						finish();
					}

				}
			} else {// 搜索后按返回键
//				dataList.clear();
//				getData(0, true, Code, haveType);
//				changeListView();
				mClearEditText.setText("");
				isWrite = 0;
			}
			break;
			
		case 1:// 人员
			if (isWrite == 0) {
				if (DepartmentList.size() > 0) {
					keyCode = dataList.get(0).getParentID();
					L.e("keyCode = " + keyCode);
					L.e("DEPARTMENT_ID = " + preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
					if (keyCode == preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0)) {
						L.e("返回1");
						finish();
					} else {
						getData(keyCode, true, Code, haveType);
						changeListView();
					}

				} else {
					if (preferences.getString(Constant.HUISHANG_TYPE, "0")
							.equals("1")) {
						finish();
					} else {

						if (List.size() > 0) {
							keyCode = List.get(0);
							getData(keyCode, false, Code, haveType);
							changeListView();
						} else {
							super.onBackPressed();
						}
					}
				}
			} else {// 搜索后按返回键
				mClearEditText.setText("");
				isWrite = 0;
			}
			break;
		case 2:// 产品
			if (isWrite==0) {
				if (dataList.size()<=0) {
					Com_Class.clear();
					Com_Product.clear();
					getData(List.get(0), false, Code, haveType);
					changeListView();
					L.e("List.get(0)//////////:" + List.get(0));
				}else {
				
				if (!(dataList.get(0).getGroup_ID()).equals("") ) {
					Com_Class.clear();
					Com_Product.clear();
					getData(Integer.parseInt(dataList.get(0).getGroup_ID()), true, Code, haveType);
					changeListView();
					return;
				}else if (dataList.get(0).getParentID() != 0) {
					getData(dataList.get(0).getParentID(), true, Code, haveType);
					changeListView();
				}else {
					finish();
				}
			}
			}else {
				dataList.clear();
				mClearEditText.setText("");
				isWrite = 0;
			}
			
			break;

		default:
			break;
		}

	}

	/**
	 * 通过刚进来的模式，选择显示的界面和操作
	 */
	private void selectMode(){
		
		sure_bar = (LinearLayout)findViewById(R.id.sure_bar);
		Intent intent = getIntent();
		Code = intent.getStringExtra("mode");
		//0表示多选1表示单选
		select = intent.getIntExtra("select", 0);
		L.i("select:" + select);
		Tittle = intent.getStringExtra("Tittle");
		groupName = intent.getStringExtra("groupName");
		
		//多选----计划选择多个客户
		if (Code.equals("0")&& select == 0) {
			Bundle bundle = intent.getBundleExtra("bundle");
			Tlist = (java.util.List<PlanList>) bundle.getSerializable("SelectList");
		}
		
		if (Code.equals("1")||select == 1) {
			sure_bar.setVisibility(View.GONE);
		}
		

	}

}

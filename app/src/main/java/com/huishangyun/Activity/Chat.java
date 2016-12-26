package com.huishangyun.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeUser;
import com.huishangyun.Adapter.FaceAdapter;
import com.huishangyun.Adapter.FacePageAdeapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Task.TaskChooseFileActivity;
import com.huishangyun.GIF.MyTextViewEx;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PhotoHelp;
import com.huishangyun.Util.Service;
import com.huishangyun.Util.ServiceMenu;
import com.huishangyun.Util.ServiceUtil;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.WebHelp;
import com.huishangyun.View.CirclePageIndicator;
import com.huishangyun.View.JazzyViewPager;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.IMMessage;
import com.huishangyun.model.MessageData;
import com.huishangyun.model.MessageType;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 聊天窗口
 *
 * @author pan
 */
public class Chat extends BaseChatActivity implements OnClickListener,
        OnScrollListener, OnTouchListener {
    private TextView tvChatTitle;
    private LinearLayout imgChatBack;
    private LinearLayout imgChatIcon1;
    private ImageView face_show_btn;
    private boolean mIsFaceShow = false;// 是否显示表情
    private boolean mIsMenuShow = false;
    private boolean mIsVideShow = false;
    private InputMethodManager mInputMethodManager;
    private EditText mChatEditText;// 消息输入框
    private LinearLayout mFaceRoot;// 表情父容器
    private int mCurrentPage = 0;// 当前表情页
    private List<String> mFaceMapKeys;// 表情对应的字符串数组
    private JazzyViewPager mFaceViewPager;// 表情选择ViewPager
    private MyXListView msgListView;// 聊天记录
    private MessageListAdapter adapter = null;// 聊天记录适配器
    private TextView Sendbtn;// 消息发送按钮
    private ImageView addBtn;
    private int recordCount;
    private Button et_chat_video;
    private List<GotyeMessage> list;
    // private ImageView record_media;
    private File out;// 相机的文件
    private String PhotoPath;
    private MediaRecorder mRecorder;
    private int[] micimg = {R.drawable.voice_vol01, R.drawable.voice_vol02,
            R.drawable.voice_vol03, R.drawable.voice_vol04,
            R.drawable.voice_vol05};
    private LinearLayout layout;
    private ImageView mic_imgview;
    private long startTime;
    private long endTime;
    private int BASE = 600;
    private int SPACE = 200;// 间隔取样时间
    private String AutoFilePath;
    private MediaPlayer mPlayer = null;
    private ImageView face_video;
    private LinearLayout menuLayout;
    private ImageView takeCrame;
    private ImageView takePhoto;
    private ImageView takeFile;
    private static final int CHOOSE_FILE = 20;
    private boolean isRessss = false;
    private int size = 1;
    // 以下是服务号功能
    private LinearLayout chatLayout; // 聊天布局
    private LinearLayout serviceLayout; // 服务号布局
    private LinearLayout serviceSureBtn; // 打开服务功能
    private LinearLayout serviceNoBen; // 收起服务功能
    private FrameLayout serviceBtn01; // 服务功能1
    private FrameLayout serviceBtn02; // 服务功能2
    private FrameLayout serviceBtn03; // 服务功能3
    private TextView serviceTxt01; // 服务功能01名称
    private TextView serviceTxt02; // 服务功能02名称
    private TextView serviceTxt03; // 服务功能03名称
    private List<ServiceMenu> fristMenus;
    private List<ServiceMenu> SecondMenu_01 = new ArrayList<ServiceMenu>();
    private List<ServiceMenu> SecondMenu_02 = new ArrayList<ServiceMenu>();
    private List<ServiceMenu> SecondMenu_03 = new ArrayList<ServiceMenu>();
    private ImageView serviceImg_01;
    private ImageView serviceImg_02;
    private ImageView serviceImg_03;
    private String PhotoUrl;//图片资源
    private  String photo;
    private GotyeChatTarget target;//新加的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.e("走了onCreate");
        setContentView(R.layout.activity_chat);
        MyApplication.getInstance().getGotyeAPI().addListener(mDelegate);
        initIntent(getIntent());
        initview();
        initlistener();
        initFacePage();
        GotyeAPI.getInstance().setMessageReadIncrement(10);
        if (chatType == 0) {
            //  GotyeAPI.getInstance().markMeeagesAsread(user);
            GotyeAPI.getInstance().getMessageList(user,false);
            GotyeAPI.getInstance().activeSession(user);
            list.clear();
            // list = loadData();
            for (GotyeMessage gotyeMessage : loadData()) {
                list.add(gotyeMessage);
            }
            adapter.resreshList();
        } else {
            GotyeAPI.getInstance().getMessageList(group, true);
            GotyeAPI.getInstance().activeSession(group);
            list.clear();
            for (GotyeMessage gotyeMessage : loadData()) {
                list.add(gotyeMessage);
            }
            adapter.resreshList();
        }
        GotyeAPI.getInstance().enableTextFilter(
                GotyeChatTargetType.values()[chatType], false);
        int state = MyApplication.getInstance().getGotyeAPI().isOnline();
        if (state != 1) {
            setErrorTip(0);
        } else {
            setErrorTip(1);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        L.e("走了onNewIntent");
        setIntent(intent);
        initIntent(intent);
    }

    /**
     * 取出里面的值
     *
     * @param intent
     */
    private void initIntent(Intent intent) {

        to = intent.getStringExtra("JID");
        type = intent.getIntExtra("type", 1);
        m_sSign = intent.getStringExtra("Sign");
        user_name = intent.getStringExtra("name");
        messtype = intent.getIntExtra("messtype", 0);
        chat_name = intent.getCharSequenceExtra("chat_name");
        if (messtype == 0) {
            chatType = messtype;
        } else {
            chatType = 2;
        }

        o_user = user = (GotyeUser) intent.getSerializableExtra("user");
        o_group = group = (GotyeGroup) intent.getSerializableExtra("group");
        currentLoginUser = MyApplication.getInstance().getGotyeAPI().getLoginUser();
        L.d("JID =" + to);

        if (to == null)
            return;
        //onCreate(savedInstanceState);

        initview();
        initlistener();
        initFacePage();
        initService();
        GotyeAPI.getInstance().setMessageReadIncrement(10);
        if (chatType == 0) {
          //  GotyeAPI.getInstance().markMeeagesAsread(user);易注释
            GotyeAPI.getInstance().getMessageList(user, true);
            GotyeAPI.getInstance().activeSession(user);
            list.clear();
            // list = loadData();
            for (GotyeMessage gotyeMessage : loadData()) {
                list.add(gotyeMessage);
            }
            adapter.resreshList();
        } else {
          //  GotyeAPI.getInstance().markMeeagesAsread(group);易注释
            GotyeAPI.getInstance().getMessageList(group, true);
            GotyeAPI.getInstance().activeSession(group);
            list.clear();
            for (GotyeMessage gotyeMessage : loadData()) {
                list.add(gotyeMessage);
            }
            adapter.resreshList();
        }
        GotyeAPI.getInstance().enableTextFilter(
                GotyeChatTargetType.values()[chatType], false);

        int state = MyApplication.getInstance().getGotyeAPI().isOnline();
        if (state != 1) {
            setErrorTip(0);
        } else {
            setErrorTip(1);
        }
    }

    /**
     * 设置聊天布局
     */
    private void initService() {
        // TODO Auto-generated method stub
        L.e("to = " + to);
        if (to.contains("_s_")) { // 判断到是服务号
            fristMenus = new ArrayList<ServiceMenu>();
            fristMenus = getFristMenu(StringUtil.getUserNameByJid(to));
            if (fristMenus == null || fristMenus.size() == 0) {
                chatLayout.setVisibility(View.VISIBLE);
                serviceLayout.setVisibility(View.GONE);
                serviceSureBtn.setVisibility(View.GONE);
            } else {
                chatLayout.setVisibility(View.GONE);
                serviceLayout.setVisibility(View.VISIBLE);
                serviceSureBtn.setVisibility(View.VISIBLE);
                serviceTxt01.setText(fristMenus.get(0).getName());
                serviceTxt02.setText(fristMenus.get(1).getName());
                serviceTxt03.setText(fristMenus.get(2).getName());
                // 获取二级菜单数据
                SecondMenu_01 = getSecondMenu(fristMenus.get(0).getID());
                SecondMenu_02 = getSecondMenu(fristMenus.get(1).getID());
                SecondMenu_03 = getSecondMenu(fristMenus.get(2).getID());
                if (SecondMenu_01.size() == 0) {
                    serviceImg_01.setVisibility(View.GONE);
                }

                if (SecondMenu_02.size() == 0) {
                    serviceImg_02.setVisibility(View.GONE);
                }

                if (SecondMenu_03.size() == 0) {
                    serviceImg_03.setVisibility(View.GONE);
                }
                serviceSureBtn.setOnClickListener(this);
                serviceNoBen.setOnClickListener(this);
                serviceBtn01.setOnClickListener(this);
                serviceBtn02.setOnClickListener(this);
                serviceBtn03.setOnClickListener(this);
            }
        } else { // 不是服务号隐藏服务号布局
            chatLayout.setVisibility(View.VISIBLE);
            serviceLayout.setVisibility(View.GONE);
            serviceSureBtn.setVisibility(View.GONE);
        }
    }

    private void showServiceMenu(boolean isShow) {
        if (isShow) {
            chatLayout.setVisibility(View.GONE);
            serviceLayout.setVisibility(View.VISIBLE);
        } else {
            chatLayout.setVisibility(View.VISIBLE);
            serviceLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 实例化组件
     */
    private void initview() {
        list = new ArrayList<GotyeMessage>();
        tvChatTitle = (TextView) this.findViewById(R.id.chat_title_txt);
        imgChatBack = (LinearLayout) this.findViewById(R.id.chat_back);
        imgChatIcon1 = (LinearLayout) this.findViewById(R.id.chat_info);
        face_show_btn = (ImageView) this.findViewById(R.id.face_switch_btn);
        msgListView = (MyXListView) this.findViewById(R.id.listview_chat);
        chatLayout = (LinearLayout) this
                .findViewById(R.id.lay_activity_chat_edit);
        serviceLayout = (LinearLayout) this
                .findViewById(R.id.lay_chat_lightapp);
        serviceSureBtn = (LinearLayout) this
                .findViewById(R.id.chat_lightapp_sure);
        serviceNoBen = (LinearLayout) this.findViewById(R.id.chat_lightapp_no);
        serviceBtn01 = (FrameLayout) this.findViewById(R.id.chat_lightapp_01);
        serviceBtn02 = (FrameLayout) this.findViewById(R.id.chat_lightapp_02);
        serviceBtn03 = (FrameLayout) this.findViewById(R.id.chat_lightapp_03);

        serviceTxt01 = (TextView) this.findViewById(R.id.chat_lightapp_01txt);
        serviceTxt02 = (TextView) this.findViewById(R.id.chat_lightapp_02txt);
        serviceTxt03 = (TextView) this.findViewById(R.id.chat_lightapp_03txt);

        serviceImg_01 = (ImageView) this.findViewById(R.id.chat_lightapp_img01);
        serviceImg_02 = (ImageView) this.findViewById(R.id.chat_lightapp_img02);
        serviceImg_03 = (ImageView) this.findViewById(R.id.chat_lightapp_img03);

        // msgListView.setOnRefreshListener(onRefreshListener);
        msgListView.setMyXListViewListener(myXListViewListener);
        msgListView.setPullLoadEnable(false);
        msgListView.setPullRefreshEnable(true);
        msgListView.setIsChat();
        msgListView.setTimeLayoutGone();
        layout = (LinearLayout) this.findViewById(R.id.chat_mic_layout);
        mic_imgview = (ImageView) this.findViewById(R.id.chat_mic_img);
        face_video = (ImageView) this.findViewById(R.id.face_video_btn);
        layout.setVisibility(View.GONE);
        et_chat_video = (Button) this.findViewById(R.id.et_chat_video);
        et_chat_video.setVisibility(View.GONE);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mFaceRoot = (LinearLayout) findViewById(R.id.face_ll);
        mChatEditText = (EditText) this.findViewById(R.id.et_chat_content);
        mFaceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
        /*
         * take_photo = (ImageView) findViewById(R.id.take_photo); takeImage =
		 * (ImageView) findViewById(R.id.take_image);
		 */
        // record_media = (ImageView) findViewById(R.id.record_media);
        addBtn = (ImageView) findViewById(R.id.face_add_btn);
        tvChatTitle.setText(chat_name);
        menuLayout = (LinearLayout) findViewById(R.id.file_ll);
        imgChatIcon1.setOnClickListener(this);
        Set<String> keySet = MyApplication.getInstance().getFaceMap().keySet();
        mFaceMapKeys = new ArrayList<String>();
        mFaceMapKeys.addAll(keySet);
        Sendbtn = (TextView) this.findViewById(R.id.img_chat_send);
        Sendbtn.setVisibility(View.GONE);
        /*
         * list = MessageManager.getInstance(context) .getMessageListByFrom(to,
		 * 1, pageSize);
		 */
        // Collections.sort(list);
        adapter = new MessageListAdapter(Chat.this, list, msgListView);
        msgListView.setCacheColorHint(0);
        msgListView.setAdapter(adapter);
        // msgListView.smoothScrollToPosition(adapter.getCount() - 1);
        msgListView.setVisibility(View.VISIBLE);
        msgListView.setOnScrollListener(listener);
        // msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setOnTouchListener(this);
        mChatEditText.setOnTouchListener(this);
        mChatEditText.addTextChangedListener(watcher);
        takeCrame = (ImageView) findViewById(R.id.chat_crame);
        takePhoto = (ImageView) findViewById(R.id.chat_photo);
        takeFile = (ImageView) findViewById(R.id.chat_file);
        msgListView.setSelection((adapter.getCount() - 1));
		/*
		 * take_photo.setOnClickListener(this);
		 * takeImage.setOnClickListener(this);
		 */
        // record_media.setOnTouchListener(this);
    }

    /**
     * 添加监听
     */
    private void initlistener() {
        imgChatBack.setOnClickListener(this);
        face_show_btn.setOnClickListener(this);
        Sendbtn.setOnClickListener(this);
        face_video.setOnClickListener(this);
        et_chat_video.setOnTouchListener(this);
        addBtn.setOnClickListener(this);
        takeCrame.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        takeFile.setOnClickListener(this);
    }

    /**
     * 初始化表情 哈
     */
    private void initFacePage() {
        List<View> lv = new ArrayList<View>();
        for (int i = 0; i < MyApplication.NUM_PAGE; i++) {
            lv.add(getGridView(i));
        }
        FacePageAdeapter adapter = new FacePageAdeapter(lv, mFaceViewPager);
        mFaceViewPager.setAdapter(adapter);
        mFaceViewPager.setCurrentItem(mCurrentPage);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mFaceViewPager);
        adapter.notifyDataSetChanged();
        mFaceRoot.setVisibility(View.GONE);
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int i) {
                mCurrentPage = i;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // do nothing
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // do nothing
            }
        });
    }

    /**
     * 返回表情页
     *
     * @param i
     * @return
     */
    private GridView getGridView(int i) {
        GridView gv = new GridView(this);
        gv.setNumColumns(7);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        gv.setBackgroundColor(Color.TRANSPARENT);
        gv.setCacheColorHint(Color.TRANSPARENT);
        gv.setHorizontalSpacing(10);
        gv.setVerticalSpacing(1);
        gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        gv.setGravity(Gravity.CENTER);
        gv.setAdapter(new FaceAdapter(this, i));
        gv.setOnTouchListener(forbidenScroll());
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == MyApplication.NUM) {// 删除键的位置
                    int selection = mChatEditText.getSelectionStart();
                    String text = mChatEditText.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            mChatEditText.getText().delete(start, end);
                            return;
                        }
                        mChatEditText.getText()
                                .delete(selection - 1, selection);
                    }
                } else {
                    int count = mCurrentPage * MyApplication.NUM + arg2;
                    // 注释的部分，在EditText中显示字符串
                    // String ori = msgEt.getText().toString();
                    // int index = msgEt.getSelectionStart();
                    // StringBuilder stringBuilder = new StringBuilder(ori);
                    // stringBuilder.insert(index, keys.get(count));
                    // msgEt.setText(stringBuilder.toString());
                    // msgEt.setSelection(index + keys.get(count).length());

                    // 下面这部分，在EditText中显示表情
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory
                                .decodeResource(getResources(),
                                        (Integer) MyApplication.getInstance()
                                                .getFaceMap().values()
                                                .toArray()[count]);
                        if (bitmap != null) {
                            int rawHeigh = bitmap.getHeight();
                            int rawWidth = bitmap.getHeight();
                            int newHeight = 60;
                            int newWidth = 60;
                            // 计算缩放因子
                            float heightScale = ((float) newHeight) / rawHeigh;
                            float widthScale = ((float) newWidth) / rawWidth;
                            // 新建立矩阵
                            Matrix matrix = new Matrix();
                            matrix.postScale(heightScale, widthScale);
                            // 设置图片的旋转角度
                            // matrix.postRotate(-30);
                            // 设置图片的倾斜
                            // matrix.postSkew(0.1f, 0.1f);
                            // 将图片大小压缩
                            // 压缩后图片的宽和高以及kB大小均会变化
                            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0,
                                    0, rawWidth, rawHeigh, matrix, true);
                            ImageSpan imageSpan = new ImageSpan(Chat.this,
                                    newBitmap);
                            String emojiStr = mFaceMapKeys.get(count);
                            SpannableString spannableString = new SpannableString(
                                    emojiStr);
                            spannableString.setSpan(imageSpan,
                                    emojiStr.indexOf('['),
                                    emojiStr.indexOf(']') + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mChatEditText.append(spannableString);
                        } else {
                            String ori = mChatEditText.getText().toString();
                            int index = mChatEditText.getSelectionStart();
                            StringBuilder stringBuilder = new StringBuilder(ori);
                            stringBuilder.insert(index, mFaceMapKeys.get(count));
                            mChatEditText.setText(stringBuilder.toString());
                            mChatEditText.setSelection(index
                                    + mFaceMapKeys.get(count).length());
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            }
        });
        return gv;
    }

    // 防止乱pageview乱滚动
    private OnTouchListener forbidenScroll() {
        return new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 聊天列表适配器
     *
     * @author pan
     */
    private class MessageListAdapter extends BaseAdapter {

        private List<GotyeMessage> items;
        private Context context;
        private LayoutInflater inflater;
        public static final int MESSAGE_DIRECT_RECEIVE = 1;
        public static final int MESSAGE_DIRECT_SEND = 0;
        public static final int TYPE_RECEIVE_TEXT = 0;
        public static final int TYPE_SEND_TEXT = 1;

        public MessageListAdapter(Context context, List<GotyeMessage> items,
                                  ListView adapterList) {
            this.context = context;
            this.items = items;
            currentLoginName = MyApplication.getInstance().getGotyeAPI()
                    .getLoginUser().getName();
        }

        public void updateMessage(GotyeMessage msg) {
            int position = items.indexOf(msg);
            if (position < 0) {
                return;
            }
            items.remove(position);
            items.add(position, msg);
            notifyDataSetChanged();
            scrollToBottom();
        }

        /**
         * @param msg
         */
        public void addMsgToBottom(GotyeMessage msg) {
            int position = items.indexOf(msg);
            if (position < 0) {
                items.add(msg);
                return;
            }
            items.remove(position);
            items.add(position, msg);
            notifyDataSetChanged();
            scrollToBottom();
        }

        public void resreshList() {
            this.notifyDataSetChanged();
            scrollToBottom();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items == null ? 0 : items.size();
        }

        @Override
        public GotyeMessage getItem(int position) {
            // TODO Auto-generated method stub
			/*
			 * if(position<0||position>=items.size()){ return null; }else{
			 * return items.get(position); }
			 */
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            GotyeMessage message = getItem(position);
            return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_TEXT
                    : TYPE_SEND_TEXT;
        }

        private String currentLoginName;
       // private MyChatTask myChatTask = null;

        private int getDirect(GotyeMessage message) {
            if (message.getSender().getName().equals(currentLoginName)) {
                return MESSAGE_DIRECT_SEND;
            } else {
                return MESSAGE_DIRECT_RECEIVE;
            }
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            // 有三种布局，分别为：接收到的普通消息，接收到的服务消息，用户发送的消息
            return 2;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            L.d("" + msgListView.getFirstVisiblePosition());
            final GotyeMessage message = items.get(position);
            ViewHolder holder = null;

            if (convertView == null) {

                if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {// 普通接收的即时消息
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_chat_left,
                            null);
                    holder.time = (TextView) convertView
                            .findViewById(R.id.chat_letf_time);
                    holder.msg = (MyTextViewEx) convertView
                            .findViewById(R.id.chat_left_msg);
                    holder.img = (ImageView) convertView
                            .findViewById(R.id.chat_left_img);
                    holder.imgs = (ImageView) convertView
                            .findViewById(R.id.chat_right_img);
                    holder.msgimg = (ImageView) convertView
                            .findViewById(R.id.chat_left_msgimg);
                    holder.record = (LinearLayout) convertView
                            .findViewById(R.id.chat_left_record);
                    holder.player = (ImageView) convertView
                            .findViewById(R.id.chat_left_player);
                    holder.file = (LinearLayout) convertView
                            .findViewById(R.id.chat_left_file);
                    holder.filename = (TextView) convertView
                            .findViewById(R.id.chat_left_filename);
                    holder.layout = (LinearLayout) convertView
                            .findViewById(R.id.chatlet_layout);
                    holder.playerTime = (TextView) convertView
                            .findViewById(R.id.chat_left_player_time);

                    holder.service_FlowName = (TextView) convertView
                            .findViewById(R.id.chat_service_FlowName);
                    holder.service_linear = (RelativeLayout) convertView
                            .findViewById(R.id.chat_service_linear);
                    holder.service_linears = (LinearLayout) convertView
                            .findViewById(R.id.chat_service_linears);//普通服务消息
                    holder.service_linears.setVisibility(View.GONE);
                    holder.service_linear.setVisibility(View.GONE);
                    holder.service_time = (TextView) convertView
                            .findViewById(R.id.chat_service_time);
                    holder.service_times = (TextView) convertView
                            .findViewById(R.id.chat_service_times);
                    holder.service_title = (TextView) convertView
                            .findViewById(R.id.chat_service_title);
                    holder.service_titles = (TextView) convertView
                            .findViewById(R.id.chat_service_titles);

//                    holder.service_body = (TextView) convertView
//                            .findViewById(R.id.chat_service_body);
                    holder.service_bodys = (TextView) convertView
                            .findViewById(R.id.chat_service_bodys);
                    holder.service_img = (ImageView) convertView
                            .findViewById(R.id.chat_service_img);
                    holder.service_imgs = (ImageView) convertView
                            .findViewById(R.id.chat_service_imgs);
                    holder.service_date = (TextView) convertView
                            .findViewById(R.id.chat_service_date);
//                    holder.service_dates = (TextView) convertView
//                            .findViewById(R.id.chat_service_dates);
                    holder.name = (TextView) convertView.findViewById(R.id.chat_left_name);
                    convertView.setTag(holder);
                } else if (getDirect(message) == MESSAGE_DIRECT_SEND) {// 自己发送的消息 服务号
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_chat_right,
                            null);
                    holder.time = (TextView) convertView
                            .findViewById(R.id.chat_right_time);
                    holder.msg = (MyTextViewEx) convertView
                            .findViewById(R.id.chat_right_msg);
                    holder.img = (ImageView) convertView
                            .findViewById(R.id.chat_right_img);
                    holder.msgimg = (ImageView) convertView
                            .findViewById(R.id.chat_right_msgimg);
                    holder.record = (LinearLayout) convertView
                            .findViewById(R.id.chat_right_record);
                    holder.player = (ImageView) convertView
                            .findViewById(R.id.chat_right_player);
                    holder.file = (LinearLayout) convertView
                            .findViewById(R.id.chat_right_file);
                    holder.filename = (TextView) convertView
                            .findViewById(R.id.chat_right_filename);
                    holder.layout = (LinearLayout) convertView
                            .findViewById(R.id.chatrig_layout);
                    holder.playerTime = (TextView) convertView
                            .findViewById(R.id.chat_right_player_time);
                    //聊天的人
                    holder.service_FlowName = (TextView) convertView
                            .findViewById(R.id.chat_service_FlowName);
                    holder.service_linear = (RelativeLayout) convertView
                            .findViewById(R.id.chat_service_linear);
                    holder.service_linears = (LinearLayout) convertView
                            .findViewById(R.id.chat_service_linears);//普通服务消息
                    holder.service_linears.setVisibility(View.GONE);
                    holder.service_linear.setVisibility(View.GONE);
                    holder.service_time = (TextView) convertView
                            .findViewById(R.id.chat_service_time);
                    holder.service_times = (TextView) convertView
                            .findViewById(R.id.chat_service_times);
                    holder.service_title = (TextView) convertView
                            .findViewById(R.id.chat_service_title);
                    holder.service_titles = (TextView) convertView
                            .findViewById(R.id.chat_service_titles);
//                    holder.service_body = (TextView) convertView
//                            .findViewById(R.id.chat_service_body);易注释
                    holder.service_bodys = (TextView) convertView
                            .findViewById(R.id.chat_service_bodys);
                    holder.service_img = (ImageView) convertView
                            .findViewById(R.id.chat_service_img);
                    holder.service_imgs = (ImageView) convertView
                            .findViewById(R.id.chat_service_imgs);
                    holder.service_date = (TextView) convertView
                            .findViewById(R.id.chat_service_date);
                    //易注释
//                    holder.service_dates = (TextView) convertView
//                            .findViewById(R.id.chat_service_dates);
                    holder.name = (TextView) convertView.findViewById(R.id.chat_right_name);
                    convertView.setTag(holder);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (getDirect(message) == MESSAGE_DIRECT_SEND) {// 自己发送的消息
                holder.img.setImageResource(R.drawable.plan_person);
                setImagePhoto(StringUtil.getUserNameByJid(preferences
                        .getString(Constant.XMPP_LOGIN_NAME, "")), holder.img);
                holder.name.setVisibility(View.GONE);
            } else {
                if (messtype == 0) {
                    holder.name.setVisibility(View.GONE);
                } else {
                    String name;
                    name = DepartmentManager.getInstance(context).getManager(message.getSender().getName());
                    if (name == null) {
                        name = MemberManager.getInstance(context).getMember(message.getSender().getName());
                        if (name == null) {
                            name = message.getSender().getName();
                        }
                    }
                    holder.name.setText(name + " :");
                    holder.name.setVisibility(View.VISIBLE);
                }
            }

            try {
                final MessageData<String> messageData = JsonUtil.fromJson(
                        message.getText(), new TypeToken<MessageData<String>>() {
                        }.getType());

                holder.layout.setVisibility(View.VISIBLE);
                holder.service_linear.setVisibility(View.GONE);
                if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {// 接收的消息
                    holder.img.setImageResource(R.drawable.plan_person);
                    setImagePhoto(message.getSender().getName(), holder.img);
                }
                long longtime = message.getDate() * 1000;
                holder.time.setText(TimeUtil.getChatTime(longtime));
                if (position > 0) {
                    long lastTime = (items.get(position - 1).getDate() * 1000);
                    L.e("longtime - lastTime = " + (longtime - lastTime));
                    if (longtime - lastTime > 60 * 1000 * 1) {// 当前时间比前一时间大于一分钟
                        holder.time.setVisibility(View.VISIBLE);
                    } else {
                        holder.time.setVisibility(View.GONE);
                    }
                }
                // 判断是否为录音
                if (messageData.getMessageCategory().equals(
                        MessageType.MESSAGE_VIDEO)) {
                    // 录音
                    holder.msg.setVisibility(View.GONE);
                    holder.file.setVisibility(View.GONE);
                    holder.record.setVisibility(View.VISIBLE);
                    holder.msgimg.setVisibility(View.GONE);
                    // 下载录音并添加点击事件监听
                    String filePath = "";
                    String fileLenght = "";
                    if (messageData.getMessageContent().contains("#")) {
                        filePath = messageData.getMessageContent().split("#")[0];
                        fileLenght = messageData.getMessageContent().split("#")[1];
                        ;
                    } else {
                        filePath = messageData.getMessageContent();
                        fileLenght = "0''";
                    }
                    holder.playerTime.setText(fileLenght + "");
                    FileTools.decodeRecording(Constant.pathurl+ MyApplication.getInstance().getCompanyID() + "/Chat/" + filePath, false, holder.record, holder.player);

                } else if (messageData.getMessageCategory().equals(
                        MessageType.MESSAGE_PHOTO)) {
                    // 照片
                    holder.msg.setVisibility(View.GONE);
                    holder.file.setVisibility(View.GONE);
                    holder.record.setVisibility(View.GONE);
                    holder.msgimg.setVisibility(View.VISIBLE);
                    holder.msgimg.setImageResource(R.drawable.image_loading);
                    // 加载图片
					/*
					 * new ImageLoader(Chat.this).DisplayImage(
					 * "http://img.huishangyun.com/UploadFile/huishang/"+
					 * MyApplication.getInstance().getCompanyID() +"/Chat/" +
					 * message.getContent(), holder.msgimg, false);
					 */
                    FileTools.decodeImage(
                            Constant.pathurl
                                    + MyApplication.getInstance()
                                    .getCompanyID() + "/Chat/"
                                    + messageData.getMessageContent(),
                            holder.msgimg, Chat.this);
                    //加载缩略图
                    ImageLoader.getInstance().displayImage(Constant.pathurl
                            + MyApplication.getInstance()
                            .getCompanyID() + "/Chat/100x100/"
                            + messageData.getMessageContent(), holder.msgimg);

                } else if (messageData.getMessageCategory().equals(
                        MessageType.MESSAGE_FILE)) {
                    // 文件
                    holder.msg.setVisibility(View.GONE);
                    holder.file.setVisibility(View.VISIBLE);
                    holder.record.setVisibility(View.GONE);
                    holder.msgimg.setVisibility(View.GONE);
                    // 设置相应数据并添加点击事件
                    holder.filename.setText(messageData.getMessageContent()
                            .split("#")[0]);
                    holder.file.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            FileTools.decodeFile(
                                    Constant.pathurl
                                            + MyApplication.getInstance()
                                            .getCompanyID()
                                            + "/Chat/"
                                            + messageData.getMessageContent()
                                            .split("#")[1], Chat.this);
                        }
                    });
                } else {
                    // 普通消息
                    holder.msg.setVisibility(View.VISIBLE);
                    holder.file.setVisibility(View.GONE);
                    holder.record.setVisibility(View.GONE);
                    holder.msgimg.setVisibility(View.GONE);
					/*
					 * holder.msg.setText(FaceUtil
					 * .convertNormalStringToSpannableString(Chat.this,
					 * message.getContent(), false));
					 */
                    // 播放gif图片
					/* holder.msg.setSpanText(mHandler, message.getContent()); */
                    holder.msg.insertGif(messageData.getMessageContent());
                }
            } catch (Exception e) {
                // TODO: handle exception
                // 服务号的内容
                e.printStackTrace();

                //Log.e("TAGS","获取到的内容message.getText()="+message.getText());
                MessageData<ServiceUtil> messageData = null;
                try {
                    messageData = JsonUtil.fromJson(message.getText(), new TypeToken<MessageData<ServiceUtil>>() {}.getType());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

//                MessageDatas<ServiceUtils> messageDatas = JsonUtil.fromJsons(
//                        message.getText(), MessageDatas.class);

                //这里类似于 判断消息类型
                try {
                    if (messageData.getMessageCategory().equals(MessageType.MESSAGE_SERVICE_TASK)) { // 判断到是服务号task
                        holder.service_linear.setVisibility(View.VISIBLE);//显示
                        holder.service_linears.setVisibility(View.GONE);//隐藏
                        //获取头像
                        holder.imgs.setImageResource(R.drawable.contact_person);

                        Service imgurl =ServiceManager.getInstance(Chat.this).getService(message.getSender().getName());
                        photo= imgurl.getPhoto();
                        if (imgurl == null) {
                            imgurl = ServiceManager.getInstance(Chat.this).getService(message.getSender().getName());
                            photo= imgurl.getPhoto();
                            if (imgurl == null) {
                                return null;
                            }
                        }
                        try {
                            ImageLoader.getInstance().displayImage(Constant.pathurl + MyApplication.preferences.getInt(Content.COMPS_ID, 1016) + "/Photo/" + photo, holder.imgs, MyApplication.getInstance().getGroupOptions());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        long longtime = (message.getDate() * 1000);
                        holder.service_time.setText(TimeUtil.getChatTime(longtime));
                        final ServiceUtil sUtil = messageData.getMessageContent();
                        //Log.e("TAGS","获取到的内容sUtil1="+sUtil);
                        if (sUtil == null) {
                            holder.service_title.setText(messageData.getMessageContent().toString());
                            return convertView;
                        }
                        // FlowName tilele  时间
                        holder.service_FlowName.setText(sUtil.getFlowName());
                        holder.service_title.setText(sUtil.getTitle());
                        //  holder.service_date.setText(TimeUtil.getChatServiceDate(sUtil.getSendDate()));
                        holder.service_date.setText(sUtil.getSenderTime());
                        //内容
                        try {
                            holder.service_body.setText(Html.fromHtml(sUtil.getContent()) + "");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        if (TextUtils.isEmpty(sUtil.getCover())
                                || sUtil.getCover() == null) {// 判断是否有封面
                            holder.service_img.setVisibility(View.GONE);
                        } else {
                            holder.service_img.setVisibility(View.VISIBLE);
                            holder.service_img
                                    .setImageResource(R.drawable.chat_service_load);

                        /*new com.huishangyun.ImageLoad.ImageLoader(Chat.this)
                                .DisplayImage(sUtil.getCover(),
                                        holder.service_img, false);*/
                            ImageLoader.getInstance().displayImage(sUtil.getCover(), holder.service_img);
                        }

                        holder.service_linear.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                // 这里打开webview查看详细的信息
                                Intent intent = new Intent(Chat.this, ChatWebview.class);
                                String httpurl = WebHelp.callWebservice(Chat.this)
                                        + "message/View.aspx?id="
                                        + sUtil.getID();
                                intent.putExtra("httpurl", httpurl);
                                startActivity(intent);
                            }
                        });
                    } else if (messageData.getMessageCategory().equals(//普通服务号类型消息server
                            MessageType.MESSAGE_SERVICE)) {
                        holder.service_linears.setVisibility(View.VISIBLE);
                        holder.service_linear.setVisibility(View.GONE);//隐藏

                        //易加
                        holder.imgs.setImageResource(R.drawable.plan_person);
//                        setImagePhoto(StringUtil.getUserNameByJid(preferences
//                                .getString(Constant.XMPP_LOGIN_NAME, "")), holder.imgs);

                        setImagePhoto(message.getSender().getName(), holder.imgs);

                        long longtime = (message.getDate() * 1000);
                        holder.service_times.setText(TimeUtil.getChatTime(longtime));

                        final ServiceUtil sUtils = messageData.getMessageContent();

                        holder.service_titles.setText(sUtils.getTitle());

                        try {
                            holder.service_bodys.setText(Html.fromHtml(sUtils.getContent() + ""));
                        } catch (Exception ss) {
                            // TODO: handle exception
                            ss.printStackTrace();
                        }
                        if (TextUtils.isEmpty(sUtils.getCover())
                                || sUtils.getCover() == null) {// 判断是否有封面
                            holder.service_imgs.setVisibility(View.GONE);

                        } else {
                            //holder.service_img.setVisibility(View.VISIBLE);
                            //holder.service_img.setImageResource(Integer.parseInt(sUtil.getCover()));
                            holder.service_imgs
                                    .setImageResource(R.drawable.chat_service_load);

                        /*new com.huishangyun.ImageLoad.ImageLoader(Chat.this)
                                .DisplayImage(sUtil.getCover(),
                                        holder.service_img, false);*/
                            ImageLoader.getInstance().displayImage(sUtils.getCover(), holder.service_imgs);
                        }
                        holder.service_linears.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                // 这里打开webview查看详细的信息
                                Intent intent = new Intent(Chat.this, ChatWebview.class);
                                String httpurl = WebHelp.callWebservice(Chat.this)
                                        + "message/View.aspx?id="
                                        + sUtils.getID();
                                intent.putExtra("httpurl", httpurl);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return convertView;
        }

    }

    /**
     * 设置照片
     *
     * @param JID
     * @param imageView
     */
    public void setImagePhoto(String JID, ImageView imageView) {
        String imgurl = DepartmentManager.getInstance(Chat.this)
                .getManagerPhoto(JID);
        if (imgurl == null) {
            imgurl = MemberManager.getInstance(Chat.this).getMemberPhoto(JID);
            if (imgurl == null) {
                imgurl = "";
            }
        }

        ImageLoader.getInstance().displayImage(Constant.pathurl
                + MyApplication.preferences.getInt(Content.COMPS_ID, 1016)
                + "/Photo/" + imgurl, imageView, MyApplication.getInstance().getOptions());

    }

    /**
     * 控件实体类
     *
     * @author pan
     */
    class ViewHolder {
        // 即时通讯内容
        public TextView time;
        public MyTextViewEx msg;
        public ImageView img;
        public ImageView imgs;
        public ImageView msgimg;
        public LinearLayout record;
        public ImageView player;
        public LinearLayout file;
        public TextView filename;
        public LinearLayout layout;
        public TextView name;
        public TextView playerTime;

        public LinearLayout service_linears;

        // 以下是服务号内容
        public RelativeLayout service_linear;
        public TextView service_time;
        public TextView service_title;
        public ImageView service_img;
        public TextView service_body;
        public TextView service_date;
        public TextView service_FlowName;
        //普通服务号消息类型
        public TextView service_titles;
        public TextView service_times;
        public ImageView service_imgs;
        public TextView service_bodys;
        // public TextView service_dates;
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HanderUtil.case1:
                    // 发送成功，清空输入框
                    mChatEditText.setText("");
                    break;
                case HanderUtil.case2:
                    // 发送失败
                    // T.showShort(Chat.this, "消息发送失败");
                    showCustomToast("消息发送失败", false);
                    break;
                case HanderUtil.case3:
                    // T.showShort(getApplicationContext(), "发送失败");
                    showCustomToast("发送失败", false);
                    dismissDialog();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * 按键监听
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            // 返回键
            case R.id.chat_back:
                finish();
                break;
            // 查看联系人资料
            case R.id.chat_info:
                if (to.contains("_c_")) { // 客户
                    Intent intent = new Intent(Chat.this, PrivateData.class);
                    intent.putExtra("JID", to);
                    intent.putExtra("Name", chat_name);
                    startActivity(intent);
                } else if (to.contains("_m_")) { // 人员
                    Intent intent = new Intent(Chat.this, ManagerInfo.class);
                    intent.putExtra("JID", to);
                    intent.putExtra("Name", chat_name);
                    startActivity(intent);
                } else if (to.contains("_s_")) { // 服务号资料
                    Intent intent = new Intent(Chat.this, SerivceIfon.class);
                    intent.putExtra("JID", to);
                    intent.putExtra("Name", chat_name);
                    startActivity(intent);
                } else {
                    if (group != null) {
                        //这里打开群资料
                        Intent intent = new Intent(Chat.this, GroupInfoActivity.class);
                        intent.putExtra("JID", to);
                        intent.putExtra("Name", chat_name);
                        startActivity(intent);
                    }
                }

                break;
            // 发送消息
            case R.id.img_chat_send:
                final String message = mChatEditText.getText().toString();
                if ("".equals(message.trim())) {
                    showCustomToast("不能发送空消息", false);
                } else {
                    // 发送消息
                    adapter.addMsgToBottom(sendMessage(message,
                            MessageType.MESSAGE_MSG));
                    scrollToBottom();
                    mChatEditText.setText("");
                }
                // closeInput();
                break;
            // 表情按钮
            case R.id.face_switch_btn:
                if (!mIsFaceShow) {
                    mInputMethodManager.hideSoftInputFromWindow(
                            mChatEditText.getWindowToken(), 0);
                    try {
                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    face_show_btn.setImageResource(R.drawable.chat_iconinput);
                    mFaceRoot.setVisibility(View.VISIBLE);
                    mIsFaceShow = true;
                    // 将菜单缩下去
                    addBtn.setImageResource(R.drawable.chat_iconmore);
                    menuLayout.setVisibility(View.GONE);
                    mIsMenuShow = false;

                    // 将录音换成输入框
                    mChatEditText.setVisibility(View.VISIBLE);
                    et_chat_video.setVisibility(View.GONE);
                    mIsVideShow = false;
                    face_video.setImageResource(R.drawable.chat_iconvoice);

                } else {
                    mFaceRoot.setVisibility(View.GONE);
                    face_show_btn.setImageResource(R.drawable.chat_expression);
                    mInputMethodManager.showSoftInput(mChatEditText, 0);
                    mIsFaceShow = false;
                }
                break;
            // 发送相机图片
            case R.id.chat_crame:
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                PhotoPath = Constant.SAVE_IMG_PATH + File.separator
                        + getSystemTime() + ".jpg";
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(PhotoPath)));
                startActivityForResult(i, Content.REUQEST_CODE_OK_TAKEPHOTO);
                break;
            // 发送图库图片
            case R.id.chat_photo:
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                startActivityForResult(intent1, Content.REUQEST_CODE_OK_TAKEIMAGE);
                break;
            // 查看聊天历史记录
		/*
		 * case R.id.chat_history: Intent intent2 = new Intent(Chat.this,
		 * CharHistory.class); intent2.putExtra("to", to);
		 * intent2.putExtra("name", chat_name); intent2.putExtra("type",
		 * messtype); startActivity(intent2); break;
		 */
            case R.id.face_video_btn:// 录音按钮
                if (!mIsVideShow) {
                    mChatEditText.setVisibility(View.GONE);
                    et_chat_video.setVisibility(View.VISIBLE);
                    mIsVideShow = true;
                    face_video.setImageResource(R.drawable.chat_iconinput);
                    // 将表情缩下去
                    mFaceRoot.setVisibility(View.GONE);
                    face_show_btn.setImageResource(R.drawable.chat_expression);
                    // 将菜单缩下去
                    addBtn.setImageResource(R.drawable.chat_iconmore);
                    menuLayout.setVisibility(View.GONE);
                    mIsMenuShow = false;

                } else {
                    mChatEditText.setVisibility(View.VISIBLE);
                    et_chat_video.setVisibility(View.GONE);
                    mIsVideShow = false;
                    mInputMethodManager.showSoftInput(mChatEditText, 2);
                    face_video.setImageResource(R.drawable.chat_iconvoice);
                }
                break;

            case R.id.face_add_btn:// 文件添加按钮
                if (!mIsMenuShow) {
                    mInputMethodManager.hideSoftInputFromWindow(
                            mChatEditText.getWindowToken(), 0);
                    menuLayout.setVisibility(View.VISIBLE);
                    mIsMenuShow = true;
                    addBtn.setImageResource(R.drawable.chat_iconinput);
                    // 将表情缩下去
                    mFaceRoot.setVisibility(View.GONE);
                    face_show_btn.setImageResource(R.drawable.chat_expression);

                    // 将录音换成输入框
                    mChatEditText.setVisibility(View.VISIBLE);
                    et_chat_video.setVisibility(View.GONE);
                    mIsVideShow = false;
                    face_video.setImageResource(R.drawable.chat_iconvoice);

                    mIsFaceShow = false;
                } else {
                    mInputMethodManager.showSoftInput(mChatEditText, 1);
                    addBtn.setImageResource(R.drawable.chat_iconmore);
                    menuLayout.setVisibility(View.GONE);
                    mIsMenuShow = false;
                }
                break;

            case R.id.chat_file:
                Intent mIntent = new Intent(Chat.this, TaskChooseFileActivity.class);
                startActivityForResult(mIntent, CHOOSE_FILE);
                break;
            case R.id.chat_lightapp_sure:
                closeInput();
                mFaceRoot.setVisibility(View.GONE);
                face_show_btn.setImageResource(R.drawable.chat_expression);

                // 将录音换成输入框
                mChatEditText.setVisibility(View.VISIBLE);
                et_chat_video.setVisibility(View.GONE);
                mIsVideShow = false;
                face_video.setImageResource(R.drawable.chat_iconvoice);
                mIsFaceShow = false;
                mInputMethodManager.showSoftInput(mChatEditText, 1);
                addBtn.setImageResource(R.drawable.chat_iconmore);
                menuLayout.setVisibility(View.GONE);
                mIsMenuShow = false;

                showServiceMenu(true);

                break;
            case R.id.chat_lightapp_no:
                showServiceMenu(false);
                break;
            // 功能1
            case R.id.chat_lightapp_01:
                showSecondMenu(SecondMenu_01, 0, v);
                break;
            // 功能2
            case R.id.chat_lightapp_02:
                showSecondMenu(SecondMenu_02, 1, v);
                break;
            // 功能3
            case R.id.chat_lightapp_03:
                showSecondMenu(SecondMenu_03, 2, v);
                break;
            default:
                break;
        }
    }

    public void scrollToBottom() {
        msgListView.setSelection(adapter.getCount() - 1);
    }

    /**
     * 显示二级菜单
     *
     * @param functionID
     */
    private void showSecondMenu(List<ServiceMenu> serviceMenus, int functionID,
                                View v) {
        showMenu(functionID, serviceMenus, fristMenus.get(functionID).getID(),
                v);
    }

    /**
     * 显示菜单
     *
     * @param ID
     */
    private void showMenu(int functionID, List<ServiceMenu> serviceMenus,
                          int ID, View v) {
        // List<ServiceMenu> secondMenus = getSecondMenu(ID);
        String encodedURL = fristMenus.get(functionID).getUrl();
        String url = encodedURL;
        // Log.e("TAGS","getLogin值="+fristMenus.get(functionID).getLogin());
        if (serviceMenus == null || serviceMenus.size() == 0) {
            if (fristMenus.get(functionID).getLogin()) {
                try {
                    encodedURL = URLEncoder.encode(encodedURL, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String pathurl = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_WEBDOMAIN, "http://webapp.huishangyun.com");
                url = pathurl + "/Adminlogin.aspx?";

                url = url + "d=" + preferences.getInt(Content.COMPS_ID, 0)
                        + "&n=" + preferences.getString(Constant.USERNAME, "") + "&p="
                        + preferences.getString(Constant.PASSWORD, "") + "&u=" + encodedURL;
                  Log.e("TAGS","显示url1="+url);
            }
            // Log.e("TAGS","显示url2="+url);
            showWebInfo(url);
        } else {
            showPopupWindows(serviceMenus, v);
        }
    }

    /**
     * 构建菜单
     *
     * @param secondMenus
     */
    private void showPopupWindows(List<ServiceMenu> secondMenus, View v) {
        View serviceView = LayoutInflater.from(this).inflate(
                R.layout.service_layout, null);
        final LinearLayout layout = (LinearLayout) serviceView
                .findViewById(R.id.service_layout);
        final PopupWindow mWindow = new PopupWindow(this);
        for (final ServiceMenu serviceMenu : secondMenus) {
            View view = LayoutInflater.from(this).inflate(
                    R.layout.service_menu, null);
            TextView textView = (TextView) view
                    .findViewById(R.id.service_menu_name);
            textView.setText(serviceMenu.getName());
            layout.addView(view);
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    showWebInfo(serviceMenu.getUrl());
                    mWindow.dismiss();
                }
            });
        }
        mWindow.setContentView(serviceView);

        mWindow.setFocusable(true);
        // 防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        mWindow.setBackgroundDrawable(new PaintDrawable());

        // 设置点击窗口外边窗口消失
        mWindow.setOutsideTouchable(true);
        mWindow.update();
		/*DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);*/
        // 设置弹出窗体的宽
        mWindow.setWidth((MyApplication.getInstance().getDisplayMetrics(Chat.this).widthPixels - 100) / 3);

        // 设置弹出窗体的高
        mWindow.setHeight(LayoutParams.WRAP_CONTENT);

        // mWindow.showAtLocation(v, Gravity.BOTTOM, 10, 10);
        mWindow.showAsDropDown(v, 0, 10);
    }

    /**
     * 显示服务号信息
     *
     * @param url
     */
    private void showWebInfo(String url) {
        Intent intent = new Intent(this, ChatWebview.class);
        intent.putExtra("httpurl", url);
        startActivity(intent);
    }

    /**
     * 输入框文字监听
     */
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (s.length() > 0) {// 根据输入的文字切换图标
                Sendbtn.setVisibility(View.VISIBLE);
                face_video.setVisibility(View.GONE);
            } else {
                Sendbtn.setVisibility(View.GONE);
                face_video.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
		/*
		 * recordCount = MessageManager.getInstance(context)
		 * .getChatCountWithSb(to); if (recordCount <= 0) {
		 * msgListView.setVisibility(View.GONE); } else {
		 * msgListView.setVisibility(View.VISIBLE); }
		 */
        // adapter.refreshList(getMessages());
		/*
		 * list = getMessages(); adapter.notifyDataSetChanged();
		 * msgListView.smoothScrollToPosition(list.size() - 1);
		 */
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
		/*recordCount = MessageManager.getInstance(context)
				.getChatCountWithSb(to);*/
        MyApplication.getInstance().getGotyeAPI().removeListener(mDelegate);
        if (chatType == 0){
            MyApplication.getInstance().getGotyeAPI().deactiveSession(user);
        }else {
            MyApplication.getInstance().getGotyeAPI().deactiveSession(group);
        }
        if (isRessss) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        } else {
            mRecorder = null;
        }
        FileTools.stopPlaying(null, null);
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.et_chat_content:
                mInputMethodManager.showSoftInput(mChatEditText, 0);
                face_show_btn.setImageResource(R.drawable.chat_expression);
                mFaceRoot.setVisibility(View.GONE);
                mIsFaceShow = false;

                // 将菜单缩下去
                addBtn.setImageResource(R.drawable.chat_iconmore);
                menuLayout.setVisibility(View.GONE);
                mIsMenuShow = false;
                scrollToBottom();
                break;
            case R.id.listview_chat:
                mInputMethodManager.hideSoftInputFromWindow(
                        mChatEditText.getWindowToken(), 0);
                face_show_btn.setImageResource(R.drawable.chat_expression);
                mFaceRoot.setVisibility(View.GONE);
                mIsFaceShow = false;

                // 将菜单缩下去
                addBtn.setImageResource(R.drawable.chat_iconmore);
                menuLayout.setVisibility(View.GONE);
                mIsMenuShow = false;
                break;
            // 发送语音消息
            case R.id.et_chat_video:
                // 开始录音
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        v.setBackgroundResource(R.drawable.chat_inputtextdown);
                        return true;
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        stopRecording();
                        v.setBackgroundResource(R.drawable.chat_inputtext);
                        return false;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    L.e("录音失败1");
                    showCustomToast("录音失败", false);
                }

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    final Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            L.d("获取文件成功");
            switch (requestCode) {

                case Content.REUQEST_CODE_OK_TAKEPHOTO:
                    // 拍照返回
                    showNotDialog("正在发送...");
                    if (new File(PhotoPath).exists()) {
                        /**
                         * 处理图片，压缩到200k以内
                         */
                        new Thread() {
                            public void run() {
                                File file;
                                try {
                                    file = PhotoHelp.compressImage(PhotoPath,
                                            Constant.SAVE_IMG_PATH + File.separator
                                                    + getSystemTime() + ".png");
                                    Message msg = new Message();
                                    msg.obj = file;
                                    msg.what = HanderUtil.case1;
                                    mHandler.sendMessage(msg);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    mHandler.sendEmptyMessage(HanderUtil.case2);
                                    e.printStackTrace();
                                }

                            }

                            ;
                        }.start();

                    }
                    break;
                case Content.REUQEST_CODE_OK_TAKEIMAGE:
                    // 选择图片返回
                    showNotDialog("正在发送...");
                    if (data != null) {
                        /**
                         * 处理图片，压缩到200k以内
                         */
                        new Thread() {
                            public void run() {
                                File file;
                                try {
                                    file = PhotoHelp.compressImage(getFiel(data),
                                            Constant.SAVE_IMG_PATH + File.separator
                                                    + getSystemTime() + ".png");
                                    Message msg = new Message();
                                    msg.obj = file;
                                    msg.what = HanderUtil.case1;
                                    mHandler.sendMessage(msg);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    mHandler.sendEmptyMessage(HanderUtil.case2);
                                    e.printStackTrace();
                                }

                            }
                        }.start();
                    }
                    break;

                case CHOOSE_FILE:
                    final String filePath = data.getData().getPath();
                    L.e("filePath = " + filePath);
                    showDialog("正在发送...");
                    sendFileMesage(filePath, MessageType.MESSAGE_FILE);

                default:
                    break;
            }

        }
    }

    /**
     * 根据uri得到真实文件
     *
     * @param data
     * @return
     */
    private String getFiel(Intent data) {
        Uri uri = data.getData();
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);

        if (img_path == null) {
            img_path = FileUtils.getPath(Chat.this, uri);
        }
        L.d("img_path = " + img_path);
        // File file = new File(img_path);
        return img_path;
    }

    /**
     * 录音
     */
    private void startRecording() {
        try {
            L.d("开始录音");
            // File file =
            // File.createTempFile(java.lang.System.currentTimeMillis()+"",
            // ".amr",
            // Constant.SAVE_AUTO_PATH);
            isRessss = true;
            AutoFilePath = Constant.SAVE_AUTO_PATH + File.separator
                    + getSystemTime() + ".amr";
            File file = new File(AutoFilePath);
            if (!file.exists()) {
                L.d("创建文件" + file.getAbsolutePath());
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    L.e("录音失败2");
                    showCustomToast("录音失败", false);
                    layout.setVisibility(View.GONE);
                    return;
                }
            }
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 保存到指定文件
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mRecorder.prepare();

            // 开始录音
            mRecorder.start();
            startTime = getSystemTime();
            layout.setVisibility(View.VISIBLE);
            updateMicStatus();
        } catch (IllegalStateException e) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            e.printStackTrace();
            L.e("录音失败3");
            showCustomToast("录音失败", false);
            layout.setVisibility(View.GONE);
            isRessss = false;

        } catch (Exception e) {
            mRecorder = null;
            e.printStackTrace();
            L.e("录音失败4");
            showCustomToast("录音失败", false);
            layout.setVisibility(View.GONE);
            isRessss = false;
        }
    }

    /**
     * 停止并保存录音,返回时间
     */
    private long stopRecording() {
        L.d("停止录音");
        isRessss = false;
        endTime = getSystemTime();
        long amrTime = endTime - startTime;
        if (amrTime > 1000) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            layout.setVisibility(View.GONE);
            mRecorder = null;
			/*
			 * File file = new File(AutoFilePath); if (file.exists()) { try {
			 * sendFile(to + "/YiQiYun_Android", file); } catch (Exception e) {
			 * // TODO Auto-generated catch block L.d("发送文件失败");
			 * 
			 * e.printStackTrace(); } }
			 */
            sendFileMesage(AutoFilePath, MessageType.MESSAGE_VIDEO);
        } else {
            L.d("录音时间太短");
            layout.setVisibility(View.GONE);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            // T.showShort(Chat.this, "录音时间太短");
            showCustomToast("录音时间太短", false);
        }

        return amrTime;
    }

    /**
     * 实时获取音量大小
     */
    private void updateMicStatus() throws Exception {
        if (mRecorder != null && mic_imgview != null) {
            int ratio = mRecorder.getMaxAmplitude() / BASE;
            int db = 0;// 分贝
            if (ratio > 1)
                db = (int) (20 * Math.log10(ratio));
            L.d("分贝值：" + db + "     " + Math.log10(ratio));
            switch (db / 4) {
                case 0:
                    mic_imgview.setImageResource(micimg[0]);
                    break;
                case 1:
                    mic_imgview.setImageResource(micimg[0]);
                    break;
                case 2:
                    mic_imgview.setImageResource(micimg[1]);
                    break;
                case 3:
                    mic_imgview.setImageResource(micimg[2]);
                    break;
                case 4:
                    mic_imgview.setImageResource(micimg[3]);
                    break;
                case 5:
                    mic_imgview.setImageResource(micimg[4]);
                    break;
            }

            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);

        }
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HanderUtil.case1:
                    File file = (File) msg.obj;
                    L.e("进入发送");
                    sendImgMessage(file.getAbsolutePath(),
                            MessageType.MESSAGE_PHOTO);
                    break;
                case HanderUtil.case2:
                    L.e("压缩失败");
                    dismissDialog();
                    showCustomToast("发送失败", false);
                    break;
                case HanderUtil.case13:
                    Service managers = (Service) msg.obj;
                    PhotoUrl = managers.getPhoto();
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            try {
                updateMicStatus();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                L.e("录音失败5");
                showCustomToast("录音失败", false);
                e.printStackTrace();
                layout.setVisibility(View.GONE);
                mHandler.removeCallbacks(mUpdateMicStatusTimer);
            }
        }
    };
    /**
     * 滑动监听
     */
    private int FirstPosition;
    private int LastPosition;
    private OnScrollListener listener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView paramAbsListView,
                                         int paramInt) {
            // TODO Auto-generated method stub
            paramAbsListView.getFirstVisiblePosition();
        }

        @Override
        public void onScroll(AbsListView paramAbsListView,
                             int firstVisibleItem, int visibleItemCount, int totalItemCoun) {
            // TODO Auto-generated method stub
            // 记录下屏幕最顶端和屏幕最低端的item位置
            FirstPosition = paramAbsListView.getFirstVisiblePosition();
            LastPosition = paramAbsListView.getLastVisiblePosition();
        }
    };

    private class GetDataTask extends AsyncTask<Void, Void, List<GotyeMessage>> {

        @Override
        protected List<GotyeMessage> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            size++;
            return null;
        }

        @Override
        protected void onPostExecute(List<GotyeMessage> messages) {
            // TODO Auto-generated method stub
			/*
			 * refreshMessage(messages);
			 */
            if (messages.size() != 0) {
                // list = messages;
                // adapter.refreshList(messages);
                // list.clear();
                for (GotyeMessage imMessage : messages) {
                    list.add(0, imMessage);
                }
                adapter.notifyDataSetChanged();
                msgListView.stopRefresh();
                msgListView.setSelection(messages.size());

            } else {
                showCustomToast("没有更多数据了...", false);
                msgListView.stopRefresh();
            }

            // msgListView.smoothScrollToPosition(linkListSize - 1);
            super.onPostExecute(messages);
        }
    }

    private MyXListView.MyXListViewListener myXListViewListener = new MyXListView.MyXListViewListener() {

        @Override
        public void onRefresh() {
            // TODO Auto-generated method stub
            // new GetDataTask().execute();
            List<GotyeMessage> list = null;

            if (chatType == 0) {
                list = MyApplication.getInstance().getGotyeAPI().getMessageList(user, true);
            } else {
                list = MyApplication.getInstance().getGotyeAPI().getMessageList(group, true);
            }
            if (list != null) {
                Chat.this.list.clear();
                for (GotyeMessage msg : list) {
                    MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(msg);
                    Chat.this.list.add(msg);
                    if (!MessageManager.getInstance(Chat.this).IsTimeEquals(TimeUtil.getLongToString((msg.getDate() * 1000)))) {
                        IMMessage imMessage = new IMMessage();
                        if (msg.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                            //保存群ID号
                            imMessage.setGroup_name(msg.getReceiver().getId() + "");
                            imMessage.setMesstype(1);
                            imMessage.setFromSubJid(msg.getSender().getName());
                        } else {
                            //0不是群
                            imMessage.setMesstype(0);
                            imMessage.setFromSubJid(to);
                        }
                        //保存内容
                        imMessage.setContent(msg.getText());
                        //保存时间
                        imMessage.setTime(TimeUtil.getLongToString((msg.getDate() * 1000)));
                        //1为发送,0为接收
                        imMessage.setMsgType(0);

                        MessageManager.getInstance(Chat.this).saveIMMessage(imMessage);
                    }
                }
            } else {
                showCustomToast("没有更多历史消息", false);
            }
            adapter.notifyDataSetChanged();
            msgListView.stopRefresh();
            msgListView.stopLoadMore();

        }

        @Override
        public void onLoadMore() {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void setMessage(GotyeMessage message) {
        // TODO Auto-generated method stub
        adapter.addMsgToBottom(message);
        msgListView.setSelection(adapter.getCount() - 1);
    }

    @Override
    protected void onSendMessage(GotyeMessage message) {
        // TODO Auto-generated method stub
        adapter.updateMessage(message);
        msgListView.setSelection(adapter.getCount() - 1);
    }

    @Override
    protected void refreshData(List<GotyeMessage> listmessages) {
        // TODO Auto-generated method stub
        list.clear();
        for (GotyeMessage gotyeMessage : listmessages) {
            list.add(gotyeMessage);
        }
        adapter.resreshList();
    }

    private void setErrorTip(int code) {
        // code=api.getOnLineState();
        if (code == 1) {
            ((LinearLayout) findViewById(R.id.net_status_bar_top))
                    .setVisibility(View.GONE);
        } else {
            ((LinearLayout) findViewById(R.id.net_status_bar_top))
                    .setVisibility(View.VISIBLE);
            if (code == -1) {
                ((TextView) findViewById(R.id.net_status_bar_info_top))
                        .setText("正在连接登陆...");
                ((LinearLayout) findViewById(R.id.net_status_bar_top))
                        .setVisibility(View.VISIBLE);
            } else {
                ((TextView) findViewById(R.id.net_status_bar_info_top))
                        .setText("当前未登陆或网络异常");
                ((LinearLayout) findViewById(R.id.net_status_bar_top))
                        .setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isMyMessage(GotyeMessage message) {
        if (message.getSender() != null
                && user.getName().equals(message.getSender().getName())
                && currentLoginUser.getName().equals(
                message.getReceiver().getName())) {
            return true;
        } else {
            return false;
        }
    }
    private GotyeDelegate mDelegate = new GotyeDelegate(){
        @Override
        public void onLogin(int code, GotyeUser user) {
            super.onLogin(code, user);
            setErrorTip(1);
        }

        @Override
        public void onLogout(int code) {
            super.onLogout(code);
            setErrorTip(0);
            if(mDelegate != null){
                MyApplication.getInstance().getGotyeAPI().removeListener(mDelegate);
                finish();
            }
        }

        @Override
        public void onReconnecting(int code, GotyeUser user) {
            super.onReconnecting(code, user);
            setErrorTip(-1);
        }
        @Override
        public void onSendMessage(int code, GotyeMessage message) {
            // GotyeChatManager.getInstance().insertChatMessage(message);
            adapter.updateMessage(message);
            if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
//				 api.decodeAudioMessage(message);
            }
            msgListView.setSelection(adapter.getCount() - 1);
        }

        @Override
        public void onReceiveMessage(GotyeMessage message) {
            if (chatType == 0) {
                if (isMyMessage(message)) {
                    adapter.addMsgToBottom(message);
                    msgListView.setSelection(adapter.getCount());
                    MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(message);
                }
            }else {
                if (message.getReceiver().getId() == group.getGroupID()) {
                    adapter.addMsgToBottom(message);
                    msgListView.setSelection(adapter.getCount());
                    MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(message);
                }
            }
        }
        @Override
        public void onDownloadMediaInMessage(int code, GotyeMessage message) {
            adapter.updateMessage(message);
        }
        @Override
        public void onGetUserDetail(int code, GotyeUser user) {
            if (chatType == 0) {
                if (user.getName().equals(Chat.this.user.getName())) {
                    Chat.this.user = user;
                }
            }
        }
    };
}

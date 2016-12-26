package com.huishangyun.App;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeAPI;
import com.huishangyun.Activity.LandActivity;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Attendance.OfficeMainDataList;
import com.huishangyun.Office.Clock.ReSetAlram;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.db.CityDBHelper;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.manager.CartManager;
import com.huishangyun.manager.CompetingManager;
import com.huishangyun.manager.DBManager;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.DisplayManager;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.manager.FileManager;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.manager.NoticeManager;
import com.huishangyun.manager.OperationTime;
import com.huishangyun.manager.ProductFavManager;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.manager.ScheduleManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.manager.UnitPriceManager;
import com.huishangyun.manager.VisitManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.service.MUCInfo;
import com.huishangyun.widget.CircleBitmapDisplayer;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    public static final int NUM_PAGE = 6;// 总共有多少页
    public static int NUM = 20;// 每页20个表情,还有最后一个删除button
    public Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
    public Map<String, String> mGifMap = new LinkedHashMap<String, String>();
    public Map<String, Integer> bigFaceMap = new LinkedHashMap<String, Integer>();
    public static MyApplication instance;
    public static SharedPreferences preferences = null;
    public static TelephonyManager tmManager;
    public static String IMEI;
    public static String IMSI;
    // 不接收群消息
    public static boolean notReceiveGroupMsg = false;
    //单列模式获取唯一的MyApplication实例

    //屏幕宽度
    public static int widthPixels;
    public static int heightPixels;
    public static int densityDpi;
    public static boolean isServiceRunning = false;
    public static boolean isExit = false;
    public static boolean isPhoneServiceRun = false;
    public static String LandLogSize = "xhdpi";
    public CityDBHelper cityDBHelper;
    public Context mContext;
    private GotyeAPI api;
    private static final String API_KEY = "96b6add1-fac2-43f4-b4c3-727efa58dcc5";
    //要缩照片的长高
    public static int photoWidth = 720;
    public static int photoHeigh = 1280;
    private DisplayImageOptions options = null;

    private DisplayImageOptions groupOptions = null;
    private DisplayImageOptions indexOptions = null;


    private List<Activity> list = new LinkedList<Activity>();
    ;

    public synchronized static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();

        }
        return instance;
    }

    public List<Activity> mList = new LinkedList<>();

    public List<Activity> getActivityList() {
        return mList;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        CrashReport.initCrashReport(getApplicationContext(), "900052149", true);//测试的时候建议用true，发布时用false

        SDKInitializer.initialize(this);
        instance = this;
        mContext = this;
        //在这里设置是否开启Debug模式
        /*L.isDebug = PreferenceUtils.getPrefBoolean(this,
				PreferenceConstants.ISNEEDLOG, true);*/
        //初始化操作
        api = GotyeAPI.getInstance();
        api.init(this, API_KEY); ///< 使用活动上下文以及appkey初始化亲加API
        //添加推送消息监听
        //api.addListener(mDelegate);
        api.initIflySpeechRecognition();///< 如需开启语音识别功能，请添加此行
        api.beginReceiveOfflineMessage();//开始接收离线消息

        initFaceMap();
        preferences = this.getSharedPreferences(Constant.LOGIN_SET, 0);
        tmManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMSI = tmManager.getSubscriberId();
        IMEI = tmManager.getDeviceId();
        reSetAlarm();
        //开启异常监听
        /*CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);*/
        //L.isDebug = true;
        initImageLoader(getApplicationContext());
        initGifMap();
        bigFaceMap();

        cityDBHelper = new CityDBHelper(this);
        //   SQLiteDatabase db = cityDBHelper.getWritableDatabase();
        try {
            cityDBHelper.createDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            L.e("复制数据库失败");
            e.printStackTrace();
        }
        //请求定位
        startLocation();
    }
    /**
     * 程序异常搜集
     * */
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            final String message=  thread.getName() + ":" + ex.getMessage();
            //程序异常搜集 下面三行代码可以不加
            exit();
            android.os.Process.killProcess(android.os.Process.myPid()) ;  //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出

        }
    };
    public void exit() {
        try {
            for (Activity activity : mList)
                if (activity != null)
                    activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    /**
     * 获取首页广告位
     */
    public void getNewAd() {
        webServiceHelp<OfficeMainDataList> webServiceHelp = new webServiceHelp<OfficeMainDataList>(Methods.HUISHANG_GETAD_LIST,
                new TypeToken<ZJResponse<OfficeMainDataList>>() {
                }.getType());
        webServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<OfficeMainDataList>() {
            @Override
            public void onServiceCallBack(boolean haveCallBack, ZJResponse<OfficeMainDataList> zjResponse) {
                if (haveCallBack) {
                    switch (zjResponse.getCode()) {
                        case 0:
                            List<OfficeMainDataList> lists = zjResponse.getResults();
                            if (lists != null && lists.size() > 0) {
                                //保存广告位数据
                                getSharedPreferences().edit().putString(Constant.HUISHANG_INDEX_IMGURL, lists.get(0).getPicture()).commit();
                                getSharedPreferences().edit().putString(Constant.HUISHANG_INDEX_GOURL, lists.get(0).getSourceUrl()).commit();
                                if (!lists.get(0).getPicture().equals("")) {
                                    FileTools.decodeImage2(Constant.pathurl+"AD/" + lists.get(0).getPicture(), null);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setCompany_ID(getCompanyID());
        zjRequest.setKeywords("外勤启动页全屏广告");
        webServiceHelp.start(JsonUtil.toJson(zjRequest));
    }
//    /**
//     * 判断是否接收群消息
//     */
//    public static boolean isNotReceiveGroupMsg() {
//        return notReceiveGroupMsg;
//    }
    /**
     * 获取IM连接
     *
     * @return
     */
    public GotyeAPI getGotyeAPI() {
        if (api == null) {
            api = GotyeAPI.getInstance();
            int code = api.init(getBaseContext(), API_KEY);
            //api.beginRcvOfflineMessge();
            api.beginReceiveOfflineMessage();
        }
        return GotyeAPI.getInstance();
    }
    private void startLocation() {
        LocationUtil.startLocation(MyApplication.this.getApplicationContext(), new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReceivePoi(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
                L.e("地址 = " + location.getAddrStr());
                LocationUtil.stopLocation();
            }
        });
    }

    /**
     * 获取本地城市列表的查询SQLiteDatabase
     *
     * @return
     */
    public SQLiteDatabase getCitySqLiteDatabase() {
        try {
            cityDBHelper.createDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cityDBHelper.getWritableDatabase();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "HSY_Yun/img");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .diskCacheExtraOptions(480, 320, null)
//		    .diskCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个  
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())//使用弱引用
//		    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HashCode 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(50) //缓存的文件数量
                .diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);//全局初始化此配置
    }

    public Map<String, Integer> getFaceMap() {
        if (!mFaceMap.isEmpty())
            return mFaceMap;
        return null;
    }

    /**
     * 获取圆形头像配置
     *
     * @return
     */
    public DisplayImageOptions getOptions() {
        if (options == null) {
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new CircleBitmapDisplayer())
                    .showImageOnLoading(R.drawable.contact_person) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.contact_person)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.contact_person)  //设置图片加载/解码过程中错误时候显示的图片
                    .build();
        }
        return options;
    }

    public DisplayImageOptions getGroupOptions() {
        if (groupOptions == null) {
            groupOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new CircleBitmapDisplayer())
                    .showImageOnLoading(R.drawable.contact_group) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.contact_group)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.contact_group)  //设置图片加载/解码过程中错误时候显示的图片
                    .build();
        }
        return groupOptions;
    }

    public DisplayImageOptions getIndexOptions() {
        if (indexOptions == null) {
            groupOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .showImageOnLoading(R.drawable.welcome) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.welcome)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.welcome)  //设置图片加载/解码过程中错误时候显示的图片
                    .build();
        }
        return indexOptions;
    }

    public Map<String, String> getGifMap() {
        if (!mGifMap.isEmpty())
            return mGifMap;
        return null;
    }

    public SharedPreferences getSharedPreferences() {
        if (preferences == null) {
            preferences = this.getSharedPreferences(Constant.LOGIN_SET, 0);
        }
        return preferences;
    }

    public int getCompanyID() {
        return getSharedPreferences().getInt(Content.COMPS_ID, 0);

    }

    public int getManagerID() {
        return Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));

    }

    public void initFaceMap() {
        // TODO Auto-generated method stub
        mFaceMap.put("[呲牙]", R.drawable.f_static_000);
        mFaceMap.put("[调皮]", R.drawable.f_static_001);
        mFaceMap.put("[流汗]", R.drawable.f_static_002);
        mFaceMap.put("[偷笑]", R.drawable.f_static_003);
        mFaceMap.put("[再见]", R.drawable.f_static_004);
        mFaceMap.put("[敲打]", R.drawable.f_static_005);
        mFaceMap.put("[擦汗]", R.drawable.f_static_006);
        mFaceMap.put("[猪头]", R.drawable.f_static_007);
        mFaceMap.put("[玫瑰]", R.drawable.f_static_008);
        mFaceMap.put("[流泪]", R.drawable.f_static_009);
        mFaceMap.put("[大哭]", R.drawable.f_static_010);
        mFaceMap.put("[嘘]", R.drawable.f_static_011);
        mFaceMap.put("[酷]", R.drawable.f_static_012);
        mFaceMap.put("[抓狂]", R.drawable.f_static_013);
        mFaceMap.put("[委屈]", R.drawable.f_static_014);
        mFaceMap.put("[便便]", R.drawable.f_static_015);
        mFaceMap.put("[炸弹]", R.drawable.f_static_016);
        mFaceMap.put("[菜刀]", R.drawable.f_static_017);
        mFaceMap.put("[可爱]", R.drawable.f_static_018);
        mFaceMap.put("[色]", R.drawable.f_static_019);
        mFaceMap.put("[害羞]", R.drawable.f_static_020);

        mFaceMap.put("[得意]", R.drawable.f_static_021);
        mFaceMap.put("[吐]", R.drawable.f_static_022);
        mFaceMap.put("[微笑]", R.drawable.f_static_023);
        mFaceMap.put("[发怒]", R.drawable.f_static_024);
        mFaceMap.put("[尴尬]", R.drawable.f_static_025);
        mFaceMap.put("[惊恐]", R.drawable.f_static_026);
        mFaceMap.put("[冷汗]", R.drawable.f_static_027);
        mFaceMap.put("[爱心]", R.drawable.f_static_028);
        mFaceMap.put("[示爱]", R.drawable.f_static_029);
        mFaceMap.put("[白眼]", R.drawable.f_static_030);
        mFaceMap.put("[傲慢]", R.drawable.f_static_031);
        mFaceMap.put("[难过]", R.drawable.f_static_032);
        mFaceMap.put("[惊讶]", R.drawable.f_static_033);
        mFaceMap.put("[疑问]", R.drawable.f_static_034);
        mFaceMap.put("[睡]", R.drawable.f_static_035);
        mFaceMap.put("[亲亲]", R.drawable.f_static_036);
        mFaceMap.put("[憨笑]", R.drawable.f_static_037);
        mFaceMap.put("[爱情]", R.drawable.f_static_038);
        mFaceMap.put("[衰]", R.drawable.f_static_039);
        mFaceMap.put("[撇嘴]", R.drawable.f_static_040);
        mFaceMap.put("[阴险]", R.drawable.f_static_041);

        mFaceMap.put("[奋斗]", R.drawable.f_static_042);
        mFaceMap.put("[发呆]", R.drawable.f_static_043);
        mFaceMap.put("[右哼哼]", R.drawable.f_static_044);
        mFaceMap.put("[拥抱]", R.drawable.f_static_045);
        mFaceMap.put("[坏笑]", R.drawable.f_static_046);
        mFaceMap.put("[飞吻]", R.drawable.f_static_047);
        mFaceMap.put("[鄙视]", R.drawable.f_static_048);
        mFaceMap.put("[晕]", R.drawable.f_static_049);
        mFaceMap.put("[大兵]", R.drawable.f_static_050);
        mFaceMap.put("[可怜]", R.drawable.f_static_051);
        mFaceMap.put("[强]", R.drawable.f_static_052);
        mFaceMap.put("[弱]", R.drawable.f_static_053);
        mFaceMap.put("[握手]", R.drawable.f_static_054);
        mFaceMap.put("[胜利]", R.drawable.f_static_055);
        mFaceMap.put("[抱拳]", R.drawable.f_static_056);
        mFaceMap.put("[凋谢]", R.drawable.f_static_057);
        mFaceMap.put("[饭]", R.drawable.f_static_058);
        mFaceMap.put("[蛋糕]", R.drawable.f_static_059);
        mFaceMap.put("[西瓜]", R.drawable.f_static_060);
        mFaceMap.put("[啤酒]", R.drawable.f_static_061);
        mFaceMap.put("[飘虫]", R.drawable.f_static_062);

        mFaceMap.put("[勾引]", R.drawable.f_static_063);
        mFaceMap.put("[OK]", R.drawable.f_static_064);
        mFaceMap.put("[爱你]", R.drawable.f_static_065);
        mFaceMap.put("[咖啡]", R.drawable.f_static_066);
        mFaceMap.put("[钱]", R.drawable.f_static_067);
        mFaceMap.put("[月亮]", R.drawable.f_static_068);
        mFaceMap.put("[美女]", R.drawable.f_static_069);
        mFaceMap.put("[刀]", R.drawable.f_static_070);
        mFaceMap.put("[发抖]", R.drawable.f_static_071);
        mFaceMap.put("[差劲]", R.drawable.f_static_072);
        mFaceMap.put("[拳头]", R.drawable.f_static_073);
        mFaceMap.put("[心碎]", R.drawable.f_static_074);
        mFaceMap.put("[太阳]", R.drawable.f_static_075);
        mFaceMap.put("[礼物]", R.drawable.f_static_076);
        mFaceMap.put("[足球]", R.drawable.f_static_077);
        mFaceMap.put("[骷髅]", R.drawable.f_static_078);
        mFaceMap.put("[挥手]", R.drawable.f_static_079);
        mFaceMap.put("[闪电]", R.drawable.f_static_080);
        mFaceMap.put("[饥饿]", R.drawable.f_static_081);
        mFaceMap.put("[困]", R.drawable.f_static_082);
        mFaceMap.put("[咒骂]", R.drawable.f_static_083);

        mFaceMap.put("[折磨]", R.drawable.f_static_084);
        mFaceMap.put("[抠鼻]", R.drawable.f_static_085);
        mFaceMap.put("[鼓掌]", R.drawable.f_static_086);
        mFaceMap.put("[糗大了]", R.drawable.f_static_087);
        mFaceMap.put("[左哼哼]", R.drawable.f_static_088);
        mFaceMap.put("[哈欠]", R.drawable.f_static_089);
        mFaceMap.put("[快哭了]", R.drawable.f_static_090);
        mFaceMap.put("[吓]", R.drawable.f_static_091);
        mFaceMap.put("[篮球]", R.drawable.f_static_092);
        mFaceMap.put("[乒乓球]", R.drawable.f_static_093);
        mFaceMap.put("[NO]", R.drawable.f_static_094);
        mFaceMap.put("[跳跳]", R.drawable.f_static_095);
        mFaceMap.put("[怄火]", R.drawable.f_static_096);
        mFaceMap.put("[转圈]", R.drawable.f_static_097);
        mFaceMap.put("[磕头]", R.drawable.f_static_098);
        mFaceMap.put("[回头]", R.drawable.f_static_099);
        mFaceMap.put("[跳绳]", R.drawable.f_static_100);
        mFaceMap.put("[激动]", R.drawable.f_static_101);
        mFaceMap.put("[街舞]", R.drawable.f_static_102);
        mFaceMap.put("[献吻]", R.drawable.f_static_103);
        mFaceMap.put("[左太极]", R.drawable.f_static_104);

        mFaceMap.put("[右太极]", R.drawable.f_static_105);
        mFaceMap.put("[闭嘴]", R.drawable.f_static_106);
    }


    public void initGifMap() {
        mGifMap.put("[呲牙]", "f000");
        mGifMap.put("[调皮]", "f001");
        mGifMap.put("[流汗]", "f002");
        mGifMap.put("[偷笑]", "f003");
        mGifMap.put("[再见]", "f004");
        mGifMap.put("[敲打]", "f005");
        mGifMap.put("[擦汗]", "f006");
        mGifMap.put("[猪头]", "f007");
        mGifMap.put("[玫瑰]", "f008");
        mGifMap.put("[流泪]", "f009");
        mGifMap.put("[大哭]", "f010");
        mGifMap.put("[嘘]", "f011");
        mGifMap.put("[酷]", "f012");
        mGifMap.put("[抓狂]", "f013");
        mGifMap.put("[委屈]", "f014");
        mGifMap.put("[便便]", "f015");
        mGifMap.put("[炸弹]", "f016");
        mGifMap.put("[菜刀]", "f017");
        mGifMap.put("[可爱]", "f018");
        mGifMap.put("[色]", "f019");
        mGifMap.put("[害羞]", "f020");

        mGifMap.put("[得意]", "f021");
        mGifMap.put("[吐]", "f022");
        mGifMap.put("[微笑]", "f023");
        mGifMap.put("[发怒]", "f024");
        mGifMap.put("[尴尬]", "f025");
        mGifMap.put("[惊恐]", "f026");
        mGifMap.put("[冷汗]", "f027");
        mGifMap.put("[爱心]", "f028");
        mGifMap.put("[示爱]", "f029");
        mGifMap.put("[白眼]", "f030");
        mGifMap.put("[傲慢]", "f031");
        mGifMap.put("[难过]", "f032");
        mGifMap.put("[惊讶]", "f033");
        mGifMap.put("[疑问]", "f034");
        mGifMap.put("[睡]", "f035");
        mGifMap.put("[亲亲]", "f036");
        mGifMap.put("[憨笑]", "f037");
        mGifMap.put("[爱情]", "f038");
        mGifMap.put("[衰]", "f039");
        mGifMap.put("[撇嘴]", "f040");
        mGifMap.put("[阴险]", "f041");

        mGifMap.put("[奋斗]", "f042");
        mGifMap.put("[发呆]", "f043");
        mGifMap.put("[右哼哼]", "f044");
        mGifMap.put("[拥抱]", "f045");
        mGifMap.put("[坏笑]", "f046");
        mGifMap.put("[飞吻]", "f047");
        mGifMap.put("[鄙视]", "f048");
        mGifMap.put("[晕]", "f049");
        mGifMap.put("[大兵]", "f050");
        mGifMap.put("[可怜]", "f051");
        mGifMap.put("[强]", "f052");
        mGifMap.put("[弱]", "f053");
        mGifMap.put("[握手]", "f054");
        mGifMap.put("[胜利]", "f055");
        mGifMap.put("[抱拳]", "f056");
        mGifMap.put("[凋谢]", "f057");
        mGifMap.put("[饭]", "f058");
        mGifMap.put("[蛋糕]", "f059");
        mGifMap.put("[西瓜]", "f060");
        mGifMap.put("[啤酒]", "f061");
        mGifMap.put("[飘虫]", "f062");

        mGifMap.put("[勾引]", "f063");
        mGifMap.put("[OK]", "f064");
        mGifMap.put("[爱你]", "f065");
        mGifMap.put("[咖啡]", "f066");
        mGifMap.put("[钱]", "f067");
        mGifMap.put("[月亮]", "f068");
        mGifMap.put("[美女]", "f069");
        mGifMap.put("[刀]", "f070");
        mGifMap.put("[发抖]", "f071");
        mGifMap.put("[差劲]", "f072");
        mGifMap.put("[拳头]", "f073");
        mGifMap.put("[心碎]", "f074");
        mGifMap.put("[太阳]", "f075");
        mGifMap.put("[礼物]", "f076");
        mGifMap.put("[足球]", "f077");
        mGifMap.put("[骷髅]", "f078");
        mGifMap.put("[挥手]", "f079");
        mGifMap.put("[闪电]", "f080");
        mGifMap.put("[饥饿]", "f081");
        mGifMap.put("[困]", "f082");
        mGifMap.put("[咒骂]", "f083");

        mGifMap.put("[折磨]", "f084");
        mGifMap.put("[抠鼻]", "f085");
        mGifMap.put("[鼓掌]", "f086");
        mGifMap.put("[糗大了]", "f087");
        mGifMap.put("[左哼哼]", "f088");
        mGifMap.put("[哈欠]", "f089");
        mGifMap.put("[快哭了]", "f090");
        mGifMap.put("[吓]", "f091");
        mGifMap.put("[篮球]", "f092");
        mGifMap.put("[乒乓球]", "f093");
        mGifMap.put("[NO]", "f094");
        mGifMap.put("[跳跳]", "f095");
        mGifMap.put("[怄火]", "f096");
        mGifMap.put("[转圈]", "f097");
        mGifMap.put("[磕头]", "f098");
        mGifMap.put("[回头]", "f099");
        mGifMap.put("[跳绳]", "f100");
        mGifMap.put("[激动]", "f101");
        mGifMap.put("[街舞]", "f102");
        mGifMap.put("[献吻]", "f103");
        mGifMap.put("[左太极]", "f104");

        mGifMap.put("[右太极]", "f105");
        mGifMap.put("[闭嘴]", "f106");
    }

    /**
     * 将所有实例赋为空
     */
    public void Canaell() {
        MessageManager.getInstance(this).Canaell();
        NoticeManager.getInstance(this).Canaell();
        DepartmentManager.departmentManager = null;
        AreaManager.areaManager = null;
        CartManager.cartManager = null;
        EnumManager.enumManager = null;
        FileManager.fileManager = null;
        MemberManager.memberManager = null;
        OperationTime.operationTime = null;
        ProductFavManager.productFavManager = null;
        ProductManager.privateManager = null;
        ScheduleManager.scheduleManager = null;
        ServiceManager.serviceManager = null;
        GroupManager.groupManager = null;
        UnitPriceManager.unitPriceManager = null;
        VisitManager.getInstance(this).Canaell();
        DisplayManager.getInstance(this).Canaell();
        CompetingManager.getInstance(this).Canaell();
        //关闭数据库连接
        DBManager.getInstance(this).Canaell();
        SQLiteTemplate.getInstance().closeDatabase(null);
    }


    private boolean needExit = true;

    public void setNeed2Exit(boolean bool) {
        needExit = bool;
    }

    public boolean need2Exit() {
        return needExit;
    }

    public void addActivity(Activity activity) {
        list.add(activity);
    }

    public boolean removeActivity() {
        //遍历所有Activity退出
        for (Activity activity : list) {
            activity.finish();
        }
        return true;
    }

    public Activity getActivity() {
        //取出栈顶的Activity
        return list.get(list.size() - 1);
    }

    /**
     * 创建数据库
     */
    public void openDatabase(Context context) {
        DBManager.getInstance(context).openDatabase();
    }


    /**
     * 在覆盖重装程序的情况下，重新设置一次闹钟
     */
    private void reSetAlarm() {
        //进来重新设置一次闹钟
        String Manager_ID = MyApplication.getInstance().getManagerID() + "";
        //避免无登入人时闹钟出错
        if (!Manager_ID.equals("") && Manager_ID != null) {
            new ReSetAlram(this, Manager_ID, 1, 1).setAlarm();
            new ReSetAlram(this, Manager_ID, 2, 2).setAlarm();
            L.e("闹钟进行了重设！");
        }
    }


    public void addMUCInfo(MUCInfo info) {
        // TODO Auto-generated method stub
        L.e("MyApplication addMUCInfo");
        L.e("getRoom = " + info.getRoom());
        L.e("getAccount = " + info.getAccount());
        L.e("getNickname = " + info.getNickname());
    }

    /**
     * @param context
     * @param isShow  是否显示进度条
     * @param str     提示语 ,不要提示可以传null
     */
    public void showDialog(Context context, boolean isShow, String str) {
        ProgressBar_Loading.showProgressBar(context, isShow, str);
    }

    /**
     * 大表情
     */
    public void bigFaceMap() {
        // TODO Auto-generated method stub
        bigFaceMap.put("[折磨]", R.drawable.f_static_084);
        bigFaceMap.put("[抠鼻]", R.drawable.f_static_085);
        bigFaceMap.put("[鼓掌]", R.drawable.f_static_086);
        bigFaceMap.put("[糗大了]", R.drawable.f_static_087);
        bigFaceMap.put("[左哼哼]", R.drawable.f_static_088);
        bigFaceMap.put("[哈欠]", R.drawable.f_static_089);
        bigFaceMap.put("[快哭了]", R.drawable.f_static_090);
        bigFaceMap.put("[吓]", R.drawable.f_static_091);
        bigFaceMap.put("[篮球]", R.drawable.f_static_092);
        bigFaceMap.put("[呲牙]", R.drawable.f_static_000);
        bigFaceMap.put("[调皮]", R.drawable.f_static_001);
        bigFaceMap.put("[流汗]", R.drawable.f_static_002);
        bigFaceMap.put("[偷笑]", R.drawable.f_static_003);
        bigFaceMap.put("[再见]", R.drawable.f_static_004);
        bigFaceMap.put("[敲打]", R.drawable.f_static_005);
        bigFaceMap.put("[擦汗]", R.drawable.f_static_006);
        bigFaceMap.put("[猪头]", R.drawable.f_static_007);
        bigFaceMap.put("[玫瑰]", R.drawable.f_static_008);
        bigFaceMap.put("[流泪]", R.drawable.f_static_009);
    }

    /**
     * 获取屏幕
     *
     * @return
     */
    public DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics;
    }

    public Map<String, Integer> getBigFaceMap() {
        if (!bigFaceMap.isEmpty())
            return bigFaceMap;
        return null;
    }
}

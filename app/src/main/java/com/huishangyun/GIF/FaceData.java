package com.huishangyun.GIF;

import java.util.HashMap;

import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

public class FaceData {
	public static HashMap<String, Integer> gifFaceInfo;

    public static HashMap<String,Integer> staticFaceInfo;

    public static String[] gifFaceName={"\\ji","\\gl"};

  //  public static Integer[] gifFaceId={R.raw.ji,R.raw.gl};

    public static int[] faceId={R.drawable.f_static_000, R.drawable.f_static_001, R.drawable.f_static_002, R.drawable.f_static_003

    	, R.drawable.f_static_004, R.drawable.f_static_005, R.drawable.f_static_006, R.drawable.f_static_009, R.drawable.f_static_010, R.drawable.f_static_011

    	, R.drawable.f_static_012, R.drawable.f_static_013, R.drawable.f_static_014,R.drawable.f_static_015,R.drawable.f_static_017,R.drawable.f_static_018};

    public static String[] faceName={"\\呲牙","\\调皮","\\流汗","\\偷笑","\\再会","\\敲打","\\擦汗","\\流泪","\\掉泪","\\小声","\\炫酷","\\发疯"

             ,"\\委屈","\\便便","\\菜刀","\\微笑","\\","\\害羞"};

    

    //静态块的应用

    static {

        gifFaceInfo=new HashMap<String,Integer>();
        gifFaceInfo = (HashMap<String, Integer>) MyApplication.getInstance().getFaceMap();
        staticFaceInfo = (HashMap<String, Integer>) MyApplication.getInstance().getFaceMap();
        /*staticFaceInfo=new HashMap<String,Integer>();

        for(int i=0;i<gifFaceName.length;i++){

            gifFaceInfo.put(gifFaceName[i], gifFaceId[i]);

        }

        for(int i=0;i<faceId.length;i++){

            staticFaceInfo.put(faceName[i], faceId[i]);

        }
*/
    }

    


}

package com.bjtu.nourriture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mobstat.StatService;
import com.bjtu.nourriture.attention.ListAttentionActivity;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.recipe.CreateRecipeActivity;
import com.bjtu.nourriture.recipe.ListRecipeActivity;
import com.bjtu.nourriture.recipe.SingleRecipeActivity;
import com.bjtu.nourriture.topic.ConnectToServer;
import com.bjtu.nourriture.topic.ListTopicActivity;
import com.bjtu.nourriture.topic.PublishTopicActivity;
import com.bjtu.nourriture.topic.TopicDetailActivity;
import com.bjtu.nourriture.user.LoginActivity;
import com.bjtu.nourriture.user.MoreActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

	//View layout_1,layout_1_1;
	//TextView menu_1_1;
	//String head;
    //ImageView one_oneImageView;
	ArrayList<JSONObject> galleryRecipeList = new ArrayList<JSONObject>();
	DisplayImageOptions options;
	Handler handler;
	Bitmap bitmap;
	String resultString;
	ConnectToServer connect=new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//made by zhangcan
		//设置渠道
		StatService.setAppChannel(this, "testmarket", true);
		// 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
		StatService.setDebugOn(true);
				
		handler = new Handler();
//		layout_1 = this.findViewById(R.id.layout_1);
//		layout_1_1 = this.findViewById(R.id.layout_1_1);
//		menu_1_1 = (TextView)this.findViewById(R.id.menu_1_1);
//		one_oneImageView=(ImageView)this.findViewById(R.id.one_one);
		//逻辑还存在问题，待修改
		Session session = Session.getSession();
		Boolean islogin=(Boolean)session.get("islogin");
		System.out.println("IsLogin:"+islogin);
//		if(islogin!=null){
//			if(islogin){
////				layout_1.setVisibility(View.GONE);
////				layout_1_1.setVisibility(View.VISIBLE);
//				String username = (String) session.get("username");
////				menu_1_1.setText(username);
////				head = "http://123.57.38.31:3000/"+(String) session.get("head");
//				new Thread(){
//					public void run() {
////						bitmap = getHttpBitmap(head);
//					    		 //从网上取图片
//						handler.post(new Runnable() {
//							
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
////								one_oneImageView .setImageBitmap(bitmap);	//设置Bitmap
//							}
//						});
//						
//					};
//				}.start();
//			}
//		}
	
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())  
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()  
        .discCacheFileNameGenerator(new Md5FileNameGenerator())  
        .tasksProcessingOrder(QueueProcessingType.LIFO)  
        .build();  
    	ImageLoader.getInstance().init(config);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		GalleryRecipeTask task = new GalleryRecipeTask();
		@SuppressWarnings("deprecation")
	//	LeftGallery gallery=(LeftGallery) findViewById(R.id.homeRecipeGallery);
		Gallery gallery=(Gallery) findViewById(R.id.homeRecipeGallery);
		task.list = galleryRecipeList;
		task.gallery = gallery;		  
	
		task.activity = this;
		task.execute();
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		try {
			String url="topic/showTopicList?pageNo=1&pageSize=5";
			String method ="GET";
			resultString = connect.testURLConn(url,method);
			
			JSONObject jsonObject = new JSONObject(resultString);
			JSONArray jsonArray = jsonObject.getJSONArray("topics");
			for(int i=0;i<jsonArray.length();i++){   
                JSONObject jo = (JSONObject)jsonArray.opt(i);
                list.add(jo);
            }
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TopicListViewAdapter adapter = new TopicListViewAdapter(this,list);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListenerImpl());
		
		gallery.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
            
            	if(position >= 10){
    				position = position%10;
    			}
            	
            	System.out.println("-----pos----"+position);
            	Intent intent = new Intent();
        		intent.setClass(MainActivity.this, SingleRecipeActivity.class);
        		
        		try {
        			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID, galleryRecipeList.get(position).getString("_id"));
        			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE, galleryRecipeList.get(position).toString());
        			startActivity(intent);
        		} catch (JSONException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
                    
            	}
            
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id){
		System.out.println("----------click--------------");
		System.out.println(pos);
	
		
	}
	
	private class OnItemClickListenerImpl implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?>parent, View view, int position,
				long id) {
			
			System.out.println("----------click--------------");
			System.out.println(list.get(position).toString());
			
			list.get(position);
			Intent intent = new Intent();    
            intent.setClass(MainActivity.this, TopicDetailActivity.class); 		
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL,list.get(position).toString() );
			startActivity(intent);
		
			
			
		}
		
	}
	
	public void toListRecipe(View view){
		Intent intent = new Intent();
		ImageLoader.getInstance().destroy();
		intent.setClass(MainActivity.this,ListRecipeActivity.class);
		startActivity(intent);
	}
	public void toListTopic(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,ListTopicActivity.class);
		startActivity(intent);
	}
	public void toListAttention(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ListAttentionActivity.class);
		startActivity(intent);
	}
	
	public void toMore(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,MoreActivity.class);
		startActivity(intent);
	}
	
	public void toMenu1(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,LoginActivity.class);
		startActivity(intent);
	}
	
	private class RecipeGalleryImageAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		Context context;
		Activity activity;
		ArrayList<JSONObject> list;
		
		public RecipeGalleryImageAdapter(Activity a, ArrayList<JSONObject> d){
	    	activity = a;  
	        list=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

		public void setContext(Context context){
			this.context = context;
		}

		public void setArrayList(ArrayList<JSONObject> list){
			this.list = list;
		}
		
		@Override
		public int getCount(){
				return Integer.MAX_VALUE;  
		}
		
		@Override
		public JSONObject getItem(int positon){
			JSONObject item = list.get(positon);
			return item;
		}
		
		@Override
		public long getItemId(int position){
			if(position >= 10){
				position = position%10;
			}
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View vi=convertView;
			if(position >= 10){
				position = position%10;
			}
			/*if(vi == null){
				vi = ViewScaleType.get(position);
			}*/
			JSONObject oneRecipe = list.get(position);
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.activity_main_recipe_gallery_inner, null);
	  
	        ImageView recipeGalleryImageView=(ImageView)vi.findViewById(R.id.recipeGalleryImage); // 缩略图
	        TextView recipeGalleryName = (TextView)vi.findViewById(R.id.recipeGalleryName);
	          
	        try {
	        	recipeGalleryName.setText(oneRecipe.getString("recipeName"));
	        	ImageLoader.getInstance()
				.displayImage("http://123.57.38.31:3000/"+list.get(position).getString("photo").trim(), recipeGalleryImageView, options, new SimpleImageLoadingListener() {
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        
	        return vi;
		}
	}
	
	class GalleryRecipeTask extends AsyncTask<Object, Object, Object>{
		Gallery gallery;
		MainActivity activity;
		ArrayList<JSONObject> list;
		
		@Override
		protected Object doInBackground(Object... arg0) {
			String recipeRecult = getRecipeGaleryList();
			try {
				JSONObject jsonObject = new JSONObject(recipeRecult);
				JSONArray jsonArray = jsonObject.getJSONArray("root");
				for(int i=0;i<jsonArray.length();i++){   
	                JSONObject jo = (JSONObject)jsonArray.opt(i);
	                list.add(jo);
	            }
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Object result){
			RecipeGalleryImageAdapter adapter = new RecipeGalleryImageAdapter(activity,list);
			gallery.setAdapter(adapter);
			gallery.setSelection(1);
		}
	}
	
	public String getRecipeGaleryList(){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "http://123.57.38.31:3000/service/recipe/listAll?pageNo=1&pageSize=10"));
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
 
            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
	}
	public static Bitmap getHttpBitmap(String url) {
	     URL myFileUrl = null;
	     Bitmap bitmap = null;
	     try {
	          myFileUrl = new URL(url);
	     } catch (MalformedURLException e) {
	          e.printStackTrace();
	     }
	     try {
	          HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
	          conn.setConnectTimeout(0);
	          conn.setDoInput(true);
	          conn.connect();
	          InputStream is = conn.getInputStream();
	          bitmap = BitmapFactory.decodeStream(is);
	          is.close();
	     } catch (IOException e) {
	          e.printStackTrace();
	     }
	     return bitmap;
	}
	
	//made by zhangcan for baidu analytics
	public void onResume() {
		super.onResume();
		System.out.println("FragmentDemoActivity-->onResume");
		handler = new Handler();
//		layout_1 = this.findViewById(R.id.layout_1);
//		layout_1_1 = this.findViewById(R.id.layout_1_1);
//		menu_1_1 = (TextView)this.findViewById(R.id.menu_1_1);
//		one_oneImageView=(ImageView)this.findViewById(R.id.one_one);
		//逻辑还存在问题，待修改
		Session session = Session.getSession();
		Boolean islogin=(Boolean)session.get("islogin");
		System.out.println("IsLogin:"+islogin);
//		if(islogin!=null){
//			if(islogin){
////				layout_1.setVisibility(View.GONE);
////				layout_1_1.setVisibility(View.VISIBLE);
//				String username = (String) session.get("username");
////				menu_1_1.setText(username);
////				head = "http://123.57.38.31:3000/"+(String) session.get("head");
//				new Thread(){
//					public void run() {
////						bitmap = getHttpBitmap(head);
//					    		 //从网上取图片
//						handler.post(new Runnable() {
//							
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
////								one_oneImageView .setImageBitmap(bitmap);	//设置Bitmap
//							}
//						});
//						
//					};
//				}.start();
//			}
//		}
		StatService.onResume(this);
	}

	public void onPause() {
		super.onPause();
		System.out.println("FragmentDemoActivity-->onPause");
		/**
		 * 页面结束（注意： 每个Activity中都需要添加，如果有继承的父Activity中已经添加了该调用，那么子Activity中务必不能添加）
		 * 如果该FragmentActivity包含了几个全页面的fragment，那么可以在fragment里面加入就可以了，这里可以不加入。如果不加入将不会记录该Activity页面。
		 */
		StatService.onPause(this);
	}
	
	public void toCreateRecipe(View view){
		Session session = Session.getSession();
		if(session.get("username") == null || session.get("username").equals("")){
			Toast.makeText(getApplicationContext(), "Sign in please",
				     Toast.LENGTH_SHORT).show();
			Intent intentLogIn = new Intent(this, LoginActivity.class);
			startActivity(intentLogIn);
		}else{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, CreateRecipeActivity.class);
			startActivity(intent);
		}
	}
	
	public void toCreateTopic(View view){
		Session session = Session.getSession();
		if(session.get("username") == null || session.get("username").equals("")){
			Toast.makeText(getApplicationContext(), "Sign in please",
				     Toast.LENGTH_SHORT).show();
			Intent intentLogIn = new Intent(this, LoginActivity.class);
			startActivity(intentLogIn);
		}else{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, PublishTopicActivity.class);
			startActivity(intent);
		}
	}
	
class TopicListViewAdapter extends BaseAdapter {
		
		ArrayList<JSONObject> list;
		DisplayImageOptions options;
		Context context;
		private Activity activity;  
	    //private ArrayList<HashMap<String, String>> data;  
	    private LayoutInflater inflater=null;  
	   
	    
	    public TopicListViewAdapter(Activity a, ArrayList<JSONObject> d){
	    	activity = a;  
	        list=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	       
	    }
	    
		public void setContext(Context context){
			this.context = context;
		}

		public void setArrayList(ArrayList<JSONObject> list){
			this.list = list;
		}
		
		@Override
		public int getCount(){
			return list.size();
		}
		
		@Override
		public JSONObject getItem(int positon){
			JSONObject item = list.get(positon);
			return item;
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.activity_main_topic_list_inner, null);
	  

	        
	        TextView name = (TextView)vi.findViewById(R.id.topic_name); 
	        TextView product_count = (TextView)vi.findViewById(R.id.product_count);
	        // 缩略图  
	          
	        //HashMap<String, String> song = new HashMap<String, String>();  
	        //item = list.get(position);
	          
	        // 设置ListView的相关值   
	        try {
	        	name.setText("#"+list.get(position).getString("topicName")+"#");
	        	String count = list.get(position).getString("upload_count");
	        
	        	product_count.setText(count+" upload product");
	        	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        //hot.setText(song.get(CustomizedListView.KEY_DURATION));
	        
	      
	        return vi;
		}
	}
	
}

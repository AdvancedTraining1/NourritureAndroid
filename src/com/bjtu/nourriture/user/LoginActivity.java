package com.bjtu.nourriture.user;

import org.json.JSONObject;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.MainTabActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.CheckHttpUtil;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.topic.ListTopicActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.GetChars;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	String message;
	String isSuccess;
	Button exitBtn;
	String username;
	String password;
	String url;
	EditText accountText;
	EditText passText;
	ConnectToServer connect;
	ProgressDialog proDia;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		CheckHttpUtil.initIntener(this);
		
		connect = new ConnectToServer();
		accountText = (EditText) this.findViewById(R.id.login_user_edit);
		passText = (EditText) this.findViewById(R.id.login_passwd_edit);
		Button registerButton = (Button) this.findViewById(R.id.login_register_btn);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		Button subButton = (Button) this.findViewById(R.id.login_login_btn);

		subButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				username = accountText.getText().toString();
				password = passText.getText().toString();
				System.out.println("Account:" + username + "---------Password:"+ password);
				proDia = ProgressDialog.show(LoginActivity.this, "Login","It is just login now,please wait!");
				proDia.show();
				new Thread() {
					@Override
					public void run() {
						try {
							loginConnection(username, password);
							// 子线程不能修改main线程，所以必须用handler来对ui线程进行操作
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(LoginActivity.this,isSuccess, Toast.LENGTH_SHORT).show();
								}
							});
						} catch (Exception e) {
						} finally {
							proDia.dismiss();
							//回到之前的操作页
							Intent fromIntent = getIntent();
							if(fromIntent.getStringExtra(Constants.INTENT_EXTRA_MAIN_TAB) != null){
								LoginActivity.this.finish();
								Intent intent = new Intent();
								intent.setClass(LoginActivity.this,MainTabActivity.class);
								startActivity(intent);
							}else{
								LoginActivity.this.finish();
							}
						}
					}
				}.start();
				
			}
		});

		exitBtn = (Button) findViewById(R.id.login_reback_btn);
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
	}
	
	
	public void loginConnection(String username,String password) throws Exception{
		url = "service/userinfo/login";
		StringBuffer params = new StringBuffer();
		// 表单参数与get形式一样
		params.append("username").append("=").append(username)
				.append("&").append("password").append("=")
				.append(password);
		byte[] bytes = params.toString().getBytes();  //变为字节
		message = connect.testURLConn3(url, bytes);
		JSONObject jsonObject = new JSONObject(message);
		isSuccess = jsonObject.getString("message");
		

		// 将登录信息存入session

		String user_account, user_password, user_head,user_id,user_email;
		String userServer = jsonObject.getString("user");
		JSONObject userServer2 = new JSONObject(userServer);
		user_account = userServer2.getString("account");
		user_password = userServer2.getString("password");
		user_head = userServer2.getString("head");
		user_id=userServer2.getString("_id");
		user_email=userServer2.getString("email");
		
		String sessionid =jsonObject.getString("sessionId");
		Session session=Session.getSession();
		session.put("username", user_account);
		session.put("head", user_head);
		session.put("user_id",user_id);
		session.put("emal",user_email);
		session.put("islogin", true);
		session.put("sessionId",sessionid);
	}
	@Override
	public void onBackPressed() {
	// 这里处理逻辑代码，大家注意：该方法仅适用于2.0或更新版的sdk
		System.out.println("onBackPressed!!!!!!!!!!!!!!!!!!!");
	return;
	}

	protected void onPause(){
		super.onPause();
		System.out.println("-----------------------pause");
		//LoginActivity.this.finish();
	}
	
	protected void onStop() {         
        super.onStop();  
        System.out.println("-----------------------stop");
    }
}

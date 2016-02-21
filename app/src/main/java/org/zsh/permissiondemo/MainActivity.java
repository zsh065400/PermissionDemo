package org.zsh.permissiondemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.zsh.permission.callback.IExplain;
import org.zsh.permission.callback.IHandleCallback;
import org.zsh.permission.callback.IParticular;
import org.zsh.permission.handle.Execute;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_CODE = 0x123;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		findViewById(R.id.btnRequest).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				particularPermission();
				dangerousPermission();
			}
		});

//		测试添加悬浮窗
		findViewById(R.id.btnAd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Button b = new Button(MainActivity.this);
				b.setText("测试");
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(MainActivity.this, "点击悬浮窗", Toast.LENGTH_LONG).show();
					}
				});

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSPARENT
				);

				lp.gravity = Gravity.RIGHT | Gravity.TOP;
				lp.setTitle("test");

				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				wm.addView(b, lp);
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void particularPermission() {
		Execute.getInstance(this).reqParticularPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
				getPackageName()
				, new IParticular() {
					@Override
					public void grant() {
						Log.d("权限授权", "-----> 授权");
					}

					@Override
					public void deny() {
						Log.d("权限拒绝", "-----> 拒绝");
					}
				}, REQUEST_CODE);
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void dangerousPermission() {
		//设置提示框内容
		Execute.getInstance(this).setExplain(new IExplain() {
			@Override
			public void showExplain(String[] permissions) {
				String msg = "需要给用户提示的权限有：";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
//		请求多个权限
//		Execute.getInstance(this).requestOnePlus(new String[]{
//				Manifest.permission.WRITE_EXTERNAL_STORAGE,
//				Manifest.permission.SEND_SMS
//		}, new IHandleCallback() {
//
//			@Override
//			public void granted(String[] permissions) {
//				String msg = "授权的权限有：";
//				for (String p : permissions) {
//					msg = msg + p;
//				}
//				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void denied(String[] permissions) {
//				String msg = "拒绝的权限有：";
//				for (String p : permissions) {
//					msg = msg + p;
//				}
//				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
//			}
//		});

//		第三方ROM最好采用每次请求一个的方式
//		请求单个权限
		Execute.getInstance(this).requestOne(
				Manifest.permission.WRITE_EXTERNAL_STORAGE,new HandleRes());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Execute.getInstance(this).handleResult(permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Execute.getInstance(this).handleParticular(requestCode);
	}

//	为了测试第三方ROM的逻辑性
//	实现该接口即可处理请求结果
	private class HandleRes implements IHandleCallback {

		@Override
		public void granted(String[] permission) {
			switch (permission[0]) {
				case Manifest.permission.WRITE_EXTERNAL_STORAGE:
					Toast.makeText(MainActivity.this, "请求写入存储卡成功", Toast.LENGTH_LONG).show();
					Execute.getInstance(MainActivity.this).requestOnePlus(new String[]{
							Manifest.permission.READ_PHONE_STATE,
					}, new HandleRes());
					break;

				case Manifest.permission.READ_PHONE_STATE:
					Toast.makeText(MainActivity.this, "请求读取手机状态成功", Toast.LENGTH_LONG).show();
					break;
			}
		}

		@Override
		public void denied(String[] permission) {
			switch (permission[0]) {
				case Manifest.permission.WRITE_EXTERNAL_STORAGE:
					Toast.makeText(MainActivity.this, "请求写入存储卡失败", Toast.LENGTH_LONG).show();
					Execute.getInstance(MainActivity.this).requestOnePlus(new String[]{
							Manifest.permission.READ_PHONE_STATE,
					}, new HandleRes());
					break;

				case Manifest.permission.READ_PHONE_STATE:
					Toast.makeText(MainActivity.this, "请求读取手机状态失败", Toast.LENGTH_LONG).show();
					break;
			}
		}
	}

}

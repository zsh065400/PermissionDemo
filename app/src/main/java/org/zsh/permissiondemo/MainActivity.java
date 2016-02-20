package org.zsh.permissiondemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
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

	/**
	 * 启动特殊权限：<p>
	 * <ul>
	 * <li>SYSTEM_ALERT_WINDOW：悬浮窗<p>
	 * <li>WRITE_SETTINGS：修改系统设置
	 * </ul>
	 * <h3>方法：设置Action，使用startActivityForResult</h3>
	 */
	private void particularPermission() {
//		Settings.ACTION_MANAGE_OVERLAY_PERMISSION 悬浮窗
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
		intent.setData(Uri.parse("package:" + getPackageName()));
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_CODE);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void dangerousPermission() {
//		int res = checkSelfPermission(Manifest.permission.CAMERA);
//		if (res == PackageManager.PERMISSION_DENIED) {
//			//在出现“不再询问”选项时该方法返回true
//			if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//
//				Toast.makeText(this, "扫描二维码需要开启相机，请您允许该权限", Toast.LENGTH_LONG).show();
//			}
//			requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
//		} else {
//			Toast.makeText(this, "授权结果:已授权", Toast.LENGTH_LONG).show();
//		}

//		Execute.getInstance(this).requestOne(Manifest.permission.CAMERA, new IHandleCallback() {
//			@Override
//			public void granted(String permission) {
//				Toast.makeText(MainActivity.this, "授权结果:已授权", Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void denied(String permission) {
//				Toast.makeText(MainActivity.this, "授权结果:未授权", Toast.LENGTH_LONG).show();
//			}
//		});
		Execute.getInstance(this).setExplain(new IExplain() {
			@Override
			public void showExplain(String[] permissions) {
				Log.d("Explaion ", "some permissions need explain");
			}
		});
		Execute.getInstance(this).requestOnePlus(new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.SEND_SMS
		}, new IHandleCallback() {

			@Override
			public void granted(String[] permissions) {
				int lenght = permissions.length;
				for (int i = 0; i < lenght; i++) {
					Log.d("grant permission ", permissions[i]);
				}
			}

			@Override
			public void denied(String[] permissions) {
				int lenght = permissions.length;
				for (int i = 0; i < lenght; i++) {
					Log.d("deny permission ", permissions[i]);
				}
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//		if (requestCode == REQUEST_CODE) {
//			int grant = grantResults[0];
//			boolean isGranted = grant == PackageManager.PERMISSION_GRANTED;
//			Toast.makeText(this, "授权结果：" + (isGranted ? "已授权" : "未授权"), Toast.LENGTH_LONG).show();
//		}
		Execute.getInstance(this).notifyResult(permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			getPermission();
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void getPermission() {
//		if (Settings.canDrawOverlays(this)) {
		if (Settings.System.canWrite(this)) {
			Toast.makeText(this, "获取权限成功", Toast.LENGTH_LONG).show();
		}
	}
}

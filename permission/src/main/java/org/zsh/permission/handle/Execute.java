package org.zsh.permission.handle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import org.zsh.permission.callback.IExplain;
import org.zsh.permission.callback.IHandleCallback;
import org.zsh.permission.callback.IParticular;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体执行类
 *
 * @author：Administrator
 * @version:v1.2
 */
public class Execute {
	private static final String TAG = Execute.class.getSimpleName();
	public static final int REQUEST_PERMISSION_CODE = 0x44444;
	private IHandleCallback mCallback;
	private IExplain mExplain;
	private Activity mActivity;

	/**
	 * 设置授权结果回调
	 *
	 * @param mCallback 回调实现
	 */
	public void setCallback(IHandleCallback mCallback) {
		this.mCallback = mCallback;
	}

	/**
	 * 设置被拒绝权限的提示信息
	 * <p>当权限被拒绝一次后再次申请时</p>
	 * <p>小米等第三方ROM可能无效。</p>
	 *
	 * @param mExplain 提示回调
	 */
	public void setExplain(IExplain mExplain) {
		this.mExplain = mExplain;
	}

	private static Execute mInstance;

	public static Execute getInstance(Activity activity) {
		if (mInstance == null) {
			synchronized (Execute.class) {
				if (mInstance == null) {
					mInstance = new Execute(activity);
				}
			}
		}
		return mInstance;
	}

	private Execute(Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * 请求多个权限
	 *
	 * @param permissions 多个权限
	 * @param callback    回调函数，处理结果
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void requestOnePlus(@NonNull String[] permissions, IHandleCallback callback) {
		if (permissions.length == 0)
			return;
		if (permissions.length == 1) {
			requestOne(permissions[0], callback);
			return;
		}
		if (callback != null)
			setCallback(callback);
//		需要请求的权限
		List<String> need = new ArrayList<>();
//		拒绝过的权限
		List<String> denied = new ArrayList<>();
//		已经获得授权的权限
		List<String> granted = new ArrayList<>();
		for (String permission : permissions) {
			if (!checkGrantedState(permission)) {
				need.add(permission);
				if (checkDenied(permission))
					denied.add(permission);
			} else {
				granted.add(permission);
			}
		}
		if (!denied.isEmpty()) {
			if (mExplain != null)
				mExplain.showExplain(
						denied.toArray(new String[denied.size()]));
		}
		//判断有无需要请求的权限
		if (!need.isEmpty()) {
			mActivity.requestPermissions(
					need.toArray(new String[need.size()]), REQUEST_PERMISSION_CODE);
		}
		if (!granted.isEmpty()) {
			mCallback.granted(granted.toArray(new String[granted.size()]));
		}
	}

	/**
	 * <p> 请求指定权限</p>
	 * 建议只在需要的位置请求指定权限，且不要一次请求多个权限，造成用户体验的不友好
	 *
	 * @param permission 指定权限
	 * @param callback   处理结果
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void requestOne(@NonNull String permission, IHandleCallback callback) {
		if (callback != null)
			setCallback(callback);
		boolean state = checkGrantedState(permission);
		if (!state) {
			//为true，表明用户拒绝过
			if (checkDenied(permission)) {
				if (mExplain != null)
//					请求提示信息
					mExplain.showExplain(new String[]{permission});
			}
			//请求权限
			mActivity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION_CODE);
		} else {
//			已经授权则回调接口
			mCallback.granted(new String[]{permission});
		}
	}

	/**
	 * 检查权限的授权状态
	 *
	 * @param permission 被检查的权限
	 * @return true为授权，false为未授权
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public boolean checkGrantedState(String permission) {
		int granted = mActivity.checkSelfPermission(permission);
		boolean state = granted == PackageManager.PERMISSION_GRANTED ? true : false;
		Log.d(TAG, "checkGrantedState ---> " + permission + " granted: " + state);
		return state;
	}

	/**
	 * 检查权限是否被拒绝过
	 *
	 * @param permission 被检查的权限
	 * @return 拒绝过返回true，否则返回false
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public boolean checkDenied(String permission) {
		boolean b = mActivity.shouldShowRequestPermissionRationale(permission);
		Log.d(TAG, "checkDenied ---> " + permission + " denied : " + b);
		return b;
	}

	/**
	 * 通知结果，回调接口
	 *
	 * @param permissions
	 * @param grantResults
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void handleResult(String[] permissions, int[] grantResults) {
		int length = permissions.length;
		if (length == 1) {
			String permission = permissions[0];
			boolean result = grantResults[0] == PackageManager.PERMISSION_GRANTED ? true : false;
			if (mCallback != null && result) {
				mCallback.granted(new String[]{permission});
			} else {
				mCallback.denied(new String[]{permission});
			}

		} else {//一个以上的权限
			List<String> grantPer = new ArrayList<>();
			List<String> denyPer = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					grantPer.add(permissions[i]);
				} else {
					denyPer.add(permissions[i]);
				}
			}

			if (!grantPer.isEmpty() && mCallback != null) {
				mCallback.granted(grantPer.toArray(new String[grantPer.size()]));
			}
			if (!denyPer.isEmpty() && mCallback != null) {
				mCallback.denied(denyPer.toArray(new String[denyPer.size()]));
			}
		}
	}

	private int mRequestCode;
	private String mParticularPermission;
	//	Particular Permission Callback
	private IParticular mPPCallback;

	/**
	 * 根据请求码处理权限结果
	 *
	 * @param requestCode 请求代码
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void handleParticular(int requestCode) {
		boolean b;
		if (requestCode == mRequestCode) {
			switch (mParticularPermission) {
				case Settings.ACTION_MANAGE_OVERLAY_PERMISSION:
					b = Settings.canDrawOverlays(mActivity);
					break;
				case Settings.ACTION_MANAGE_WRITE_SETTINGS:
					b = Settings.System.canWrite(mActivity);
					break;

				default:
					return;
			}
			if (mPPCallback != null && b) {
				mPPCallback.grant();
			} else {
				mPPCallback.deny();
			}
		}
	}

	/**
	 * <h3>请求特殊权限，目前支持悬浮窗和写入系统设置</h3>
	 * <li>Settings.ACTION_MANAGE_WRITE_SETTINGS</li>
	 * <li>Settings.Settings.ACTION_MANAGE_OVERLAY_PERMISSION</li>
	 *
	 * @param permission  支持的两个权限之一
	 * @param packageName 请求该权限的包名
	 * @param callback    处理结果回调
	 * @param requestCode 请求代码
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void reqParticularPermission(@NonNull String permission,
	                                    @NonNull String packageName,
	                                    IParticular callback,
	                                    int requestCode) {
		this.mPPCallback = callback;
		this.mRequestCode = requestCode;
		this.mParticularPermission = permission;
		Intent intent = new Intent(permission);
		intent.setData(Uri.parse("package:" + packageName));
//		非6.0+版本没有这两个权限界面
//		if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
		mActivity.startActivityForResult(intent, requestCode);
//		}
	}


}

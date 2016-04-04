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

import org.zsh.permission.callback.IHandleCallback;
import org.zsh.permission.callback.IParticular;
import org.zsh.permission.callback.IRationale;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求类
 *
 * @author zhaoshuhao
 * @version 2.0
 */
public class Request {
	private static final String TAG = "Request";
	private IHandleCallback mCallback;
	private IRationale mRationale;
	private Activity mActivity;

	/**
	 * 设置被拒绝权限的提示信息
	 * <p>当权限被拒绝一次后再次申请时</p>
	 * <p>小米等第三方ROM可能无效。</p>
	 *
	 * @param rationable 提示回调
	 */
	public Request setRationable(IRationale rationable) {
		this.mRationale = rationable;
		return sInstance;
	}

	private volatile static Request sInstance;

	public static Request getInstance(Activity activity) {
		if (sInstance == null) {
			sInstance = new Request(activity);
		}
		return sInstance;
	}

	private Request(Activity activity) {
		this.mActivity = activity;
	}

	private String[] listToArray(List list) {
		int length = list.size();
		String[] s = new String[length];
		return (String[]) list.toArray(s);
	}

	/**
	 * 执行请求权限操作
	 *
	 * @param callback    设置回调接口
	 * @param permissions 请求的权限
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void execute(IHandleCallback callback, int requestCode, @NonNull String... permissions) {
		if (callback != null)
			this.mCallback = callback;
		if (permissions == null || permissions.length == 0)
			throw new IllegalArgumentException("requested permission is null, method can't to do");
		if (permissions.length == 1) {
			one(requestCode, permissions[0]);
		} else {
			many(requestCode, permissions);
		}
	}

	/**
	 * 请求多个权限
	 *
	 * @param permissions 多个权限
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private void many(int requestCode, String... permissions) {
		Log.d(TAG, "many: permissions are --->" + permissions.toString());
//		需要请求的权限
		List<String> need = new ArrayList();
//		拒绝过的权限
		List<String> denied = new ArrayList<>();
//		已经获得授权的权限
		List<String> granted = new ArrayList<>();
		int i = 0;
		for (String permission : permissions) {
			if (!checkState(permission)) {
				need.add(permission);
				if (checkShouldShowRationale(permission))
					denied.add(permission);
			} else {
				granted.add(permission);
			}
			i++;
		}
		if (!denied.isEmpty()) {
			if (mRationale != null)
				mRationale.showRationale(listToArray(denied));
		}
		//判断有无需要请求的权限
		if (!need.isEmpty()) {
			mActivity.requestPermissions(listToArray(need), requestCode);
		}
		if (!granted.isEmpty()) {
			mCallback.granted(listToArray(granted));
		}
	}

	/**
	 * <p> 请求指定权限</p>
	 * 建议只在需要的位置请求指定权限，且不要一次请求多个权限，造成用户体验的不友好
	 *
	 * @param permission 指定权限
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private void one(int requestCode, String permission) {
		Log.d(TAG, "one: the permission is--->" + permission);
		boolean state = checkState(permission);
		String[] s = new String[]{permission};
		if (!state) {
			//为true，表明用户拒绝过
			if (checkShouldShowRationale(permission)) {
				if (mRationale != null)
					//请求提示信息
					mRationale.showRationale(s);
			}
			//请求权限
			mActivity.requestPermissions(s, requestCode);
		} else {
			//已经授权则回调接口
			mCallback.granted(s);
		}
	}

	/**
	 * 检查权限的授权状态
	 *
	 * @param permission 被检查的权限
	 * @return true为授权，false为未授权
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public boolean checkState(String permission) {
		int granted = mActivity.checkSelfPermission(permission);
		boolean state = granted == PackageManager.PERMISSION_GRANTED ? true : false;
		Log.d(TAG, "checkState ---> " + permission + " granted: " + state);
		return state;
	}

	/**
	 * 检查权限是否应该显示提示信息
	 *
	 * @param permission 被检查的权限
	 * @return 拒绝过返回true，否则返回false
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public boolean checkShouldShowRationale(String permission) {
		boolean b = mActivity.shouldShowRequestPermissionRationale(permission);
		Log.d(TAG, "checkShouldShowRationale ---> " + permission + " denied : " + b);
		return b;
	}

	/**
	 * 通知结果，回调接口
	 *
	 * @param permissions
	 * @param grantResults
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult: execute");
		List<String> d = new ArrayList<>();
		List<String> g = new ArrayList<>();
		int i = 0;
		for (String permission : permissions) {
			final int grantResult = grantResults[i];
			if (grantResult == PackageManager.PERMISSION_GRANTED)
				g.add(permission);
			else
				d.add(permission);
			i++;
		}
		if (!g.isEmpty() && mCallback != null) {
			mCallback.granted(listToArray(g));
		}
		if (!d.isEmpty() && mCallback != null) {
			mCallback.denied(listToArray(d));
		}
		recycle();
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
		recycle();
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
	public void requestParticularPermission(@NonNull String permission,
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

	/**
	 * 清理内存，解决重复请求问题
	 */
	private void recycle() {
		if (sInstance != null)
			sInstance = null;
		if (mActivity != null)
			mActivity = null;
		if (mCallback != null)
			mCallback = null;
		if (mRationale != null)
			mRationale = null;
		if (mParticularPermission != null)
			mParticularPermission = null;
		if (mPPCallback != null)
			mPPCallback = null;
	}

}

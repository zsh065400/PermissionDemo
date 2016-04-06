package org.zsh.permission;

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
public class Permission {
	private static final String TAG = Permission.class.getSimpleName();
	private IHandleCallback mCallback;
	private IRationale mRationale;
	private final int requestCode = 0x222;

	static class PermissionHolder {
		public static final Permission sInstance = new Permission();
	}

	/**
	 * 设置被拒绝权限的提示信息
	 * <p>当权限被拒绝一次后再次申请时</p>
	 * <p>小米等第三方ROM可能无效。</p>
	 *
	 * @param rationable 提示回调
	 */
	public void setRationable(IRationale rationable) {
		this.mRationale = rationable;
	}

	public static Permission getInstance() {
		return PermissionHolder.sInstance;
	}

	private Permission() {
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
	public void request(@NonNull IHandleCallback callback, @NonNull Activity activity,
	                    @NonNull String... permissions) {
		this.mCallback = callback;
		if (permissions == null || permissions.length == 0)
			throw new IllegalArgumentException("requested permission is null, method can't to do");
		if (permissions.length == 1) {
			one(activity, permissions[0]);
		} else {
			many(activity, permissions);
		}
	}

	/**
	 * 请求多个权限
	 *
	 * @param permissions 多个权限
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private void many(Activity activity, String... permissions) {
		Log.d(TAG, "many: permissions have " + permissions.length + " permissions");
//		需要请求的权限
		List<String> need = new ArrayList();
//		拒绝过的权限
		List<String> denied = new ArrayList<>();
//		已经获得授权的权限
		List<String> granted = new ArrayList<>();
		for (String permission : permissions) {
			if (!checkState(activity, permission)) {
				need.add(permission);
				if (checkShouldShowRationale(activity, permission))
					denied.add(permission);
			} else {
				granted.add(permission);
			}
		}
		if (!denied.isEmpty() && mRationale != null) {
			mRationale.showRationale(listToArray(denied));
		}
		//判断有无需要请求的权限
		if (!need.isEmpty()) {
			activity.requestPermissions(listToArray(need), requestCode);
		}
		if (!granted.isEmpty() && mCallback != null) {
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
	private void one(Activity activity, String permission) {
		Log.d(TAG, "one: the permission is--->" + permission);
		boolean state = checkState(activity, permission);
		String[] s = new String[]{permission};
		if (!state) {
			//为true，表明用户拒绝过
			if (checkShouldShowRationale(activity, permission)) {
				if (mRationale != null)
					//请求提示信息
					mRationale.showRationale(s);
			}
			//请求权限
			activity.requestPermissions(s, requestCode);
		} else {
			if (mCallback != null)
				mCallback.granted(s);//已经授权则回调接口
		}
	}

	/**
	 * 检查权限的授权状态
	 *
	 * @param permission 被检查的权限
	 * @return true为授权，false为未授权
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public boolean checkState(@NonNull Activity activity, @NonNull String permission) {
		int granted = activity.checkSelfPermission(permission);
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
	public boolean checkShouldShowRationale(@NonNull Activity activity, @NonNull String permission) {
		boolean b = activity.shouldShowRequestPermissionRationale(permission);
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
	public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult: have " + permissions.length + " permissions");
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
		recycleD();
	}

	/**
	 * 清理内存，解决重复请求问题
	 */
	private void recycleD() {
		mCallback = null;
		mRationale = null;
	}


	private void recycleP() {
		mRequestCode = 0;
		mParticularPermission = null;
		mPPCallback = null;
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
	public void onActivityResultHandleParticular(@NonNull Activity activity, @NonNull int requestCode) {
		boolean b;
		if (requestCode == mRequestCode) {
			switch (mParticularPermission) {
				case Settings.ACTION_MANAGE_OVERLAY_PERMISSION:
					b = Settings.canDrawOverlays(activity);
					break;
				case Settings.ACTION_MANAGE_WRITE_SETTINGS:
					b = Settings.System.canWrite(activity);
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
		recycleP();
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
	public void requestParticularPermission(@NonNull Activity activity,
	                                        @NonNull String permission,
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
		activity.startActivityForResult(intent, requestCode);
//		}
	}
}

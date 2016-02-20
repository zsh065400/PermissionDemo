package org.zsh.permission.handle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import org.zsh.permission.callback.IExplain;
import org.zsh.permission.callback.IHandleCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：Administrator
 * @version:1.0
 */
public class Execute {
	private static final String TAG = Execute.class.getSimpleName();
	public static final int REQUEST_PERMISSION_CODE = 0x44444;
	private IHandleCallback mCallback;
	private IExplain mExplain;
	private Activity mActivity;

	public void setCallback(IHandleCallback mCallback) {
		this.mCallback = mCallback;
	}

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
//		for (String permission : permissions) {
//			requestOne(permission, callback);
//		}
		if (callback != null)
			setCallback(callback);
		List<String> needPermission = new ArrayList<>();
		List<String> denyPermission = new ArrayList<>();
		List<String> grantPermission = new ArrayList<>();
		for (String permission : permissions) {
			if (!checkGrantedState(permission)) {
				needPermission.add(permission);
				if (checkDenied(permission))
					denyPermission.add(permission);
			} else {
				grantPermission.add(permission);
			}
		}
		if (!denyPermission.isEmpty()) {
			if (mExplain != null)
				mExplain.showExplain(denyPermission.toArray(new String[denyPermission.size()]));
		}
		//判断有无需要请求的权限
		if (!needPermission.isEmpty()) {
			mActivity.requestPermissions(needPermission.toArray(new String[needPermission.size()]), REQUEST_PERMISSION_CODE);
		}
		if (!grantPermission.isEmpty()) {
			mCallback.granted(grantPermission.toArray(new String[grantPermission.size()]));
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
			//为true，表明用户拒绝过至少一次
			if (checkDenied(permission)) {
				if (mExplain != null)
					mExplain.showExplain(new String[]{permission});
				//请求权限
			}
			mActivity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION_CODE);
		} else {
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
	private boolean checkGrantedState(String permission) {
		int granted = mActivity.checkSelfPermission(permission);
		boolean state = granted == PackageManager.PERMISSION_GRANTED ? true : false;
		Log.d(TAG, "Permission :" + permission + " state is :" + state);
		return state;
	}

	/**
	 * 检查权限是否被拒绝过
	 *
	 * @param permission 被检查的权限
	 * @return 拒绝过返回true，否则返回false
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private boolean checkDenied(String permission) {
		boolean b = mActivity.shouldShowRequestPermissionRationale(permission);
		Log.d(TAG, "Permission :" + permission + (b ? " denied" : " no denied"));
		return b;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void notifyResult(String[] permissions, int[] grantResults) {
		int length = permissions.length;
		if (length == 1) {
			String permission = permissions[0];
			boolean result = grantResults[0] == PackageManager.PERMISSION_GRANTED ? true : false;
			if (mCallback != null && result) {
				mCallback.granted(new String[]{permission});
			} else {
				mCallback.denied(new String[]{permission});
			}
			//一个以上的权限
		} else {
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
}

package org.zsh.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zsh.permission.Permission;
import org.zsh.permission.callback.IHandleCallback;

/**
 * @author：Administrator
 * @version:1.0
 */
public class TestFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_main, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Permission.getInstance().request(
				new IHandleCallback() {
					@Override
					public void granted(String[] permission) {

					}

					@Override
					public void denied(String[] permission) {

					}
				}, getActivity(), Manifest.permission.CAMERA);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Permission.getInstance().onRequestPermissionsResult(permissions, grantResults);
	}

}

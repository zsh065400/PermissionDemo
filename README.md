# PermissionRequest By：赵树豪


为适配“棉花糖”而封装的运行时权限请求处理库。

请求范例:

```java
//请求单个权限
Request.getInstance(this).execute(
				new IHandleCallback() {
					@Override
					public void granted(String[] permission) {
						
					}

					@Override
					public void denied(String[] permission) {

					}
				}, Manifest.permission.CAMERA);
```

```java
//请求多个权限
Request.getInstance(this).execute(new IHandleCallback() {

			@Override
			public void granted(String[] permissions) {
				String msg = "授权的权限有：";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}

			@Override
			public void denied(String[] permissions) {
				String msg = "拒绝的权限有：";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		}, new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.SEND_SMS
		});
```

设置提示框:

```java
Request.getInstance(this).setRationable(new IRationale() {
			@Override
			public void showRationale(String[] permissions) {
				String msg = "需要给用户提示的权限有：";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
```

Fragment：
```java
@Override
public void onViewCreated(View view, @Nullable Bundle 	savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Request.getInstance(getActivity()).execute(
				new IHandleCallback() {
					@Override
					public void granted(String[] permission) {

					}

					@Override
					public void denied(String[] permission) {

					}
				}, Manifest.permission.CAMERA);
	}

@Override
public void onRequestPermissionsResult(int requestCode, 	@NonNull String[] permissions, @NonNull int[] 	grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Request.getInstance(getActivity()).onRequestPermissionsResult(permissions, grantResults);
	}
```

## 支持库信息

- App需要支持Android6.0+版本

- 最低支持版本：>=api19

> - v2.1.0更新：修复BUG，完善权限
> - v1.0更新：初步完成权限库

##使用方法


#####添加依赖(module下build.gradle)
```gradle
dependencies {
    compile 'org.zsh.support:permission:2.1.0'
}
```

#####相关调用
- 在`onResume`调用`Request.getInstance(this).execute(IHandleCallback callback, @NonNull String... permissions)`请求一个或多个权限

- 复写`onRequestPermissionsResult`方法，在其中调用
`Request.getInstance(this).onRequestPermissionsResult(permissions, grantResults)`

- 可添加拒绝过权限的提示信息回调接口`Request.getInstance(this).setRationable(IRationale rationable)`

- 特殊权限复写`onActivityResult`方法，调用`Request.getInstance(this).handleParticular(requestCode)`

#####其它方法：
- checkState(String permission) 判断权限请求状态
- checkShouldShowRationale(String permission) 判断权限是否应该显示提示信息


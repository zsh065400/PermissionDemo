# PermissionExecute By：赵树豪


为适配“棉花糖”而封装的运行时权限请求处理库。

请求范例:

```java
//请求单个权限
Execute.getInstance(this).
	requestOne(Manifest.permission.WRITE_EXTERNAL_STORAGE,
			   new IHandleCallback() {
				@Override
				public void granted(String[] permission) {
					//获得授权
				}

				@Override
				public void denied(String[] permission) {
					//拒绝权限
				}
			});
```

```java
//请求多个权限
Execute.getInstance(this).
	requestOnePlus(new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.SEND_SMS
		}, new IHandleCallback() {

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
		});
```

设置提示框:

```java
Execute.getInstance(this).
	setRationale(new IRationale() {
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
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	  super.onViewCreated(view, savedInstanceState);
		Execute.getInstance(getActivity()).requestOne(
		Manifest.permission.CAMERA,
			new IHandleCallback() {
				@Override
				public void granted(String[] permission) {
				
				}

				@Override
				public void denied(String[] permission) {

				}
			});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
		@NonNull String[] permissions, 
		@NonNull int[] grantResults) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			Execute.getInstance(getActivity()).handleResult(permissions, grantResults);
	}
```

更多内容请关注内部实现和Demo

## 使用情况

> * App需要支持Android6.0+版本

> * 最低支持版本：>=api19

##使用方法


#####添加依赖(module下build.gradle)
```gradle
dependencies {
    compile 'org.zsh.support:permission:1.0.0'
}
```

在`onCreate`或`onResume`中请求权限：
...

1.调用`Execute.getInstance(Activity).requestOne`或`Execute.getInstance(this).requestOnePlus`请求一个或多个权限

*可添加拒绝过权限的提示信息回调接口`Execute.getInstance(Activity).setRationale`

...

2.复写`onRequestPermissionsResult`方法，在其中调用
`Execute.getInstance(this).handleResult(permissions, grantResults)`

*特殊权限复写`onActivityResult`方法，调用`Execute.getInstance(this).handleParticular(requestCode)`

...

3.开始体验简单的权限请求吧！

---
**其他方法：
1.checkGrantedState 判断权限请求状态
2.checkShouldShowRationale 判断权限是否应该显示提示信息


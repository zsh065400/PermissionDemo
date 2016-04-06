# PermissionRequest By：赵树豪


为适配“棉花糖”而封装的运行时权限请求处理库。<br>
支持Activity和Fragment

请求范例:

```java
//请求单个权限
Permission.getInstance().request(
				new IHandleCallback() {
					@Override
					public void granted(String[] permission) {
						
					}

					@Override
					public void denied(String[] permission) {

					}
				}, MainActivity.this,
			Manifest.permission.WRITE_EXTERNAL_STORAGE);
```

```java
//请求多个权限
Permission.getInstance().request(new IHandleCallback() {

			@Override
			public void granted(String[] permissions) {
				
			}

			@Override
			public void denied(String[] permissions) {
				
			}
		}, MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS
		);
```

设置提示框:

```java
Permission.getInstance().setRationable(new IRationale() {
			@Override
			public void showRationale(String[] permissions) {
				
			}
		});
```

在**onRequestPermissionsResult**中回调：

```java
Permission.getInstance().onRequestPermissionsResult(permissions, grantResults);
```

## 支持库信息

- App需要支持Android6.0+版本

- 最低支持版本：>=api19

> - v2.1.2更新：重命名库名称，修复部分问题，去除无用资源
> - v2.1.0更新：修复BUG，完善权限
> - v1.0更新：初步完成权限库

##使用方法


#####添加依赖(module下build.gradle)
```gradle
dependencies {
    compile 'org.zsh.support:permission:2.1.2'
}
```

#####特殊权限(悬浮窗和写入系统设置)
请求：

```java
Permission.getInstance().requestParticularPermission(this, Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
				getPackageName()
				, new IParticular() {
					@Override
					public void grant() {
						
					}

					@Override
					public void deny() {
						
					}
				}, Permission_CODE);
```

回调：

复写`onActivityResult`方法，调用`Permission.getInstance().onActivityResultHandleParticular(this, PermissionCode);`

#####其它方法：
- checkState(@NonNull Activity activity, @NonNull String permission) 判断权限请求状态
- checkShouldShowRationale(@NonNull Activity activity, @NonNull String permission)判断权限是否应该显示提示信息

#####第三方ROM问题解决：
- 小米：请每次请求一个权限，后续延迟500毫秒请求第二个。（官方不支持一次请求多个权限）

欢迎各位反馈使用中的问题。


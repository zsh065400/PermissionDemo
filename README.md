# PermissionsExecute��Android6.0����ʱȨ�ޣ� By:������


�������Ϊ�����䡰�޻��ǡ�����װ������ʱȨ��������⡣

������:

```java
//���󵥸�Ȩ��
Execute.getInstance(this).
	requestOne(Manifest.permission.WRITE_EXTERNAL_STORAGE,
			   new IHandleCallback() {
				@Override
				public void granted(String[] permission) {
					//�����Ȩ
				}

				@Override
				public void denied(String[] permission) {
					//�ܾ�Ȩ��
				}
			});
```

```java
//������Ȩ��
Execute.getInstance(this).
	requestOnePlus(new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.SEND_SMS
		}, new IHandleCallback() {

			@Override
			public void granted(String[] permissions) {
				String msg = "��Ȩ��Ȩ���У�";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}

			@Override
			public void denied(String[] permissions) {
				String msg = "�ܾ���Ȩ���У�";
				for (String p : permissions) {
					msg = msg + p;
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
```

������ʾ��:

```java
Execute.getInstance(this).
	setExplain(new IExplain() {
		@Override
		public void showExplain(String[] permissions) {
			String msg = "��Ҫ���û���ʾ��Ȩ���У�";
			for (String p : permissions) {
				msg = msg + p;
			}
			Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
		}
	});
```

�����������ע�ڲ�ʵ�ֺ�Demo

## ʹ�����

App��Ҫ֧��Android6.0+�汾

���֧�ְ汾��>=api19

##ʹ�÷���

Ŀǰ��ʱ��֧��`Activity`������`Fragment`�Ⱥ���������ӡ�

��`onCreate`��`onResume`������Ȩ�ޣ�
...

1.����`Execute.getInstance(Activity).requestOne`��`Execute.getInstance(this).requestOnePlus`����һ������Ȩ��

*����Ӿܾ���Ȩ�޵���ʾ��Ϣ�ص��ӿ�`Execute.getInstance(Activity).setExplain`

...

2.��д`onRequestPermissionsResult`�����������е���
`Execute.getInstance(this).handleResult(permissions, grantResults)`

*����Ȩ�޸�д`onActivityResult`����������`Execute.getInstance(this).handleParticular(requestCode)`

...

3.��ʼ����򵥵�Ȩ������ɣ�

## ��������

Ŀǰ��δ�ϴ���Jcenter�������ڴ�...

��Project `build.gradle` ������ :

```gradle
repositories {
    jcenter()
}

dependencies {
    compile ''
}
```

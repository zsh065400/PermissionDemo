package org.zsh.permissiondemo;

import android.provider.Settings;

/**
 * <h3>
 * <b>Android中的权限分为：</b>
 * <li>普通权限（Normal）</li>
 * <li>危险权限（Dangerous）</li>
 * <li>特殊权限（Particular）</li>
 * <li>其他权限（Other）</li>
 * </h3>
 * <p/>
 * <h3>除普通权限外，在6.0版本中，新的运行时权限规定：除<b>普通权限</b>外</h3>
 * <h3>其它权限一律需要在运行过程中手动向用户申请</h3>
 * <p/>
 * <b>该类中定义的内容均为需要请求的权限。</b>
 *
 * @author：Administrator
 * @version:1.0
 */
public class PermissionInfo {
	/*危险权限，请求权限前需要在Manifest中声明相应权限*/
	/*同一权限组中的一个权限被授权，其他权限自动获得*/

	/*android.permission-group.CALENDAR  日历权限组*/
	public static final int REQUEST_READ_CALENDAR = 0x98175;
	public static final int REQUEST_WRITE_CALENDAR = 0x98176;

	/*android.permission-group.CAMERA  相机权限组*/
	public static final int REQUEST_CAMERA = 0x98177;

	/*android.permission-group.CONTACTS  联系人权限组*/
	public static final int REQUEST_READ_CONTACTS = 0x98178;
	public static final int REQUEST_WRITE_CONTACTS = 0x98179;
	public static final int REQUEST_GET_ACCOUNTS = 0x98180;

	/*android.permission-group.LOCATION  位置信息权限组*/
	public static final int REQUEST_FINE_LOCATION = 0x98181;
	public static final int REQUEST_COARSE_LOCATION = 0x98182;

	/*android.permission-group.MICROPHONE  麦克风权限组*/
	public static final int REQUEST_RECORD_AUDIO = 0x98183;

	/*android.permission-group.PHONE  手机相关权限组*/
	public static final int REQUEST_READ_PHONE_STATE = 0x98184;
	public static final int REQUEST_CALL_PHONE = 0x98185;
	public static final int REQUEST_READ_CALL_LOG = 0x98186;
	public static final int REQUEST_WRITE_CALL_LOG = 0x98187;
	public static final int REQUEST_ADD_VOICE_MAIL = 0x98188;
	public static final int REQUEST_USE_SIP = 0x98189;
	public static final int REQUEST_PROCESS_OUTGOING_CALLS = 0x98190;

	/*android.permission-group.SENSORS  传感器权限组*/
	public static final int REQUEST_BODY_SENSORS = 0x98191;

	/*android.permission-group.SMS  短信权限组*/
	public static final int REQUEST_SEND_SMS = 0x98192;
	public static final int REQUEST_RECEIVE_SMS = 0x98193;
	public static final int REQUEST_READ_SMS = 0x98194;
	public static final int REQUEST_RECEIVE_WAP_SMS = 0x98195;
	public static final int REQUEST_RECEIVE_MMS = 0x98196;

	/*android.permission-group.STORAGE  存储权限组*/
	public static final int REQUEST_WRITE_EXTORAGE = 0x98197;
	public static final int REQUEST_READ_EXTORAGE = 0x98198;

	/*特殊权限*/
//	使用intent启动，附带报名信息
//	在onActivityResult中处理回调信息
//	分别使用：Settings.canDrawOverlays(this) 和 Settings.System.canWrite(this) 判断授权情况
	public static final String ACTION_ALERT_WINDOW = Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
	public static final String ACTION_WRITE_SETTINGS = Settings.ACTION_MANAGE_WRITE_SETTINGS;

	/*无需请求的权限*/
	/*android.permission.ACCESS_LOCATION_EXTRA_COMMANDS
	android.permission.ACCESS_NETWORK_STATE
	android.permission.ACCESS_NOTIFICATION_POLICY
	android.permission.ACCESS_WIFI_STATE
	android.permission.ACCESS_WIMAX_STATE
	android.permission.BLUETOOTH
	android.permission.BLUETOOTH_ADMIN
	android.permission.BROADCAST_STICKY
	android.permission.CHANGE_NETWORK_STATE
	android.permission.CHANGE_WIFI_MULTICAST_STATE
	android.permission.CHANGE_WIFI_STATE
	android.permission.CHANGE_WIMAX_STATE
	android.permission.DISABLE_KEYGUARD
	android.permission.EXPAND_STATUS_BAR
	android.permission.FLASHLIGHT
	android.permission.GET_ACCOUNTS
	android.permission.GET_PACKAGE_SIZE
	android.permission.INTERNET
	android.permission.KILL_BACKGROUND_PROCESSES
	android.permission.MODIFY_AUDIO_SETTINGS
	android.permission.NFC
	android.permission.READ_SYNC_SETTINGS
	android.permission.READ_SYNC_STATS
	android.permission.RECEIVE_BOOT_COMPLETED
	android.permission.REORDER_TASKS
	android.permission.REQUEST_INSTALL_PACKAGES
	android.permission.SET_TIME_ZONE
	android.permission.SET_WALLPAPER
	android.permission.SET_WALLPAPER_HINTS
	android.permission.SUBSCRIBED_FEEDS_READ
	android.permission.TRANSMIT_IR
	android.permission.USE_FINGERPRINT
	android.permission.VIBRATE
	android.permission.WAKE_LOCK
	android.permission.WRITE_SYNC_SETTINGS
	com.android.alarm.permission.SET_ALARM
	com.android.launcher.permission.INSTALL_SHORTCUT
	com.android.launcher.permission.UNINSTALL_SHORTCUT*/

}

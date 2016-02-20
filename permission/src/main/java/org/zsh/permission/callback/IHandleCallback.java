package org.zsh.permission.callback;

/**
 * @author：Administrator
 * @version:1.0
 */
public interface IHandleCallback {
	/**
	 * 权限授权时回调
	 *
	 * @param permission
	 */
	void granted(String[] permission);


	/**
	 * 权限拒绝时回调
	 *
	 * @param permission
	 */
	void denied(String[] permission);

}

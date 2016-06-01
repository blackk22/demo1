package com.wonhigh.base.util;

/** 
* @ClassName: InputCheckUtils 
* @Description: 输入格式检测工具类
* @author li.xy
* @date 2014-6-19 上午9:59:41  
*/
public class InputCheckUtil {

	/** 
	 * @Description: 验证手机号
	 * @param phoneNo
	 * @return：boolean    
	 */
	public static boolean checkTelephoneNo(String phoneNo) {
		// TODO Auto-generated method stub
		return phoneNo.matches("(0[0-9]{2,3}-?)?([2-9][0-9]{6,7})+(-?[0-9]{1,4})?");
	}

	/** 
	 * @Description: 验证QQ号
	 * @param qq
	 * @return：boolean    
	 */
	public static boolean checkQQ(String qq) {
		// TODO Auto-generated method stub
		return qq.matches("[1-9][0-9]{4,}");
	}

	/** 
	 * @Description: 验证座机号
	 * @param phone
	 * @return：boolean    
	 */
	public static boolean checkMobilePhoneNo(String phone) {
		if (phone.matches("\\d{4}-\\d{8}|\\d{4}-\\d{7}|\\d(3)-\\d(8)")) {
			return true;
		} else if (phone.matches("^1[3|4|5|8]\\d{9}$")) {
			return true;
		}
		return false;
	}

	/** 
	 * @Description: 验证邮政编码
	 * @param post
	 * @return：boolean    
	 */
	public static boolean checkPostalcode(String post) {
		return post.matches("[1-9]\\d{5}(?!\\d)");
	}

	/** 
	 * @Description:  验证邮箱
	 * @param email
	 * @return：boolean    
	 */
	public static boolean checkEmail(String email) {
		return email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	}

}

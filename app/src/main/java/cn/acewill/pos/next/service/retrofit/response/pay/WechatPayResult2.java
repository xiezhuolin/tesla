package cn.acewill.pos.next.service.retrofit.response.pay;

/**
 * Author：Anch
 * Date：2018/7/27 10:28
 * Desc：微信扫码接口结果
 */
public class WechatPayResult2 {

	/**
	 * nonce_str : Sbz3B0cnJcfaPVi0
	 * device_info : 59841423_1
	 * code_url : weixin://wxpay/bizpayurl?pr=RbyZQKm
	 * appid : wx36a5d37dc675175f
	 * sign : 86612F16962545E7A5912DFD8B1F66AA98F782945A700905AD30D827BFF7B827
	 * trade_type : NATIVE
	 * return_msg : OK
	 * result_code : SUCCESS
	 * mch_id : 1317968701
	 * return_code : SUCCESS
	 * prepay_id : wx271027492012200de7ecd9b51842004498
	 */

	private String nonce_str;
	private String device_info;
	private String code_url;
	private String appid;
	private String sign;
	private String trade_type;
	private String return_msg;
	private String result_code;
	private String mch_id;
	private String return_code;
	private String prepay_id;
	private String err_code;

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getCode_url() {
		return code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
}

package cn.acewill.pos.next.service.retrofit.response.pay;

/**
 * Author：Anch
 * Date：2018/7/27 11:06
 * Desc：
 */
public class WechatPayResult3 {

	/**
	 * nonce_str : ZncUayFLdw5BecTb
	 * out_trade_no : 1011532660608521
	 * trade_state : NOTPAY
	 * appid : wx36a5d37dc675175f
	 * sign : 566CD853AC003DE4AEB127E92A61F58283FF373960E2FC910AF943994AA8901A
	 * trade_state_desc : 订单未支付
	 * return_msg : OK
	 * result_code : SUCCESS
	 * mch_id : 1317968701
	 * return_code : SUCCESS
	 */

	private String nonce_str;
	private String out_trade_no;
	private String trade_state;
	private String appid;
	private String sign;
	private String trade_state_desc;
	private String return_msg;
	private String result_code;
	private String mch_id;
	private String return_code;

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
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

	public String getTrade_state_desc() {
		return trade_state_desc;
	}

	public void setTrade_state_desc(String trade_state_desc) {
		this.trade_state_desc = trade_state_desc;
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
}

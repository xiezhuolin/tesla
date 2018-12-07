package cn.acewill.pos.next.service.retrofit.response.pay;

/**
 * Author：Anch
 * Date：2018/7/26 11:02
 * Desc：
 */
public class BaseWechatPayResult {

	/**
	 * result : 0
	 * content : {"transaction_id":"4200000138201807264447489039","nonce_str":"Ga0GU7lwh8KL5q5ZMep626Va6C6Ou22Y","bank_type":"HXB_DEBIT","openid":"oNFKzvxJTK4fil8gv5oSXp_VnZb8","sign":"AB0E03EFB0C2FA7531C4275C170C692F1089E5F39F9CC77FC2B453BF3AA68A32","return_msg":"OK","fee_type":"CNY","mch_id":"1317968701","cash_fee":"2","device_info":"湖人总冠军","out_trade_no":"1011532586554857","cash_fee_type":"CNY","appid":"wx36a5d37dc675175f","total_fee":"2","trade_type":"MICROPAY","result_code":"SUCCESS","attach":"","time_end":"20180726142912","is_subscribe":"Y","return_code":"SUCCESS"}
	 * errmsg : 0
	 */

	private int    result;
	private String content;
	private String errmsg;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}

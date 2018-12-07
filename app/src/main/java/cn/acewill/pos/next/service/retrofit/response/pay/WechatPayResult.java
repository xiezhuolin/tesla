package cn.acewill.pos.next.service.retrofit.response.pay;

/**
 * Author：Anch
 * Date：2018/7/26 10:58
 * Desc：
 */
public class WechatPayResult {

	/**
	 * 成功
	 * transaction_id : 4200000138201807264447489039
	 * nonce_str : Ga0GU7lwh8KL5q5ZMep626Va6C6Ou22Y
	 * bank_type : HXB_DEBIT
	 * openid : oNFKzvxJTK4fil8gv5oSXp_VnZb8
	 * sign : AB0E03EFB0C2FA7531C4275C170C692F1089E5F39F9CC77FC2B453BF3AA68A32
	 * return_msg : OK
	 * fee_type : CNY
	 * mch_id : 1317968701
	 * cash_fee : 2
	 * device_info : 湖人总冠军
	 * out_trade_no : 1011532586554857
	 * cash_fee_type : CNY
	 * appid : wx36a5d37dc675175f
	 * total_fee : 2
	 * trade_type : MICROPAY
	 * result_code : SUCCESS
	 * attach :
	 * time_end : 20180726142912
	 * is_subscribe : Y
	 * return_code : SUCCESS
	 * err_code:PARAM_ERROR
	 * err_code_des:time_expire时间过短，刷卡至少1分钟，其他5分钟
	 */

	/**
	 * 失败
	 * "nonce_str":"yVWzWfuS3xuRH99e",
	 "device_info":"湖人总冠军",
	 "appid":"wx36a5d37dc675175f",
	 "sign":"C7F564D8393F94F1E55B8793C35B2400AC212B6A72E25362FB6AE4435A5CE6B1",
	 "err_code":"PARAM_ERROR",
	 "return_msg":"OK",
	 "result_code":"FAIL",
	 "err_code_des":"time_expire时间过短，刷卡至少1分钟，其他5分钟",
	 "mch_id":"1317968701",
	 "return_code":"SUCCESS"
	 */

	private String transaction_id;
	private String nonce_str;
	private String bank_type;
	private String openid;
	private String sign;
	private String return_msg;
	private String fee_type;
	private String mch_id;
	private String cash_fee;
	private String device_info;
	private String out_trade_no;
	private String cash_fee_type;
	private String appid;
	private String total_fee;
	private String trade_type;
	private String result_code;
	private String attach;
	private String time_end;
	private String is_subscribe;
	private String return_code;

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_code_des() {
		return err_code_des;
	}

	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}

	private String err_code;
	private String err_code_des;

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getCash_fee() {
		return cash_fee;
	}

	public void setCash_fee(String cash_fee) {
		this.cash_fee = cash_fee;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getCash_fee_type() {
		return cash_fee_type;
	}

	public void setCash_fee_type(String cash_fee_type) {
		this.cash_fee_type = cash_fee_type;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
}

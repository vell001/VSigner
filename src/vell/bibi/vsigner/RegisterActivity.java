package vell.bibi.vsigner;

import java.util.HashMap;
import java.util.Map;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.User;
import vell.bibi.vsigner.util.PhoneStateUtil;
import vell.bibi.vsigner.util.StrUtil;
import vell.bibi.vsigner.view.TipsDialog;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

/**
 * 新用户注册
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
@SuppressLint("UseSparseArrays")
public class RegisterActivity extends BaseActivity implements OnFocusChangeListener{
	
	private final static int USERNAME_ERROR = 0100;
	private final static int REALNAME_ERROR = 0200;
	private final static int PASSWORD_ERROR = 0300;
	private final static int CONFIRM_PASSWORD_ERROR = 0400;
	private final static int EMAIL_ERROR = 0500;
	private final static int PHONE_ERROR = 0600;

	private TextView mPhoneIMSITextView;
	
	private EditText mUsernameEditText;
	private EditText mRealnameEditText;
	private EditText mPasswordEditText;
	private EditText mConfirmPasswordEditText;
	private EditText mEmailEditText;
	private EditText mPhoneEditText;
	
	private User mTempUser = new User();
	
	private Map<Integer, String> mErrorMsgMap = new HashMap<Integer, String>();
	
	@Override
	public void setContentView() {
		Log.i(TAG, "RegisterActivity");
		setContentView(R.layout.activity_register);
	}

	@Override
	public void initViews() {
		mPhoneIMSITextView = (TextView) findViewById(R.id.tv_phone_imsi);
		mUsernameEditText = (EditText) findViewById(R.id.et_username);
		mRealnameEditText = (EditText) findViewById(R.id.et_realname);
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		mConfirmPasswordEditText = (EditText) findViewById(R.id.et_confirm_password);
		mEmailEditText = (EditText) findViewById(R.id.et_email);
		mPhoneEditText = (EditText) findViewById(R.id.et_phone);
	}

	@Override
	public void initListeners() {
		mUsernameEditText.setOnFocusChangeListener(this);
		mRealnameEditText.setOnFocusChangeListener(this);
		mPasswordEditText.setOnFocusChangeListener(this);
		mConfirmPasswordEditText.setOnFocusChangeListener(this);
		mEmailEditText.setOnFocusChangeListener(this);
		mPhoneEditText.setOnFocusChangeListener(this);
	}

	@Override
	public void initData() {
		mPhoneIMSITextView.setText(PhoneStateUtil.getPhoneIMSI(mContext));
	}
	
	@Override
	public void onFocusChange(View view, boolean isFocus) {
		String text = null;
		if(!isFocus) { // 动态验证
			switch (view.getId()) {
			case R.id.et_username:
				text = mUsernameEditText.getText().toString().trim();
				if(text.length() < Constants.USERNAME_MIN_LENGTH) {
					String errorMsg = getString(R.string.username_hint);
					mErrorMsgMap.put(USERNAME_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(USERNAME_ERROR);
				}
				break;
			case R.id.et_realname:
				if(StrUtil.isEmpty(mRealnameEditText.getText().toString())) {
					String errorMsg = getString(R.string.realname_hint);
					mErrorMsgMap.put(REALNAME_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(REALNAME_ERROR);
				}
				break;
			case R.id.et_password:
				text = mPasswordEditText.getText().toString().trim();
				if(text.length() < Constants.PASSWORD_MIN_LENGTH) {
					String errorMsg = getString(R.string.password_hint);
					mErrorMsgMap.put(PASSWORD_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(PASSWORD_ERROR);
				}
				break;
			case R.id.et_confirm_password:
				text = mConfirmPasswordEditText.getText().toString().trim();
				String password = mPasswordEditText.getText().toString().trim();
				if(!text.equals(password)) {
					String errorMsg = getString(R.string.confirm_password_error);
					mErrorMsgMap.put(CONFIRM_PASSWORD_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(CONFIRM_PASSWORD_ERROR);
				}
				break;
			case R.id.et_email:
				text = mEmailEditText.getText().toString().trim();
				if(!StrUtil.isEmail(text)) {
					String errorMsg = getString(R.string.email_format_error);
					mErrorMsgMap.put(EMAIL_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(EMAIL_ERROR);
				}
				break;
			case R.id.et_phone:
				text = mPhoneEditText.getText().toString().trim();
				if(!StrUtil.isPhone(text)) {
					String errorMsg = getString(R.string.phone_format_error);
					mErrorMsgMap.put(PHONE_ERROR, errorMsg);
					ShowToast(errorMsg);
				} else {
					mErrorMsgMap.remove(PHONE_ERROR);
				}
				break;
			default:
				break;
			}
		}
	}
	
	public void btn_register_onclick(View view) {
		mTempUser.setIMSI(mPhoneIMSITextView.getText().toString().trim());
		mTempUser.setUsername(mUsernameEditText.getText().toString().trim());
		mTempUser.setRealname(mRealnameEditText.getText().toString().trim());
		mTempUser.setPassword(mPasswordEditText.getText().toString().trim());
		mTempUser.setEmail(mEmailEditText.getText().toString().trim());
		mTempUser.setPhoneNumber(mPhoneEditText.getText().toString().trim());
		String errorMsg = "";
		
		if(!mTempUser.isComplete()) { // 注册信息不完整
			errorMsg += getString(R.string.register_info_not_complete);
		} else if(!mErrorMsgMap.isEmpty()) {
			for (String errorItem : mErrorMsgMap.values()) {
				errorMsg += "1. " + errorItem + "\n";
			}
		}
		
		if(!StrUtil.isEmpty(errorMsg)) { // 出错了
			TipsDialog dialogTips = new TipsDialog(mContext, errorMsg, getString(R.string.ok));
			dialogTips.show();
		} else { // 注册
			showProgressDialog(getString(R.string.request_server));
			
			mTempUser.signUp(this, new SaveListener() {
			    @Override
			    public void onSuccess() { // 注册成功
			    	hideProgressDialog();
			    	TipsDialog dialogTips = new TipsDialog(mContext, getString(R.string.register_success), getString(R.string.ok));
					dialogTips.SetOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface arg0) {
							Intent intent = new Intent(mContext, MainActivity.class);
					    	startActivity(intent);
							finish();
						}
					});
			    	dialogTips.show();
			    }
			    @Override
			    public void onFailure(int code, String msg) { // 注册失败
			    	hideProgressDialog();
			    	new TipsDialog(mContext, getString(R.string.register_error) + ": " + msg, getString(R.string.ok)).show();
			    }
			});
		}
	}
}

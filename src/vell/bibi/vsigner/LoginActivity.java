package vell.bibi.vsigner;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.User;
import vell.bibi.vsigner.view.TipsDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

	private TextView mUsernameTextView;
	private EditText mPasswordEditText;
	
	@Override
	public void setContentView() {
		Log.i(TAG, "LoginActivity");
		setContentView(R.layout.activity_login);
	}

	@Override
	public void initViews() {
		String username = this.getIntent().getStringExtra(Constants.USERNAME_EXTRA);
		mUsernameTextView = (TextView) findViewById(R.id.tv_signer_username);
		mUsernameTextView.setText(username);
		
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void initData() {
		
	}
	
	/**
	 * 响应登录按钮
	 * @param v
	 */
	public void btn_login_onclick(View v) {
		showProgressDialog(getString(R.string.request_server));
		User user = new User();
		user.setUsername(mUsernameTextView.getText().toString());
		user.setPassword(mPasswordEditText.getText().toString());
		user.login(mContext, new SaveListener() {
			@Override
			public void onSuccess() { // 登录成功
				hideProgressDialog();
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailure(int code, String msg) {
				hideProgressDialog();
				TipsDialog dialogTips = new TipsDialog(mContext, getString(R.string.login_error_tips) + ": " + msg, getString(R.string.ok));
				dialogTips.show();
			}
		});
	}

}

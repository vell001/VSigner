package vell.bibi.vsigner;

import vell.bibi.vsigner.util.PhoneStateUtil;
import vell.bibi.vsigner.util.StrUtil;
import vell.bibi.vsigner.view.DialogTips;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 新用户注册
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
public class RegisterActivity extends BaseActivity {

	private TextView mPhoneIMSITextView;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private EditText mConfirmPasswordEditText;
	private EditText mEmailEditText;
	
	private EditText mSchoolEditText;
	private EditText mMajorEditText;
	private EditText mStudentIdEditText;
	
	private DialogTips mDialogTips;
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_register);
	}

	@Override
	public void initViews() {
		mPhoneIMSITextView = (TextView) findViewById(R.id.tv_phone_imsi);
		mUserNameEditText = (EditText) findViewById(R.id.et_username);
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		mConfirmPasswordEditText = (EditText) findViewById(R.id.et_confirm_password);
		mEmailEditText = (EditText) findViewById(R.id.et_email);
		mSchoolEditText = (EditText) findViewById(R.id.et_school);
		mMajorEditText = (EditText) findViewById(R.id.et_major);
		mStudentIdEditText = (EditText) findViewById(R.id.et_student_id);
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void initData() {
		String phoneNumber = PhoneStateUtil.getPhoneIMSI(mContext);
		if(StrUtil.isEmpty(phoneNumber)) { // 获取本机号码失败
			mDialogTips = new DialogTips(mContext, getString(R.string.cannot_get_phone_imsi_tips), getString(R.string.exist));
			mDialogTips.SetOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					finish();
				}
			});
			mDialogTips.show();
			return;
		}
		mPhoneIMSITextView.setText(phoneNumber);
	}

}

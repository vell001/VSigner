package vell.bibi.vsigner.view;

import vell.bibi.vsigner.R;
import vell.bibi.vsigner.SplashActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import cn.bmob.v3.BmobUser;

public class SetFragment extends BaseFragment implements OnClickListener{
	
	private Button mLogoutButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_set, container, false);
	}

	@Override
	public void initViews() {
		mLogoutButton = (Button) findViewById(R.id.btn_logout);
	}

	@Override
	public void initListeners() {
		mLogoutButton.setOnClickListener(this);
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_logout:
			BmobUser.logOut(mContext);
			startActivity(new Intent(mContext, SplashActivity.class));
			getActivity().finish();
			break;

		default:
			break;
		}
	}

}

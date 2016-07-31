package app.com.work.shimonaj.helpdx;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyAuthFragment extends Fragment {

    public CompanyAuthFragment() {
    }

    private EditText mCompanyNameView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_auth, container, false);
        mCompanyNameView = (EditText) rootView.findViewById(R.id.companyUrl);

        Button mNextButton = (Button) rootView.findViewById(R.id.nextBtn);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().registerReceiver(mRefreshingReceiver,
                        new IntentFilter(UpdaterService.VALIDATE_COMPANY));

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString(Config.COMPANY_NAME,"").putString(Config.HELPDX_NAME,"").commit();
                mCompanyNameView.setError(null);
                String companyName = mCompanyNameView.getText().toString();

                if (TextUtils.isEmpty(companyName)) {
                    mCompanyNameView.setError(getString(R.string.error_invalid_company));
                    mCompanyNameView.requestFocus();

                } else {

                    Intent intent = new Intent(getActivity(), UpdaterService.class);
                    intent.putExtra("companyName", companyName);
                    intent.setAction(UpdaterService.VALIDATE_COMPANY);
                    getActivity().startService(intent);
                }
            }
        });
        return rootView;
    }



    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.VALIDATE_COMPANY.equals(intent.getAction())) {
                if (!intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false)) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String helpdesk_name = prefs.getString(Config.HELPDX_NAME, "");
                    //   showProgress(false);
                    mCompanyNameView.setError(null);
                    if (helpdesk_name == "") {
                        mCompanyNameView.setError(getString(R.string.error_company_notfound));
                        mCompanyNameView.requestFocus();
                    } else {
                        ((LoginPageActivity)getActivity()).swapFragment();
                        //  initLoginPage();
                        getActivity().unregisterReceiver(mRefreshingReceiver);
                    }

                }

            }
        }


    };
}

package app.com.work.shimonaj.helpdx;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.w3c.dom.Text;

import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;


public class SignInFragment extends Fragment {
    private static final String TAG = SignInFragment.class.getName();
    private EditText mEmailView;
    private EditText mPasswordView;
    public SignInFragment() {
    }
    InterstitialAd mInterstitialAd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        spinner = (ProgressBar)findViewById(R.id.progressBar1);
//        spinner.setVisibility(View.INVISIBLE);
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

                //new FetchJokeTask().execute(this);
            }
        });

        View rootView =  inflater.inflate(R.layout.fragment_sign_in, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.email);


        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String company_name = prefs.getString(Config.COMPANY_NAME, "");
        if(company_name!=""){
            ((TextView)rootView.findViewById(R.id.companyName)).setText(company_name);
        }

//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        return rootView;
    }
    private boolean doValidate() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean isValid = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) ) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            isValid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            isValid = false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            isValid = false;
        }

        if (!isValid) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return isValid;
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);



    }
    private void attemptLogin() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String helpdesk_name = prefs.getString(Config.HELPDX_NAME,"");
        if (helpdesk_name == "") {

            return;
        }
        if(doValidate()){
            requestNewInterstitial();
            getActivity().registerReceiver(mRefreshingReceiver, new IntentFilter(UpdaterService.VALIDATE_USER));

            prefs.edit().putString(Config.USER_KEY,"").commit();
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            Intent intent = new Intent(getActivity(), UpdaterService.class);
            intent.putExtra("helpdeskname", helpdesk_name);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.setAction(UpdaterService.VALIDATE_USER);
            getActivity().startService(intent);
        }


    }
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.VALIDATE_USER.equals(intent.getAction())) {
                if (!intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false)) {
                if(getActivity()==null){
                    return;
                }

                    //   showProgress(false);
                    if(mEmailView!=null) {
                        mEmailView.setError(null);
                    }
                    int EmpId = Utility.getUserEmpId(getActivity());
                    if (EmpId > 0) {
                        Log.v(TAG, "All is well ***********." + EmpId, null);
                        getActivity().unregisterReceiver(mRefreshingReceiver);
                        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                        startActivity(mainActivity);

                    } else {

                            mEmailView.setError(getString(R.string.error_invalid_credentials));
                            mEmailView.requestFocus();

                    }


                }

            }
        }


    };
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}


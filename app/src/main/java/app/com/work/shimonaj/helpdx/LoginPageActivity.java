package app.com.work.shimonaj.helpdx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ProgressBar;

import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginPageActivity extends FragmentActivity {
    CompanyAuthFragment companyAuthFragment;

    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.initAllFonts(this);
        boolean skipLogin=false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs!=null){
            if(prefs.contains(Config.USER_KEY)){
                skipLogin =true;
            }
        }

        if(skipLogin) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            this.finish();
        }else {


            setContentView(R.layout.activity_login_page);
            companyAuthFragment = new CompanyAuthFragment();


            getFragmentManager().beginTransaction()
                    .add(R.id.loginContainer, companyAuthFragment)
                    .commit();
        }
    }
    public void swapFragment(){
        SignInFragment signInFragment = new SignInFragment();



        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        getFragmentManager().beginTransaction().replace(R.id.loginContainer, signInFragment).addToBackStack(null).commit();


//        getFragmentManager().beginTransaction()
//                .remove(companyAuthFragment)
//                .add(R.id.loginContainer, signInFragment)
//                .addToBackStack(null)
//                .commit();
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}

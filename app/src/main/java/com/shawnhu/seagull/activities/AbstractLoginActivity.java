package com.shawnhu.seagull.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnhu.seagull.R;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoginActivity extends Activity {
    static final protected int SUCCESS_CODE = 0;

    private Intent mTargetIntent;
    /**
     * @param email
     * @return: if SUCCESS_CODE valid; else an error string res id;
     */
    protected int isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return R.string.error_field_required;
        } else if (!email.contains("@")) {
            return R.string.error_invalid_email;
        }

        return SUCCESS_CODE;
    }
    /**
     * @param password
     * @return: if SUCCESS_CODE, valid; else an error string res id;
     */
    protected int isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            return R.string.error_field_required;
        } else if (password.length() < 8) {
            return R.string.error_password_too_short;
        }

        return SUCCESS_CODE;
    }
    /**
     * @param acc
     * @param pwd
     * @return: SUCCESS_CODE or string error res
     */
    protected abstract int asyncLoginUser(String acc, String pwd);

    /**
     * @param acc
     * @param pwd
     * @return: SUCCESS_CODE or string error res
     */
    protected abstract int asyncSignUpUser(String acc, String pwd);
    protected void setTargetIntent(Intent i) {
        mTargetIntent = i;
    }

    private UserLoginORSignupTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView    mEmailView;
    private EditText                mPasswordView;
    private View                    mProgressView;
    private View                    mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLoginORSignup(true);
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginORSignup(true);
            }
        });
        Button mEmailSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginORSignup(false);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_login));
        }
    }

    private void populateAutoComplete() {
        new SetupPhoneEmailAutoCompleteTask().execute(null, null);
    }

    public void attemptLoginORSignup(boolean actionIsSignin) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        int ret;
        // Check for a valid password/email.
        if ((ret = isPasswordValid(password)) != SUCCESS_CODE) {
            mPasswordView.setError(getString(ret));
            focusView = mPasswordView;
            cancel = true;
        }
        if ((ret = isEmailValid(email)) != SUCCESS_CODE) {
            mEmailView.setError(getString(ret));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Utils.hideSoftInputMethod(this, mEmailView);
            Utils.hideSoftInputMethod(this, mPasswordView);
            showProgress(true);
            mAuthTask = new UserLoginORSignupTask(email, password, actionIsSignin);
            mAuthTask.execute((Void) null);
        }
    }

    public void showProgress(final boolean show) {
        Utils.setViewEnabledRecursive(!show, (ViewGroup) mLoginFormView);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //TODO: add phones
    //TODO: set current account's email/phone as default value
    class SetupPhoneEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            // Get all emails from the user's contacts and copy them to a list.
            return Utils.getContactsEmails(getBaseContext());
        }

	    @Override
	    protected void onPostExecute(List<String> emailAddressCollection) {
	       addEmailsToAutoComplete(emailAddressCollection);
	    }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(AbstractLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    class UserLoginORSignupTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;
        private final boolean mActionIsLogin;

        UserLoginORSignupTask(String email, String password, boolean login) {
            mEmail = email;
            mPassword = password;
            mActionIsLogin = login;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (mActionIsLogin) {
                return asyncLoginUser(mEmail, mPassword);
            } else {
                return asyncSignUpUser(mEmail, mPassword);
            }
        }

        @Override
        protected void onPostExecute(final Integer ret) {
            mAuthTask = null;
            showProgress(false);

            if (ret == SUCCESS_CODE) {
                if (mTargetIntent != null) {
                    startActivity(mTargetIntent);
                    if (mActionIsLogin) {
                        finish();
                    }
                } else {
                    Toast.makeText(AbstractLoginActivity.this,
                            "Hmm, you forgot to set the target Home activity.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                mPasswordView.setError(getString(ret));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    static public class Utils {
        static public ArrayList<String> getContactsEmails(Context context) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            Uri     contacts_uri;
            String  projection[]        = null;
            String  selection           = null;
            String  selection_args[]    = null;
            String  sort_order          = null;

            contacts_uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

            ContentResolver cr = context.getContentResolver();
            Cursor emailCur = cr.query(contacts_uri, projection, selection, selection_args, sort_order);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        static public void hideSoftInputMethod(Context context, View v) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        static public void setViewEnabledRecursive(boolean enable, ViewGroup vg) {
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                child.setEnabled(enable);
                if (child instanceof ViewGroup) {
                    setViewEnabledRecursive(enable, (ViewGroup) child);
                }
            }
        }
    }


}


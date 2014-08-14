package com.shawnhu.seagull.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
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
            showProgress(true);
            mAuthTask = new UserLoginORSignupTask(email, password, actionIsSignin);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
            if (VERSION.SDK_INT >= 14) {
                // Use ContactsContract.Profile (API 14+)
                contacts_uri    =
                        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                                             ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
                projection      = new String[] {
                        ContactsContract.CommonDataKinds.Email.DATA,
                        ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                };
                selection       = ContactsContract.Contacts.Data.MIMETYPE + " = ?";
                selection_args  = new String[] {
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                };
                sort_order      = ContactsContract.Contacts.Data.IS_PRIMARY + " DESC";
            } else {
                // Use AccountManager (API 8+)
                contacts_uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            }

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
    }
}


package org.xtimms.kitsune.ui.tools.settings;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.core.helpers.SyncHelper;
import org.xtimms.kitsune.core.services.SyncService;
import org.xtimms.kitsune.utils.LayoutUtils;
import org.xtimms.kitsune.utils.ProgressAsyncTask;
import org.xtimms.kitsune.utils.network.RESTResponse;

@SuppressWarnings("deprecation")
public class AuthLoginFragment extends PreferenceFragment implements View.OnClickListener {
    private EditText mEditLogin;
    private EditText mEditPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_syncauth, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditLogin = view.findViewById(R.id.editLogin);
        mEditPassword = view.findViewById(R.id.editPassword);
        Button mButtonLogin = view.findViewById(R.id.buttonLogin);
        Button mButtonRegister = view.findViewById(R.id.buttonRegister);
        mButtonRegister.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String login = mEditLogin.getText().toString().trim();
        final String password = mEditPassword.getText().toString().trim();
        if (login.isEmpty()) {
            LayoutUtils.showSoftKeyboard(mEditLogin);
            return;
        }
        if (password.isEmpty()) {
            LayoutUtils.showSoftKeyboard(mEditPassword);
            return;
        }
        LayoutUtils.hideSoftKeyboard(mEditPassword);
        new AuthTask((SettingsActivity) getActivity(), view.getId() == R.id.buttonRegister)
                .executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        login,
                        password
                );
    }


    private static class AuthTask extends ProgressAsyncTask<String, Void, RESTResponse> implements DialogInterface.OnCancelListener {


        private final boolean mRegister;

        AuthTask(SettingsActivity activity, boolean isRegister) {
            super(activity);
            mRegister = isRegister;
        }


        @Override
        protected RESTResponse doInBackground(String... strings) {
            try {
                SyncHelper syncHelper = SyncHelper.get(getActivity());
                if (mRegister) {
                    return syncHelper.register(strings[0], strings[1]);
                } else {
                    return syncHelper.authorize(strings[0], strings[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return RESTResponse.fromThrowable(e);
            }
        }

        @Override
        protected void onPostExecute(@NonNull AppBaseActivity activity, RESTResponse restResponse) {
            if (restResponse.isSuccess()) {
                Toast.makeText(activity, R.string.successfully, Toast.LENGTH_SHORT).show();
                ((SettingsActivity)activity).openFragment(new SyncSettingsFragment());
                SyncService.start(activity);
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.auth_failed)
                        .setMessage(restResponse.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            }
        }
    }
}

package org.xtimms.kitsune.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xtimms.kitsune.utils.Constant;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.utils.AppUtils;

public class AboutActivity extends AppBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    private void initView() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_about);
        scroll_about.startAnimation(animation);

        LinearLayout ll_card_about_2_shop = findViewById(R.id.ll_card_about_2_google_play);
        //LinearLayout ll_card_about_2_report = findViewById(R.id.ll_card_about_2_report);
        LinearLayout ll_card_about_2_github = findViewById(R.id.ll_card_about_2_github);
        LinearLayout ll_card_about_2_telegram = findViewById(R.id.ll_card_about_2_telegram);
        LinearLayout ll_card_about_2_4pda = findViewById(R.id.ll_card_about_2_4pda);
        ll_card_about_2_shop.setOnClickListener(this);
        //ll_card_about_2_report.setOnClickListener(this);
        ll_card_about_2_github.setOnClickListener(this);
        ll_card_about_2_telegram.setOnClickListener(this);
        ll_card_about_2_4pda.setOnClickListener(this);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);

        TextView tv_about_version = findViewById(R.id.tv_about_version);
        tv_about_version.setText(AppUtils.getVersionName(this));
        tv_about_version.startAnimation(alphaAnimation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_card_about_2_google_play:
                intent.setData(Uri.parse(Constant.APP_URL));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
            /*case R.id.ll_card_about_2_report:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(Constant.EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_intent));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, getString(R.string.about_not_found_email), Toast.LENGTH_SHORT).show();
                }
                break;*/
            case R.id.ll_card_about_2_github:
                intent.setData(Uri.parse(Constant.GITHUB_URL));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
            case R.id.ll_card_about_2_telegram:
                intent.setData(Uri.parse(Constant.TELEGRAM_URL));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            case R.id.ll_card_about_2_4pda:
                intent.setData(Uri.parse(Constant.PDA_URL));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
        }
    }
}

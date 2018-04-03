package com.emadabel.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    public static final String EXTRA_REVIEW_AUTHOR = "extra_author";
    public static final String EXTRA_REVIEW_CONTENT = "extra_content";

    @BindView(R.id.activity_review_author_tv)
    private
    TextView reviewAuthorTv;
    @BindView(R.id.activity_review_content_tv)
    private
    TextView reviewContentTv;
    @BindView(R.id.review_toolbar)
    private
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String extraReviewAuthor = getIntent().getStringExtra(EXTRA_REVIEW_AUTHOR);
        String extraReviewContent = getIntent().getStringExtra(EXTRA_REVIEW_CONTENT);

        reviewAuthorTv.setText(extraReviewAuthor);
        reviewContentTv.setText(extraReviewContent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

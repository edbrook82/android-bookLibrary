package uk.co.dekoorb.android.booklibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.dekoorb.android.booklibrary.R;
import uk.co.dekoorb.android.booklibrary.db.entity.Book;
import uk.co.dekoorb.android.booklibrary.ui.dialog.AddBookDialogFragment;

public class BookListActivity extends AppCompatActivity
        implements BookListFragment.BookListActionsListener {

    private static final String TAG = "BookListActivity";
    public static final String ADD_DIALOG_TAG = "add_dialog_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        if (savedInstanceState == null) {
            BookListFragment bookListFragment = BookListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_list_frame, bookListFragment, BookListFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onBookSelected(Book book) {
        long bookId = book.getId();
        Intent detailIntent = BookDetailActivity.getIntent(this, bookId);
        startActivity(detailIntent);
    }

    @Override
    public void onAddBookClicked() {
        new AddBookDialogFragment()
                .show(getSupportFragmentManager(), ADD_DIALOG_TAG);
    }
}

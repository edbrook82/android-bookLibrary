package uk.co.dekoorb.android.booklibrary.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import uk.co.dekoorb.android.booklibrary.R;
import uk.co.dekoorb.android.booklibrary.databinding.BookDetailFragmentBinding;
import uk.co.dekoorb.android.booklibrary.db.entity.Book;
import uk.co.dekoorb.android.booklibrary.ui.dialog.EditBookDialogFragment;
import uk.co.dekoorb.android.booklibrary.viewmodel.BookDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends Fragment {
    public static final String TAG = "BookDetailFragment";

    private static final String ARG_BOOK_ID = "book_id";

    private long mBookId;
    private BookDetailViewModel mViewModel;
    private BookDetailFragmentBinding mBinding;

    private BookDetailFragmentActions mListener;

    public interface BookDetailFragmentActions {
        void onBookDeleted();
        void onSearchAmazon(String title);
    }

    public BookDetailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bookId Id of the book to display.
     * @return A new instance of fragment BookDetailFragment.
     */
    public static BookDetailFragment newInstance(long bookId) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBookId = getArguments().getLong(ARG_BOOK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.book_detail_fragment, container, false);
        mBinding.fabEdit.setOnClickListener(fabEditListener);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BookDetailFragmentActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BookDetailFragmentActions");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_read:
                mViewModel.toggleRead(mBinding.getBook());
                break;
            case R.id.action_search_amazon:
                mListener.onSearchAmazon(mBinding.getBook().getTitle());
                break;
            case R.id.action_edit:
                editBook();
                break;
            case R.id.action_delete:
                mViewModel.deleteBook(mBinding.getBook());
                mListener.onBookDeleted();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BookDetailViewModel.class);
        subscribeUI();
    }

    private void subscribeUI() {
        mViewModel.getBook(mBookId).observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    mBinding.setBook(book);
                }
            }
        });
    }

    private View.OnClickListener fabEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editBook();
        }
    };

    private void editBook() {
            EditBookDialogFragment.newInstance(mBookId)
                    .show(getFragmentManager(), EditBookDialogFragment.TAG);
    }
}

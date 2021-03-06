package uk.co.dekoorb.android.booklibrary.ui;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.dekoorb.android.booklibrary.R;
import uk.co.dekoorb.android.booklibrary.databinding.BookListFragmentBinding;
import uk.co.dekoorb.android.booklibrary.db.entity.Book;
import uk.co.dekoorb.android.booklibrary.ui.adapter.BookAdapter;
import uk.co.dekoorb.android.booklibrary.ui.dialog.AddBookDialogFragment;
import uk.co.dekoorb.android.booklibrary.viewmodel.BookListViewModel;

/**
 * BookListFragment is a {@link Fragment} to display a list of books.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {

    public static final String TAG = "BookListFragment";

    public static final String ADD_DIALOG_TAG = "add_dialog_tag";

    private BookAdapter mBookAdapter;
    private BookListFragmentBinding mBinding;
    private BookListViewModel mViewModel;

    private BookListActionsListener mListener;

    public interface BookListActionsListener {
        void onBookSelected(Book book);
    }

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance() {
        return new BookListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BookListActionsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BookListActionsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBookAdapter = new BookAdapter(mBookClickCallback);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.book_list_fragment, container, false);
        mBinding.setIsLoading(true);
        mBinding.booksList.setAdapter(mBookAdapter);
        mBinding.fabAdd.setOnClickListener(fabAddListener);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BookListViewModel.class);
        subscribeUI();
        setSwipeToDelete();
    }

    private void subscribeUI() {
        mViewModel.getBooksList().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                if (books != null) {
                    mBinding.setIsLoading(false);
                    mBookAdapter.setBookList(books);
                } else {
                    mBinding.setIsLoading(true);
                }
                mBinding.executePendingBindings();
            }
        });
    }

    private void setSwipeToDelete() {
        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mViewModel.deleteBookAtPos(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.booksList);
    }

    private final BookClickCallback mBookClickCallback = new BookClickCallback() {
        @Override
        public void onClick(Book book) {
            mListener.onBookSelected(book);
        }

        @Override
        public void onLongClick(final Book book) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_book)
                    .setMessage(book.getTitle())
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.deleteBook(book);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User cancelled
                        }
                    })
                    .create()
                    .show();
        }
    };

    private final View.OnClickListener fabAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AddBookDialogFragment()
                    .show(getFragmentManager(), ADD_DIALOG_TAG);
        }
    };
}

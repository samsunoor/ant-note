package net.mnsam.antnote.feature.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_main.*
import net.mnsam.antnote.R
import net.mnsam.antnote.data.local.entity.Note
import net.mnsam.antnote.feature.detail.DetailNoteActivity
import net.mnsam.antnote.feature.list.recycler.NoteAdapter
import net.mnsam.antnote.feature.list.recycler.RecyclerItemTouchHelper
import net.mnsam.antnote.util.InputMode
import net.mnsam.antnote.util.IntentKeys
import net.mnsam.antnote.util.goGone
import net.mnsam.antnote.util.goVisible
import javax.inject.Inject

class Activity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.onAttach(this)
        Stetho.initializeWithDefaults(this)
        val noteAdapter = NoteAdapter(presenter)
        presenter.attachAdapter(noteAdapter)
        listItem.adapter = noteAdapter
        listItem.layoutManager = LinearLayoutManager(this)

        val swipeListener = object : RecyclerItemTouchHelper.SwipeListener {
            override fun delete(position: Int) {
                presenter.onArchiveNote(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(this, swipeListener))
        itemTouchHelper.attachToRecyclerView(listItem)

        fabNoteAdd.setOnClickListener { presenter.onFabClick() }

    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun observeData(observable: Observable<MutableList<Note>>) {
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<MutableList<Note>>() {
                    override fun onNext(t: MutableList<Note>) = presenter.onLoadedData(t)

                    override fun onError(e: Throwable) {
                        presenter.onErrorLoad("Failed to load notes")
                    }

                    override fun onComplete() {
                    }
                })
    }

    override fun navigateToDetail(id: Long) {
        val intent = Intent(this, DetailNoteActivity::class.java)
        intent.putExtra(IntentKeys.ID_NOTE_KEY, id)
        intent.putExtra(IntentKeys.INPUT_MODE, InputMode.VIEW)
        startActivity(intent)
    }

    override fun navigateToCreate() {
        val intent = Intent(this, DetailNoteActivity::class.java)
        intent.putExtra(IntentKeys.ID_NOTE_KEY, -1L)
        intent.putExtra(IntentKeys.INPUT_MODE, InputMode.EDIT)
        startActivity(intent)
    }

    override fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showEmptyPage() {
        emptyList.goVisible()
        listItem.goGone()
    }

    override fun showList(list: MutableList<Note>) {
        listItem.goVisible()
        emptyList.goGone()
    }

    override fun showSnackbar() {
        val snackbar = Snackbar.make(rootMain, R.string.note_archived, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.undo_archive, { presenter.onRestoreNote() })
        snackbar.show()
    }
}

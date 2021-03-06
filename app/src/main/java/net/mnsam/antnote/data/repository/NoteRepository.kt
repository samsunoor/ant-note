package net.mnsam.antnote.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.mnsam.antnote.data.local.dao.NoteDao
import net.mnsam.antnote.data.local.entity.Note
import net.mnsam.antnote.util.CreateObservable
import java.util.concurrent.Callable
import javax.inject.Inject

/**
 * Created by Mochamad Noor Syamsu on 12/27/17.
 */
class NoteRepository @Inject internal constructor(private val noteDao: NoteDao) {

    fun delete(note: Note) {
        Completable.fromRunnable { noteDao.delete(note) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun getObservableAllNotes(): Observable<MutableList<Note>> {
        return CreateObservable()
                .observable(Callable<MutableList<Note>> { noteDao.getAllNotes() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getObservableNoteDetail(id: Long): Observable<Note> {
        return CreateObservable()
                .observable(Callable<Note> { noteDao.findById(id) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun insertOrUpdate(note: Note) {
        Completable.fromRunnable { noteDao.insertOrReplace(note) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun negate(id: Long) {
        Completable.fromRunnable { noteDao.negateArchive(id) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}
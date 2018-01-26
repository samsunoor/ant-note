package net.mnsam.antnote.feature.list

import net.mnsam.antnote.data.local.entity.Note
import net.mnsam.antnote.data.repository.NoteRepository

/**
 * Created by Mochamad Noor Syamsu on 1/5/18.
 */
class MainPresenterImpl(private val list: MutableList<Note> = mutableListOf(),
                        private val noteRepository: NoteRepository) : MainContract.MainPresenter {

    private lateinit var mainView: MainContract.MainView

    override fun onAttach(view: MainContract.MainView) {
        this.mainView = view
    }

    override fun onErrorLoad(message: String) = mainView.toastMessage(message)

    override fun onLoadedData(list: MutableList<Note>) =
            if (!list.isEmpty()) mainView.showList(list) else mainView.showEmptyPage()

    override fun onResume() = mainView.observeData(noteRepository.getObservableAllNotes())

    override fun onPause() {}

    override fun onListItemClick(position: Int) = mainView.navigateToDetail(list[position].id!!)

    override fun onFabClick() = mainView.navigateToCreate()
}
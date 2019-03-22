package info.mx.tracks.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import info.mx.tracks.R
import info.mx.tracks.databinding.LayoutCommentAddBinding
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.rest.DataManagerApp
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import info.mx.tracks.sqlite.TracksRecord
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

@SuppressLint("StaticFieldLeak")
object CommentHelper {

    private var compositeDisposable: CompositeDisposable? = null

    fun doAddRating(context: Context, trackRec: TracksRecord, mxDatabase: MxDatabase, dataManagerApp: DataManagerApp) {
        compositeDisposable = CompositeDisposable()

        val binding: LayoutCommentAddBinding = LayoutCommentAddBinding
            .inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        // show note if exists
        addDisposable(mxDatabase.commentDao().loadById(trackRec.restId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ comment ->
                Timber.d("comment=" + comment)
                if (comment != null) {
                    binding.comNote.setText(comment.note)
                }
            }, { error ->
                Timber.e(error)
            }
            )
        )

        builder.setTitle(context.getString(R.string.newRating))
        builder.setNegativeButton(android.R.string.cancel) { dialog, which -> }
        builder.setPositiveButton(R.string.done) { dialog, which ->

            addDisposable(mxDatabase.commentDao().commentExistsRx(trackRec.restId, getAndroidID(context), binding.comNote.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap { x ->
                    if (x > 0) {
                        Toast.makeText(context, context.getString(R.string.comment_alredy_exists), Toast.LENGTH_SHORT).show()
                        Single.just(x)
                    } else {
                        var comment = Comment()
                        comment.approved = 0
                        comment.id = System.currentTimeMillis() * -1000 //a negative value means it's not synced
                        comment.trackId = trackRec.restId
                        comment.rating = binding.comRating.rating.toInt()
                        comment.username = binding.comUsername.text.toString()
                        comment.note = binding.comNote.text.toString()
                        comment.country = Locale.getDefault().country
                        comment.changed = System.currentTimeMillis() / 1000
                        comment.androidid = getAndroidID(context)
                        mxDatabase.commentDao().insertAll(comment)
                        if (comment.username != context.getString(R.string.noname)) {
                            val prefs1 = MxPreferences.getInstance()
                            prefs1.edit().putUsername(comment.username.trim()).commit()
                        }

                        Single.just(x)
                    }
                }
                .flatMap { x -> mxDatabase.commentDao().allNonPushed() }
                .toObservable()
                .flatMapIterable { it }
                .flatMap { item -> dataManagerApp.pushRating(item).toObservable() }
                .toList()
                .subscribe({ listUpdate ->
                    Timber.d("updateCount=" + listUpdate.size)
                }, { error ->
                    Timber.e(error)
                }
                )
            )
        }

        val dlg = builder.create()

        val prefs = MxPreferences.getInstance()
        binding.comUsername.setHint(R.string.default_username)
        binding.comUsername.setText(prefs.username)
        dlg.show()
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    private fun removeDisposeable() {
        //we un-subscribe all subscriptions to release all references to views etc.
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = null
    }

    private fun addDisposable(disposable: Disposable) {
        if (compositeDisposable == null) {
            throw Throwable("Disposable can only be added within onResume / onPause lifecycle")
        }

        compositeDisposable!!.add(disposable)
    }

}

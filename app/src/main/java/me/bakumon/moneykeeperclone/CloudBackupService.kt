package me.bakumon.moneykeeperclone

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.snatik.storage.Storage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeperclone.api.Network
import me.bakumon.moneykeeperclone.database.AppDatabase
import me.bakumon.moneykeeperclone.ui.setting.backup.BackupConstant
import me.bakumon.moneykeeperclone.utill.ToastUtils
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * WebDAV 云盘备份
 * @author Bakumon https://bakumon.me
 */
class CloudBackupService : Service() {

    private val mDisposable = CompositeDisposable()
    private var isShowSuccessTip = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isShowSuccessTip = intent.getBooleanExtra(KEY_SHOW_TIP, false)
        backup()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun backup() {
        mDisposable.add(Network.davService().list(BackupConstant.BACKUP_DIR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        it.isSuccessful -> backupUpload()
                        it.code() == 404 -> createDir()
                        else -> onCloudBackupFail(it.message())
                    }
                }
                ) {
                    onCloudBackupFail(it.message)
                }
        )
    }

    private fun createDir() {
        mDisposable.add(Network.davService().createDir(BackupConstant.BACKUP_DIR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        it.isSuccessful -> backupUpload()
                        else -> onCloudBackupFail(it.message())
                    }
                }
                ) {
                    onCloudBackupFail(it.message)
                }
        )
    }

    private fun backupUpload() {
        val storage = Storage(App.instance)
        val path = App.instance.getDatabasePath(AppDatabase.DB_NAME)?.path
        val file = storage.getFile(path)
        val body = RequestBody.create(MediaType.parse("application/octet-stream"), file)

        mDisposable.add(Network.davService().uploadCall(BackupConstant.BACKUP_FILE, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        it.isSuccessful -> onCloudBackupSuccess()
                        else -> onCloudBackupFail(it.message())
                    }
                }
                ) {
                    onCloudBackupFail(it.message)
                }
        )
    }

    private fun onCloudBackupSuccess() {
        if (isShowSuccessTip) {
            ToastUtils.show(getString(R.string.text_auto_backup_success))
        }
        stopSelf()
    }

    private fun onCloudBackupFail(msg: String?) {
        ToastUtils.show(getString(R.string.text_auto_backup_fail) + msg)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }

    companion object {
        private const val KEY_SHOW_TIP = "isShowSuccessTip"

        fun startBackup(context: Context, isShowSuccessTip: Boolean = false) {
            val intent = Intent(context, CloudBackupService::class.java)
            intent.putExtra(KEY_SHOW_TIP, isShowSuccessTip)
            context.startService(intent)
        }
    }
}

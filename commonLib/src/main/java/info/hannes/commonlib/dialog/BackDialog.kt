package info.hannes.commonlib.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import info.hannes.commonlib.R

/**
 * DialogFragment that appears when a User hits the Back-Button.
 */
class BackDialog : DialogFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_back_dialog, container)
        view.findViewById<View>(R.id.login_cancel).setOnClickListener(this)
        view.findViewById<View>(R.id.login_close).setOnClickListener(this)

        dialog?.setTitle(getApplicationName(requireContext()))

        return view
    }

    private fun getApplicationName(context: Context): String {
        val stringId = context.applicationInfo.labelRes
        return context.getString(stringId)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.login_cancel) {
            this.dismiss()
        } else if (id == R.id.login_close) {
            sendToBackground()
            this.dismiss()
        }
    }

    private fun sendToBackground() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        this.startActivity(intent)
    }

}

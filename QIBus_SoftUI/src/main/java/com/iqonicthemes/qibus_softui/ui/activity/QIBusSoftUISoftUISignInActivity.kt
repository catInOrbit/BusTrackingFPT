package com.iqonicthemes.qibus_softui.ui.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.iqonicthemes.qibus_softui.QIBusSoftUIBaseActivity
import com.iqonicthemes.qibus_softui.R

class QIBusSoftUISoftUISignInActivity : QIBusSoftUIBaseActivity(), View.OnClickListener {
    /*variable declaration*/
    private var mBtnContinue: TextView? = null
    private var mEmailSignIn: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.qibus_softui_activity_sign_in)

        initLayouts()
        initializeListeners()


    }

    /* init layout */
    private fun initLayouts() {
        mEmailSignIn = findViewById(R.id.idEmailSignIn)
        mBtnContinue = findViewById(R.id.btnSignIn)
    }

    /* initialize listener */

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeListeners() {

        mBtnContinue!!.setOnClickListener(this)
        mBtnContinue!!.stateListAnimator = null



        mEmailSignIn!!.setOnEditorActionListener { v, _, _ ->
            startActivity(QIBusSoftUISoftUIVerificationActivity::class.java)
            true
        }
    }

    /* validation */
    private fun validate(): Boolean {
        var flag = true
        if (TextUtils.isEmpty(mEmailSignIn!!.text)) {
            flag = false
            showToast(getString(R.string.QIBus_softui_msg_mobile_number))
        }
        return flag
    }

    /* onClick listener */
    override fun onClick(v: View) {
        if (v === mBtnContinue) {
            //  if (validate()) {

            startActivity(QIBusSoftUISoftUIVerificationActivity::class.java)
            //  }
        }
    }
}

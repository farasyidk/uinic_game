package rasyidk.fa.uinic_game

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.util.*
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.widget.Toast
import com.firebase.ui.auth.ErrorCodes
import org.jetbrains.anko.toast
import android.util.Log


class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            startActivity<MainActivity>()
            finish()
        } else {
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    RC_SIGN_IN)

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            AuthUI.IdpConfig.GoogleBuilder().build(),
                                            AuthUI.IdpConfig.EmailBuilder().build()
                                    )
                            )
                            .setLogo(R.drawable.infinity)
                            .setTheme(R.style.LoginTheme)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN
            )
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("pesann", "haha")
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                Log.e("pesann", "succesful login")
//                refresh()
                startActivity<MainActivity>()
                finish()
                return
            } else {
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "sign in cancelled", Toast.LENGTH_SHORT).show()
                    Log.e("pesann", "sign in cancelled")
                    return
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show()
                    Log.e("pesann", "no internet connection");
                    return
                }

                if (response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "unknown error", Toast.LENGTH_SHORT).show()
                    Log.e("pesann", "unknown error");
                    return
                }
            }
            Toast.makeText(this, "unknown_sign_in_response", Toast.LENGTH_SHORT).show()
            Log.e("pesann", "unknown_sign_in_response")
        }
    }

}

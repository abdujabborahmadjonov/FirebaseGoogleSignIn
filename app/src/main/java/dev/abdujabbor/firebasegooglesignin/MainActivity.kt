package dev.abdujabbor.firebasegooglesignin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.abdujabbor.firebasegooglesignin.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val REQUSTID  = 0
    lateinit var auth:FirebaseAuth
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.signinbutton.setOnClickListener {
          val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(R.string.webclient_id.toString()).requestEmail().build()

            val singinClient = GoogleSignIn.getClient(this,options)
            singinClient.signInIntent.also {
                startActivityForResult(it,REQUSTID)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                googleAuthForeFirebase(it)

        }
    }

    private fun googleAuthForeFirebase(account: GoogleSignInAccount) {
       val creditianals = GoogleAuthProvider.getCredential(account.idToken,null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(creditianals).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Succsedfully  logged in ", Toast.LENGTH_SHORT).show()
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
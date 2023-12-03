package com.example.api_assignment4

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var emailText : EditText
    lateinit var passwordText : EditText

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    companion object{
        fun getInstance(): LoginActivity? {
            return LoginActivity()
        }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailText = findViewById(R.id.emailLoginEditText)
        passwordText = findViewById(R.id.passwordEditText)
    }

    fun clearLoginEditText(){

        emailText.setText("")
        passwordText.setText("")
        emailText.requestFocus()
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(emailText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun isValidEmailString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }
    fun loginClicked(view: View) {
        if(emailText.text.isEmpty() || passwordText.text.isEmpty()){

            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setMessage("username and password are required")
            builder.setTitle("Login Failed")
            builder.setPositiveButton("ok",
                DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->

                    dialog?.cancel()

                    //focus shift after alert is close
                    if(emailText.text.isEmpty()){
                        emailText.requestFocus()
                        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.showSoftInput(emailText, InputMethodManager.SHOW_IMPLICIT)
                    }else if(passwordText.text.isEmpty()){
                        passwordText.requestFocus()
                        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.showSoftInput(passwordText, InputMethodManager.SHOW_IMPLICIT)
                    }
                })
            val alertDialog = builder.create()
            runOnUiThread(java.lang.Runnable {alertDialog.show()})

        }
        else if(!isValidEmailString(emailText.text.toString())){

            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setMessage("Invalid Email")
            builder.setTitle("Login Failed")
            builder.setPositiveButton("ok",
                DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->
                    dialog?.cancel()

                    //focus shift after alert is close
                    emailText.requestFocus()
                    val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.showSoftInput(emailText, InputMethodManager.SHOW_IMPLICIT)
                })
            val alertDialog = builder.create()
            runOnUiThread(java.lang.Runnable {alertDialog.show()})
        }
        else {
            try {

                val credentials = HashMap<String, String>().apply {
                    put("email", emailText.text.toString())
                    put("password", passwordText.text.toString())
                }

                //login process
                val service = RetrofitClient.retrofit.create(ApiService::class.java)
                GlobalScope.launch {
                    service.loginUser(credentials).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                if (!loginResponse.token.isNullOrBlank()) {

                                    // Save the token in SharedPreferences or other local storage
                                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                    sharedPreferences.edit().putString("AuthToken", loginResponse.token).apply()
                                    println("User logged in successfully.")
                                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()

                                    // Proceed to the next activity
                                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(i);
                                    clearLoginEditText()

                                } else {
                                    val errorMessage = loginResponse.error
                                    println("Login failed: $errorMessage")
                                    val builder = AlertDialog.Builder(this@LoginActivity)
                                    builder.setMessage(errorMessage)
                                    builder.setTitle("Login Failed")
                                    builder.setPositiveButton("OK",
                                        DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->
                                            dialog?.cancel()
                                        })
                                    runOnUiThread(java.lang.Runnable {val alertDialog = builder.create()
                                        alertDialog.show()})

                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            println("Failed to send request: ${t.message}")

                        }
                    })
                }
            }catch (e: Exception){
                println("failed to login " + e.message)
            }
        }
    }
    fun registerNowClicked(view: View) {
        val i = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(i);
        clearLoginEditText()
    }
}
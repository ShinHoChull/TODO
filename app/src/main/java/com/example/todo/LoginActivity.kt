package com.example.todo

import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.common.Defines
import com.example.todo.util.Custom_SharedPreferences
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private lateinit var mScale: Animation
    private lateinit var mCsp : Custom_SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpInit()
        setUpListener()
        setUpAnim()
    }

    private fun setUpInit() {
        Defines.log("setUpInit!!")
        this.mCsp = Custom_SharedPreferences(applicationContext)

        //카카오 로그인 확인.
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { it, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        //로그인 필요
                        Defines.log("로그인 필요합니다!.")
                    }
                    else {
                        //기타 에러
                        Defines.log("기타에러임.${error.message}.")
                    }
                }
                else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    Defines.log("token->${it.toString()}")

                }
            }
        }
        else {
            //로그인 필요
            Defines.log("로그인 필요합니다!...")
        }
    }

    private fun setUpAnim() {
        mScale = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
    }

    private fun setUpListener() {
        //val keyHash: String =


        loginBt.setOnClickListener {
            loginBt.startAnimation(mScale)
            // 카카오톡으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        logout_bt.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(
                        this,
                        "로그아웃 실패. SDK에서 토큰 삭제됨", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "로그아웃 성공. SDK에서 토큰 삭제됨", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 로그인 공통 callback 구성
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Toast.makeText(
                this,
                "로그인 실패 ${error.message}", Toast.LENGTH_LONG
            ).show()
        } else if (token != null) {
            Toast.makeText(
                this,
                "로그인성공 ${token.accessToken}", Toast.LENGTH_LONG
            ).show()
            mCsp.put("kakaoAuthToken",token.accessToken)

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Toast.makeText(
                        this,
                        "사용자 정보 요청 실패", Toast.LENGTH_LONG
                    ).show()
                } else if (user != null) {
                    Toast.makeText(
                        this,
                        "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}",
                        Toast.LENGTH_LONG
                    ).show()

                    mCsp.put("kakaoEmail",user.kakaoAccount?.email)

                }
            }
        }
    }


}
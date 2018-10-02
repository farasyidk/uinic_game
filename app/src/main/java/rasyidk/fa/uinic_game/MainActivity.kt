package rasyidk.fa.uinic_game

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.media.MediaPlayer
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity(), RewardedVideoAdListener {

    var score = 0
    var imageArray = ArrayList<ImageView>()
    var handler = Handler()
    var runnable = Runnable {  }
    private var index: Int = 0
    private lateinit var d: AlertDialog
    private lateinit var adWin: AlertDialog
    private lateinit var timer: CountDownTimer
    private lateinit var mp: MediaPlayer
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var mInterstitialAd: InterstitialAd
    private val adRequest = AdRequest.Builder().build()
    lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar = supportActionBar
        actionBar?.displayOptions

        imageArray = arrayListOf(imgArdi1, imgArdi2, imgArdi3, imgArdi4, imgArdi5, imgArdi6, imgArdi7, imgArdi8, imgArdi0)

        //auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
        mAuth = FirebaseAuth.getInstance()
        if (mFirebaseUser == null) {
            startActivity<LoginActivity>()
            finish()
            return
        }
        //end auth

        //admob
        MobileAds.initialize(this, "ca-app-pub-2574611168349467~6453002873")
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this@MainActivity)
        rewardedVideoAd.rewardedVideoAdListener = this

        mInterstitialAd = InterstitialAd(this@MainActivity)
        mInterstitialAd.adUnitId = "ca-app-pub-2574611168349467/2382606365"
        //end admob

        mAdView.loadAd(adRequest)
        loadRewardedVideoAd()
        loadRewardedPamfletAd()

        //popup
        val dialogBuilder = AlertDialog.Builder(this)
        val viewGO = layoutInflater.inflate(R.layout.game_over, null)
        dialogBuilder.setView(viewGO)
        dialogBuilder.setTitle("yahh!!")
        dialogBuilder.setPositiveButton("Maen lagi") { dialog, which ->
            mulaiGame()
            Toast.makeText(this, "Mainkann", Toast.LENGTH_SHORT).show()
        }
        dialogBuilder.setNegativeButton("Nyerah") { dialog, which -> dialog.dismiss()
            if (rewardedVideoAd.isLoaded) {
                rewardedVideoAd.show()
            }
        }
        d = dialogBuilder.create()

        val viewWin = layoutInflater.inflate(R.layout.winner, null)
        dialogBuilder.setView(viewWin)
        dialogBuilder.setTitle("yuhuuu!!")
        dialogBuilder.setPositiveButton("Maen lagi") { dialog, which ->
            mulaiGame()
            Toast.makeText(this, "Mainkann", Toast.LENGTH_SHORT).show()
        }
        dialogBuilder.setNegativeButton("Cukup") { dialog, which -> dialog.dismiss()
            if (rewardedVideoAd.isLoaded) {
                rewardedVideoAd.show()
            }
        }
        adWin = dialogBuilder.create()
        //end popup

        btnMulai.setOnClickListener {
            mulaiGame()
        }

    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-2574611168349467/7922707650", adRequest)
    }

    private fun loadRewardedPamfletAd() {
        mInterstitialAd.loadAd(adRequest)
    }

    private fun mulaiGame() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }

        score = 0
        txtScore.text = "Score : 0"
        hideImage()

        timer = object : CountDownTimer(10000, 1000) {
            override fun onFinish() {
                txtTime.text = "Waktu habis!!"
                handler.removeCallbacks(runnable)
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }

                if (score > 0)
                    winning()
                else {
                    gameOver()
                }

            }

            override fun onTick(p0: Long) {
                txtTime.text = "Waktu : ${p0 / 1000}"
            }

        }

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdClosed() {
                timer.start()
                loadRewardedPamfletAd()
            }

        }
    }

    private fun hideImage() {
        runnable = Runnable {
            for (image in imageArray) {
                image.visibility = View.INVISIBLE
            }

            val random = Random()
            index = random.nextInt(8 - 0)
            imageArray[index].visibility = View.VISIBLE
            imageArray[index].setImageResource(R.drawable.ketua_ardi)
            imageArray[index].setOnClickListener {
                timer.onFinish()
                timer.cancel()
            }
            index = random.nextInt(8 - 0)
            imageArray[index].visibility = View.VISIBLE
            imageArray[index].setImageResource(R.drawable.ketua_ari)
            imageArray[index].setOnClickListener {
                hitungScore()
            }

            handler.postDelayed(runnable, 500)
        }
        handler.post(runnable)
    }

    private fun hitungScore() {
        score++

        txtScore.text = "Score : $score"
    }

    private fun gameOver() {
        d.show()
        d.window?.setLayout(600, 650)
        mp = MediaPlayer.create(this@MainActivity, R.raw.game_over_angry)
        mp.start()
    }

    fun winning() {
        adWin.show()
        adWin.window?.setLayout(600, 650)
        mp = MediaPlayer.create(this@MainActivity, R.raw.winner)
        mp.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        when (item.itemId) {
            R.id.out ->
                Logout()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun Logout() {
        mAuth.signOut()
        startActivity<LoginActivity>()
    }

    override fun onRewarded(reward: RewardItem) {
        Toast.makeText(this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}",
                Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }

}

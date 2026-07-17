package com.bbd.tttclient.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bbd.tttclient.databinding.ActivityMainBinding
import com.bbd.tttclient.R
import com.bbd.tttclient.local.game.GameMode
import com.bbd.tttclient.local.game.ai.BotMode


class MainActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init();

    }

    override fun onResume() {
        super.onResume()

    }

    private fun init() {

        binding.playButton.setOnClickListener {

            val intent = Intent(this, GameActivity::class.java)

            val options = arrayOf(getString(R.string.pvp), getString(R.string.pve))

            AlertDialog.Builder(this).
                                setTitle(getString(R.string.choose)).
                                setItems(options) {_, which ->



                                    when(GameMode.entries[which]) {
                                        GameMode.ONE_VS_ONE -> {
                                            intent.putExtra("game-mode", "1vs1")
                                            startActivity(intent)
                                        }
                                        GameMode.PLAYER_VS_BOT -> {
                                            intent.putExtra("game-mode", "PvsBot")
                                            val options = arrayOf(getString(R.string.easyBot), getString(R.string.mediumBot), getString(R.string.hardBot))
                                            AlertDialog.Builder(this).
                                            setTitle(getString(R.string.chooseBot)).
                                            setItems(options) {_, which ->

                                                when(BotMode.entries[which]){
                                                    BotMode.EASY_BOT -> { intent.putExtra("bot-mode", "easy-bot")}
                                                    BotMode.MEDIUM_BOT -> { return@setItems/*intent.putExtra("bot-mode", "medium-bot")*/}
                                                    BotMode.HARD_BOT -> { return@setItems/*intent.putExtra("bot-mode", "hard-bot")*/}
                                                }
                                                startActivity(intent)
                                            }.show()

                                        }
                                        else -> {}
                                    }


                                }.show()
        }
        binding.multiplayerButton.setOnClickListener {
            val intent = Intent(this, LobbyActivity::class.java)
            startActivity(intent)
        }
        binding.exitButton.setOnClickListener {
            finishAffinity()
        }

    }
}
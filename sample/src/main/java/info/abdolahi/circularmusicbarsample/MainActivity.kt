package info.abdolahi.circularmusicbarsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import info.abdolahi.CircularMusicProgressBar
import info.abdolahi.OnCircularSeekBarChangeListener
import info.abdolahi.circularmusicbarsample.databinding.ActivityMainBinding
import java.util.Random

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set progress to 40%
        binding.circularMusicProgressBar.setValue(40f)

        // get user update
        binding.circularMusicProgressBar.setOnCircularBarChangeListener(object :
            OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularBar: CircularMusicProgressBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.d(
                    TAG,
                    "onProgressChanged: progress: $progress / from user? $fromUser"
                )
            }

            override fun onClick(circularBar: CircularMusicProgressBar?) {
                Log.d(TAG, "onClick")
                updateRandomly()
            }

            override fun onLongPress(circularBar: CircularMusicProgressBar?) {
                Log.d(TAG, "onLongPress")
            }
        })

        // get onClick data
        binding.circularMusicProgressBar.setOnClickListener { updateRandomly() }
        binding.play.setOnClickListener {
            binding.circularMusicProgressBar.setIndeterminate(
                !binding.circularMusicProgressBar.isIndeterminated()
            )
        }
    }

    private fun updateRandomly() {
        val random = Random()
        val percent = random.nextFloat() * 100
        binding.circularMusicProgressBar.setValue(percent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

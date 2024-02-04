package com.example.hopperhacks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.FragmentActivity
import com.example.hopperhacks.databinding.ActivityMainBinding
import com.example.hopperhacks.databinding.ActivityUserInputBinding

@ExperimentalGetImage class UserInputActivity: FragmentActivity() {
    var mBinding: ActivityUserInputBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUserInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val heightFeet = binding.heightCmInput.text.toString().toFloat()
            editor.putInt("Age", binding.ageInput.text.toString().toIntOrNull() ?: 0)
            editor.putFloat("Height", heightFeet.toFloat()) // 인치로 저장
            editor.putFloat(
                "Weight",
                binding.weightInput.text.toString().toFloatOrNull() ?: 0f
            ) // 파운드로 저장
            editor.putString("Gender", binding.genderSpinner.selectedItem.toString())
            editor.putString("ActivityLevel", binding.activityLevelSpinner.selectedItem.toString())


            editor.apply()

            // 다음 액티비티로 전환
            val nextActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(nextActivityIntent)
        }
    }


}

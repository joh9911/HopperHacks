package com.example.hopperhacks

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.hopperhacks.databinding.ActivityCameraBinding
import com.example.hopperhacks.databinding.ActivityResultBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalGetImage class ActivityResult: FragmentActivity() {
    var mBinding: ActivityResultBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val nutrition = intent.getStringExtra("nutrition")
        val image = intent.getStringExtra("image")
        val recommendations = intent.getStringExtra("list")
        val list = extractContentToList(recommendations)

        Glide.with(this)
            .load(image)
            .into(binding.productImage)
        val nutritionList = splitNutritionFacts(nutrition)
        addNutritionFactsToScrollView(nutritionList)
        addRecommendationsToScrollView(list)
        binding.productName.text = title

        val (a,b) = calculateBmrAndTdee()

        binding.myNutritionCal.text = "My daily requiremet cal: ${b} kcal"




    }

    fun calculateBmrAndTdee(
    ): Pair<Double, Double> {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val age = sharedPreferences.getInt("Age", 0) // 기본값으로 0을 사용
        val height = sharedPreferences.getFloat("Height", 0f) // 기본값으로 0f를 사용
        val weight = sharedPreferences.getFloat("Weight", 0f) // 기본값으로 0f를 사용
        val gender = sharedPreferences.getString("Gender", "Male") // 기본값으로 "Not specified"를 사용
        val activityLevel = sharedPreferences.getString("ActivityLevel", "Not specified") // 기본값으로 "Not specified"를 사용

        val bmr: Double = if (gender == "male") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }

        val tdeeMultiplier = when (activityLevel) {
            "Low" -> 1.2
            "Moderate" -> 1.55
            "High" -> 1.725
            else -> 1.2 // 기본값으로 적은 활동을 선택
        }

        val tdee = bmr * tdeeMultiplier

        return Pair(bmr, tdee)
    }

    fun extractContentToList(responseBody: String?): List<String> {
        return responseBody?.let {
            val gson = Gson()
            val responseObj = gson.fromJson(it, CameraActivity.Response::class.java)

            // 첫 번째 choice의 message -> content 값을 추출
            if (responseObj.choices.isNotEmpty()) {
                val content = responseObj.choices.first().message.content
                // content를 줄바꿈으로 분리하여 리스트로 변환
                content.split("\n")
            } else {
                emptyList()
            }
        } ?: emptyList() // responseBody가 null인 경우 빈 리스트 반환
    }

    fun addRecommendationsToScrollView(recommendations: List<String>?) {
        // LinearLayout 인스턴스를 찾음
        val recommendationsLinearLayout: LinearLayout = findViewById(R.id.recommendation_linear_layout)

        // 기존의 모든 뷰를 제거 (옵션)
        recommendationsLinearLayout.removeAllViews()

        // 리스트의 각 항목에 대해 TextView를 생성하고 LinearLayout에 추가
        recommendations?.forEach { fact ->
            val textView = TextView(this).apply {
                text = fact
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // 너비를 WRAP_CONTENT로 변경
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 여기에 추가적인 레이아웃 파라미터 설정 가능
                    rightMargin = 8.dpToPx(context) // 오른쪽 마진 추가
                    // 혹은 leftMargin을 사용할 수도 있습니다.
                }
                // TextView 스타일 설정 (옵션)
                setTextAppearance(android.R.style.TextAppearance_Material_Body1)
            }
            recommendationsLinearLayout.addView(textView)
        }
    }


    fun addNutritionFactsToScrollView(nutritionFacts: List<String>?) {
        // LinearLayout 인스턴스를 찾음
        val nutritionFactsLinearLayout: LinearLayout = findViewById(R.id.nutrition_facts_linear_layout)

        // 기존의 모든 뷰를 제거 (옵션)
        nutritionFactsLinearLayout.removeAllViews()

        // 리스트의 각 항목에 대해 TextView를 생성하고 LinearLayout에 추가
        nutritionFacts?.forEach { fact ->
            val textView = TextView(this).apply {
                text = fact
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 여기에 추가적인 레이아웃 파라미터 설정 가능
                    bottomMargin = 8.dpToPx(context) // 예: bottom margin 추가
                }
                // TextView 스타일 설정 (옵션)
                setTextAppearance(android.R.style.TextAppearance_Material_Body1)
            }
            nutritionFactsLinearLayout.addView(textView)
        }
    }

    // dp 값을 px 값으로 변환하는 확장 함수
    fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

    fun splitNutritionFacts(nutritionFacts: String?): List<String>? {
        // 마지막에 있는 마침표(.) 제거
        val cleanedNutritionFacts = nutritionFacts?.trimEnd('.', ' ')

        // 문자열을 ','로 분리하여 리스트로 변환
        return cleanedNutritionFacts?.split(", ")?.filter { it.isNotEmpty() }
    }

}
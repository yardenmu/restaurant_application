package com.example.yarden_mugany

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setUpMainScreen()
        }
    private fun setUpMainScreen() {
        setContentView(R.layout.restaurant_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        val menuItems = getMenuItems()
        val adapter = setUpRecyclerView(recyclerView, menuItems)
        val veganCheckBox = findViewById<CheckBox>(R.id.veganCheckBox)
        setUpVeganMenu(veganCheckBox,menuItems, adapter)
        val reserveBtn = findViewById<Button>(R.id.reserveButton)
        reserveBtn.setOnClickListener {
            setUpReserveScreen()
        }
    }
    private fun setUpRecyclerView(recyclerView: RecyclerView, menuItems: List<MenuItem>) : MenuAdapter{
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter(menuItems)
        recyclerView.adapter = adapter
        return adapter
    }
    private fun getMenuItems() : List<MenuItem>{
        return listOf(
            MenuItem(R.drawable.pizza_margarita,
                this.getString(R.string.pizza_margarita),
                this.getString(R.string.margarita_description),
                this.getString(R.string.margarita_price),true),
            MenuItem(R.drawable.pizza_olives,
                this.getString(R.string.pizza_olives),
                this.getString(R.string.olives_description),
                this.getString(R.string.olives_price), true),
            MenuItem(R.drawable.coca_cola,
                this.getString(R.string.coca_cola),
                this.getString(R.string.coca_description),
                this.getString(R.string.coca_price), false),
            MenuItem(R.drawable.seven_up,
                this.getString(R.string.seven_up),
                this.getString(R.string.seven_up_description),
                this.getString(R.string.seven_up_price), false),
            MenuItem(R.drawable.tomato_pasta,
                this.getString(R.string.tomato_pasta),
                this.getString(R.string.tomato_description),
                this.getString(R.string.tomato_price), true),
            MenuItem(R.drawable.cream_pasta,
                this.getString(R.string.mushroom_cream_pasta),
                this.getString(R.string.mushroom_description),
                this.getString(R.string.mushroom_price), false))
    }
    private fun setUpVeganMenu(veganCheckBox: CheckBox, menuItems: List<MenuItem>, adapter: MenuAdapter){
        veganCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val filterMenuItem = if(isChecked){
                menuItems.filter { it.isVegan }
            }
            else{
                menuItems
            }
            adapter.updateMenu(filterMenuItem)
        }
    }
    private fun setUpReserveScreen(){
        setContentView(R.layout.reserve_table_layout)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            setUpMainScreen()
        }

        val numberSeats = findViewById<NumberPicker>(R.id.numberPicker)
        numberSeats.maxValue = 40
        numberSeats.minValue = 1

        val paymentSpinner = findViewById<Spinner>(R.id.paymentSpinner)
        setUpPaymentSpinner(paymentSpinner)

        val datePickerBtn = findViewById<TextView>(R.id.timePickerBtn)
        var selectedDate = ""
        var selectedTime = ""
        setUpDateAndTimePicker(datePickerBtn) {date, time ->
            selectedDate = date
            selectedTime = time
        }
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        onRadioButtonClicked(radioGroup)
        setUpNameFieldValidation()
        val approveTableBtn = findViewById<Button>(R.id.approveTableBtn)
        approveTableBtn.setOnClickListener {
            val nameInput = findViewById<TextInputLayout>(R.id.textField)
            val selectedPayment = paymentSpinner.selectedItem.toString()
            val name = nameInput.editText?.text.toString()
            val checkedRadioButtonId = radioGroup.checkedRadioButtonId
            when {
                (name.isBlank() || name.length < 2 || name.any {it.isDigit()}) -> nameInput.error = getString(R.string.enter_name)
                (checkedRadioButtonId == -1) -> {
                    Toast.makeText(this, getString(R.string.please_select_special_request), Toast.LENGTH_SHORT).show()
                }
                (datePickerBtn.text.toString().isBlank() || datePickerBtn.text.toString() == getString(R.string.select_date)) ->{
                    Toast.makeText(this, getString(R.string.please_select_a_date), Toast.LENGTH_SHORT).show()
                }
                else-> {
                    val selectedRadioButton = findViewById<RadioButton>(checkedRadioButtonId).text.toString()
                    val dataMap = mapOf(
                        R.id.c_name to name,
                        R.id.c_table to numberSeats.value.toString(),
                        R.id.c_special to selectedRadioButton,
                        R.id.c_payment to selectedPayment,
                        R.id.c_date to selectedDate,
                        R.id.c_time to selectedTime
                    )
                    showReservationDialog(dataMap)
                }
            }
        }
    }
    private fun setUpNameFieldValidation() {
        val nameInput = findViewById<TextInputLayout>(R.id.textField)
        val nameEditText = nameInput.editText
        nameEditText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun afterTextChanged(text: Editable?) {
                if(!text.isNullOrBlank()){
                    nameInput.error = null
                }
            }
        })
    }
    private fun setUpPaymentSpinner(paymentSpinner: Spinner){
        val items = resources.getStringArray(R.array.payment_methods)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            items
        )
        paymentSpinner.setPopupBackgroundResource(android.R.color.white)
        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = items[position]
                val layout = findViewById<ConstraintLayout>(R.id.reserveLayout)
                when (selected) {
                    items[1] -> {
                        layout.setBackgroundColor(ContextCompat.getColor(layout.context, R.color.green_background))
                    }
                    items[2] -> {
                        layout.setBackgroundColor(ContextCompat.getColor(layout.context, R.color.blue_background))
                    }
                    else -> {
                        layout.setBackgroundColor(Color.WHITE)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        paymentSpinner.adapter = adapter
    }
    private fun setUpDateAndTimePicker(datePickerBtn: TextView, onDateTimeSelected: (String,String)-> Unit) {
        var selectedDate = ""
        var selectedTime = ""
        fun openTimePickerDialog(currentHour: Int){
            val timeDialog = layoutInflater.inflate(R.layout.custom_dialog_timepicker,null)
            val container = timeDialog.findViewById<LinearLayout>(R.id.numberPickerContainer)
            container.layoutDirection = View.LAYOUT_DIRECTION_LTR
            val hour = timeDialog.findViewById<NumberPicker>(R.id.numberPickerHour)
            hour.minValue = max(currentHour, 11)
            hour.maxValue = 22
            hour.displayedValues = Array(13) { "%02d".format(it + currentHour) }
            val minute = timeDialog.findViewById<NumberPicker>(R.id.numberPickerMinute)
            minute.minValue = 0
            minute.maxValue = 3
            minute.displayedValues = arrayOf("00", "15", "30", "45")

            val timePickerDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_time))
                .setView(timeDialog)
                .setPositiveButton(getString(R.string.yes)){ _, _ ->
                    val selectedHour = hour.value
                    val selectedMinute = minute.value  * 15
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    datePickerBtn.text = "$selectedDate    $selectedTime"
                    onDateTimeSelected(selectedDate,selectedTime)
                }
                .setNegativeButton(getString(R.string.exit), null)
                .create()
            timePickerDialog.show()
        }
        datePickerBtn.setOnClickListener {
            val dtCalendar = Calendar.getInstance()
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
                val currentDate = Calendar.getInstance()
                val isToday = (year == currentDate.get(Calendar.YEAR) && month == currentDate.get(Calendar.MONTH) &&
                        day == currentDate.get(Calendar.DAY_OF_MONTH))
                if(isToday){
                    val currentHour = max(currentDate.get(Calendar.HOUR_OF_DAY) + 1, 11)
                    openTimePickerDialog(currentHour)
                }
                else{
                    openTimePickerDialog(11)
                }
            }
            val datePickerDialog = DatePickerDialog(this,
                dateListener,
                dtCalendar.get(Calendar.YEAR),
                dtCalendar.get(Calendar.MONTH),
                dtCalendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.datePicker.minDate = dtCalendar.timeInMillis
            datePickerDialog.show()
        }
    }
    private fun onRadioButtonClicked(radioGroup : RadioGroup){
        val isVegan = findViewById<ImageView>(R.id.veganSelectedImage)
        radioGroup.setOnCheckedChangeListener { _, buttonId ->
            when (buttonId) {
                R.id.vegan -> isVegan.visibility = View.VISIBLE
                R.id.notVegan -> isVegan.visibility = View.INVISIBLE
                else -> isVegan.visibility = View.INVISIBLE
            }
        }
    }
    private fun showReservationDialog(dataMap: Map<Int, String>){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_dialog_detail_reservation, null)
        for ((id, value) in dataMap){
            val textView = dialogView.findViewById<TextView>(id)
            textView.text = "${textView.text} $value"
        }
        builder.apply {
            setView(dialogView)
            setCancelable(false)
        }
        val dialog = builder.create()
        val btnYes = dialogView.findViewById<Button>(R.id.yes_btn)
        val btnExit = dialogView.findViewById<Button>(R.id.exit_btn)
        btnYes.setOnClickListener{
            dialog.dismiss()
            playSuccessAnimation()
        }
        btnExit.setOnClickListener{
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    private fun playSuccessAnimation(){
        val circle = findViewById<ImageView>(R.id.img_circle)
        val v = findViewById<ImageView>(R.id.img_v)
        val text = findViewById<TextView>(R.id.thanks)
        circle.alpha = 0f
        circle.alpha = 1f
        v.alpha = 1f
        v.rotation = 0f
        text.visibility = View.VISIBLE
        circle.visibility = View.VISIBLE
        v.visibility = View.VISIBLE
        val fadeInCircle: Animator = ObjectAnimator.ofFloat(circle, "alpha",0f, 1f).setDuration(800)
        val rotateV: Animator =  ObjectAnimator.ofFloat(v, "rotation", 0f, 360f).setDuration(700)
        val fadeOutCircle: Animator = ObjectAnimator.ofFloat(circle,"alpha",1f, 0f).setDuration(800)
        val fadeOutV: Animator = ObjectAnimator.ofFloat(v,"alpha",1f, 0f).setDuration(800)
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeInCircle,rotateV,fadeOutCircle,fadeOutV)
        animatorSet.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setUpMainScreen()
            }
        })
        animatorSet.start()
    }

}



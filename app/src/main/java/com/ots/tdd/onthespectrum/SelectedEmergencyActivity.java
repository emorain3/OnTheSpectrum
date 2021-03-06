package com.ots.tdd.onthespectrum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SelectedEmergencyActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    ScrollView infoScrollView;
    ArrayList<ProfileElement> profArray = new ArrayList<>();
    String telNum = "6784670532"; //4706293412
    public static String toSpeak = "Hello.";
    TextToSpeech ttobj;
    int bodySize;
    int fontChange;
    String scenarioName;
    Button mCallButton;
    String location;


    /**
     * Loads the emergency information stored on the device and formats them for display on the screen.
     * @param  savedInstanceState Bundle
     * @see android.app.Activity
     */
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = getApplicationContext().getSharedPreferences("OnTheSpectrum", Context.MODE_PRIVATE);
        int theme = sharedPref.getInt("colorTheme", R.style.AppTheme);
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_emergency);

        scenarioName = getIntent().getStringExtra("scenario");
        location = getIntent().getStringExtra("location");
        String scenarioInfo = "I am in a " + scenarioName + " emergency.";

        setTextSizes();

        ttobj=new TextToSpeech(this, this);

        String appInfo = "I have nonverbal autism, and I am speaking to you through an application on my phone. ";

        loadProfileInfo();
        String profInfo = "";
        String caretakerPhone = ""; //for appearance when displayed on screen
        String caretakerPhoneSpaces = ""; //for speech to text
        for (ProfileElement profElem : profArray) {
            if (profElem.userInfo != "") {
                if (profElem.infoType.equals("Caretaker Phone Number")) {
                    caretakerPhoneSpaces = "My caretaker can be reached at ";
                    for (int i = 0; i < profElem.userInfo.length(); i++) {
                        //includes spaces to force numbers to be read one at a time
                        caretakerPhoneSpaces += profElem.userInfo.charAt(i) + " ";
                    }
                    caretakerPhoneSpaces +=  "to answer any questions about me  ";

                    caretakerPhone = "\nMy caretaker can be reached at " + profElem.userInfo + " to answer any questions about me.";
                } else {
                    profInfo += "\nMy " + profElem.infoType + " is " + profElem.userInfo + ". ";
                }
            }
        }

        String infoText = "What will be said on your call:\n\nHello. " + appInfo + scenarioInfo + " " + profInfo + caretakerPhone;
        if (location.length() > 0) { infoText += "\nI am located at the GPS coordinates: " + location + "."; }
        toSpeak = "Hello. " + appInfo + scenarioInfo + " " + profInfo + caretakerPhoneSpaces;
        if (location.length() > 0) { toSpeak += "\nI am located at the GPS coordinates " + location + "."; }

        infoScrollView = (ScrollView) findViewById(R.id.infoScrollView);
        TextView infoTextView = new TextView(this);
        infoTextView.setText(infoText);
        infoTextView.setTextSize(bodySize + fontChange);
        infoTextView.setTextColor(Color.BLACK);
        infoScrollView.addView(infoTextView);
        mCallButton = findViewById(R.id.callButton);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateCall(view);
            }
        });
    }



    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            ttobj.setLanguage(Locale.US);
            //ttobj.speak("hello World", 0, null);
        }
        else if (status == TextToSpeech.ERROR)
        {
            Log.e("Text To Speech", "ERROR");
        }
    }


    /**
     * Developer method to test the Android Text-to-Speech
     * @param  v  View
     * @see android.view.View
     */
    public void testVoice(View v) {
        ttobj.speak(toSpeak, 0, null);
    }


    /**
     * Loads the emergency information stored on the device and adds them to a private array.
     */

    private void loadProfileInfo() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("OnTheSpectrum", Context.MODE_PRIVATE);
        String savedProfFields = sharedPref.getString("ProfileFields", null);
        if (null == savedProfFields) {
            profArray.add(new ProfileElement("Name", "", 0));
            profArray.add(new ProfileElement("Gender", "", 1));
            profArray.add(new ProfileElement("Age", "", 2));
            profArray.add(new ProfileElement("Phone Number", "", 3));
            profArray.add(new ProfileElement("Home Address", "", 4));
            profArray.add(new ProfileElement("Caretaker Phone Number", "", 5));
        } else {
            String[] fields = savedProfFields.split(";;");
            for (int i = 0; i < fields.length; i++) {
                String info = sharedPref.getString(fields[i], "");
                profArray.add(new ProfileElement(fields[i], info, i));
            }
        }
    }

    /**
     * Initiates call to 911 and records call information to device
     * @param  view  View
     * @see android.view.View
     */
    public void initiateCall(View view) {
        saveLog();
        Intent callIntent = new Intent(SelectedEmergencyActivity.this, TestVoiceActivity.class);
        callIntent.putExtra("location", location);
        startActivity(callIntent);
    }


//    public void initiateCall(View v) {
//
//        EditText txtPhn = (EditText)findViewById(R.id.phoneNumberEditText);
//        telNum = txtPhn.getText().toString();
//        if (telNum.length() == 10) {
//            boolean valid = true;
//            for (int i = 0; i < 10; i++) {
//                if(!Character.isDigit(telNum.charAt(i))) {
//                    valid = false;
//                }
//            }
//            if (valid) {
//                // Save Call to Log
//                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("OnTheSpectrum", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPref.edit();
//                String savedCallLog = sharedPref.getString("CallLog", null);
//                if (savedCallLog == null) {
//                    savedCallLog = "";
//                }
//
//                //String timezone = TimeZone.getDefault().getDisplayName();
//                //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                Calendar rightNow = Calendar.getInstance();
//                int minute = rightNow.get(Calendar.MINUTE);
//                int hour = rightNow.get(Calendar.HOUR);
//                String isAM = "AM";
//                if (hour >= 12) {
//                    hour -= 12;
//                    isAM = "PM";
//                }
//                if (hour == 0) {
//                    hour = 12;
//                }
//                String currTime = "";
//                if (hour < 10) {
//                    currTime += "0";
//                }
//                currTime = currTime + hour + ":";
//                if (minute < 10) {
//                    currTime += "0";
//                }
//                currTime = currTime + minute + " " + isAM + " (" + rightNow.getTimeZone().getDisplayName() + ")";
//                //int month = rightNow.get(Calendar.MONTH);
//                int day = rightNow.get(Calendar.DAY_OF_MONTH);
//                String month = rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
//                String currDate = month + " " + day + ", " + rightNow.get(Calendar.YEAR);
//
//                // add scenario to list of call logs
//                //savedCallLog += currDate + "||" + currTime + "||" + scenarioInfo + ";;";
//                savedCallLog += currDate + "~" + currTime + "~" + scenarioName + ";;";
//                editor.putString("CallLog", savedCallLog);
//
//                editor.commit();;
//
//                CallLogActivity.callLogList.add(new CallLogElement(currDate, currTime, scenarioName));
//
//                // End of adding call to log
//
//                Intent intentOngoingCall = new Intent(this, OngoingCallActivity.class);
//                intentOngoingCall.putExtra("TELEPHONE_NUMBER", telNum);
//                intentOngoingCall.putExtra("TO_SPEAK", toSpeak);
//                startActivity(intentOngoingCall);
//            } else {
//                String errorMessage = "Invalid phone number (not digit): " + telNum;
//                displayExceptionMessage(errorMessage);
//            }
//        } else {
//            String errorMessage = "Invalid phone number (too short): " + telNum + ": " + telNum.length();
//            displayExceptionMessage(errorMessage);
//        }
//
//
//    }


    /**
     *  Display Error
     * @param  msg String
     */
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setTextSizes() {
        Button callButton = (Button) findViewById(R.id.callButton);
        Button text2VoiceButton = (Button) findViewById(R.id.testVoiceButton);
        //EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);

        int titleSize = sharedPref.getInt("TitleFontSize", 0);
        int subtitleSize = sharedPref.getInt("SubtitleFontSize", 0);
        bodySize = sharedPref.getInt("BodyFontSize", 0);
        fontChange = sharedPref.getInt("FontSizeChange", 0);

        callButton.setTextSize(titleSize + fontChange);
        text2VoiceButton.setTextSize(subtitleSize + fontChange);
        //phoneNumber.setTextSize(subtitleSize + fontChange);
    }

    public void saveLog() {
        // Save Call to Log
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("OnTheSpectrum", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String savedCallLog = sharedPref.getString("CallLog", null);
        if (savedCallLog == null) {
            savedCallLog = "";
        }

        //String timezone = TimeZone.getDefault().getDisplayName();
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Calendar rightNow = Calendar.getInstance();
        int minute = rightNow.get(Calendar.MINUTE);
        int hour = rightNow.get(Calendar.HOUR);
        String isAM = "AM";
        if (hour >= 12) {
            hour -= 12;
            isAM = "PM";
        }
        if (hour == 0) {
            hour = 12;
        }
        String currTime = "";
        if (hour < 10) {
            currTime += "0";
        }
        currTime = currTime + hour + ":";
        if (minute < 10) {
            currTime += "0";
        }
        currTime = currTime + minute + " " + isAM + " (" + rightNow.getTimeZone().getDisplayName() + ")";
        //int month = rightNow.get(Calendar.MONTH);
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        String month = rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        String currDate = month + " " + day + ", " + rightNow.get(Calendar.YEAR);

        // add scenario to list of call logs
        //savedCallLog += currDate + "||" + currTime + "||" + scenarioInfo + ";;";
        savedCallLog = currDate + "~" + currTime + "~" + scenarioName + ";;" + savedCallLog;
        editor.putString("CallLog", savedCallLog);

        editor.commit();;

        //CallLogActivity.callLogList.add(new CallLogElement(currDate, currTime, scenarioName));

        // End of adding call to log

        Intent intentOngoingCall = new Intent(this, OngoingCallActivity.class);
        intentOngoingCall.putExtra("TELEPHONE_NUMBER", telNum);
        intentOngoingCall.putExtra("TO_SPEAK", toSpeak);
        startActivity(intentOngoingCall);
    }
}

package com.example.robotsaheli;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.Locale;

public class MainActivity extends Activity {
    TextToSpeech tts;
    TextView status;
    static final int REQ_SPEECH=12, REQ_OVERLAY=13;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout page=new LinearLayout(this); page.setOrientation(1); page.setPadding(24,32,24,24);
        page.setBackgroundColor(Color.rgb(12,28,60));
        TextView title=new TextView(this); title.setText("🤖 روبوٹ سہیلی"); title.setTextSize(28); title.setTextColor(Color.WHITE); title.setTypeface(null, Typeface.BOLD);
        page.addView(title);
        TextView character=new TextView(this); character.setText("🦸🏻‍♂️\n〰️〰️🔴"); character.setGravity(Gravity.CENTER); character.setTextSize(48); character.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,0,1); page.addView(character,cp);
        status=new TextView(this); status.setText("یہ ابتدائی اینڈرائیڈ ایپ ہے۔ اپنی تصویر شامل کرنے کے لیے پراجیکٹ میں photo.png رکھنی ہوگی۔"); status.setTextColor(Color.WHITE); status.setTextSize(16); page.addView(status);
        addButton(page,"آواز میں جواب سنیں",()->speak("السلام علیکم! میں روبوٹ سہیلی ہوں۔"));
        addButton(page,"آپ کی بات سنیں",()->listen());
        addButton(page,"اسکرین پر تیرتا کردار چلائیں",()->{
            if (!android.provider.Settings.canDrawOverlays(this)) {
                Intent i=new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:"+getPackageName()));
                startActivityForResult(i,REQ_OVERLAY);
            } else { startService(new Intent(this,FloatingService.class)); status.setText("تیرتے کردار کی سروس شروع کی گئی۔"); }
        });
        setContentView(page);
        tts=new TextToSpeech(this, result -> { if(result==TextToSpeech.SUCCESS) tts.setLanguage(new Locale("ur","PK")); });
    }
    void addButton(LinearLayout p,String label,Runnable r) {
        Button b=new Button(this); b.setText(label); b.setOnClickListener(v->r.run()); p.addView(b);
    }
    void speak(String s) {
        if(tts!=null) { tts.setLanguage(new Locale("ur","PK")); tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"saheli"); status.setText(s); }
    }
    void listen() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)) { status.setText("اس موبائل پر آواز پہچاننے کی سہولت دستیاب نہیں۔"); return; }
        if(android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission("android.permission.RECORD_AUDIO")!=android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.RECORD_AUDIO"},21); status.setText("مائیکروفون کی اجازت دیں، پھر دوبارہ دبائیں۔"); return;
        }
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ur-PK");
        try { startActivityForResult(i,REQ_SPEECH); status.setText("اب اردو میں بولیں…"); } catch(Exception e) { status.setText("آواز کی سہولت نہیں کھل سکی۔"); }
    }
    @Override protected void onActivityResult(int req,int res,Intent data) {
        super.onActivityResult(req,res,data);
        if(req==REQ_SPEECH && res==RESULT_OK && data!=null) {
            ArrayList<String> matches=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(matches!=null && !matches.isEmpty()) speak("آپ نے کہا: "+matches.get(0)+"۔ ابھی حقیقی AI جواب منسلک نہیں ہے۔");
        }
        if(req==REQ_OVERLAY && android.provider.Settings.canDrawOverlays(this)) startService(new Intent(this,FloatingService.class));
    }
    @Override public void onDestroy(){ if(tts!=null) tts.shutdown(); super.onDestroy(); }
}

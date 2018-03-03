package com.ccc.raj.beats.settings;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ccc.raj.beats.MainActivity;
import com.ccc.raj.beats.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeData.theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Resources.Theme theme = getApplication().getTheme();
        Toast.makeText(this,theme.toString(),Toast.LENGTH_SHORT).show();
    }
    public void setBlueTheme(View view){
        ThemeData.theme = R.style.AppTheme;
        recreate();
    }
    public void setPurpleTheme(View view){
        ThemeData.theme = R.style.PurpleMagic;
        recreate();
    }
    public void setGreyTheme(View view){
        ThemeData.theme = R.style.GreyMagic;
        recreate();
    }
    public void setOrangeTheme(View view){
        ThemeData.theme = R.style.OrangeMagic;
        recreate();
    }
}

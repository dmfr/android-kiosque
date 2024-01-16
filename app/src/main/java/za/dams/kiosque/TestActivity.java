package za.dams.kiosque;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.fragment.app.FragmentActivity;

public class TestActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }










}
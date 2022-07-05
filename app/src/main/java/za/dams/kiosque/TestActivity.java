package za.dams.kiosque;



import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toolbar;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }










}
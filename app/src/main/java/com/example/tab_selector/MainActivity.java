package com.example.tab_selector;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_HISTORY = "history_list";
    private ArrayList<String> history;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views (direct findViewById — works with included layouts too)
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        View viewTab1 = findViewById(R.id.tab1);
        View viewTab2 = findViewById(R.id.tab2);
        EditText etA = findViewById(R.id.editTextText3);
        EditText etB = findViewById(R.id.editTextText5);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        ListView listHistory = findViewById(R.id.list_history);
        TextView resultText = findViewById(R.id.result_text);

        // Header views
        TextView headerPhepCong = findViewById(R.id.header_phep_cong);
        TextView headerLichSu = findViewById(R.id.header_lich_su);

        // Restore history if available
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_HISTORY)) {
            history = savedInstanceState.getStringArrayList(KEY_HISTORY);
        }
        if (history == null) history = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
        if (listHistory != null) listHistory.setAdapter(adapter);

        if (tabLayout != null) {
            TabLayout.Tab t1 = tabLayout.newTab();
            t1.setText(getString(R.string.phep_cong));
            t1.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_input_add));
            tabLayout.addTab(t1);

            TabLayout.Tab t2 = tabLayout.newTab();
            t2.setText(getString(R.string.lich_su));
            t2.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_idle_alarm));
            tabLayout.addTab(t2);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int pos = tab.getPosition();
                    if (pos == 0) {
                        viewTab1.setVisibility(View.VISIBLE);
                        viewTab2.setVisibility(View.GONE);
                    } else {
                        viewTab1.setVisibility(View.GONE);
                        viewTab2.setVisibility(View.VISIBLE);
                    }
                    // Update header visuals
                    updateHeaderVisuals(pos, headerPhepCong, headerLichSu);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) { }

                @Override
                public void onTabReselected(TabLayout.Tab tab) { }
            });

            // select first tab by default
            TabLayout.Tab first = tabLayout.getTabAt(0);
            if (first != null) first.select();
        }

        // Header click: switch tabs
        if (headerPhepCong != null) {
            headerPhepCong.setOnClickListener(v -> {
                if (tabLayout != null) tabLayout.getTabAt(0).select();
            });
        }
        if (headerLichSu != null) {
            headerLichSu.setOnClickListener(v -> {
                if (tabLayout != null) tabLayout.getTabAt(1).select();
            });
        }

        // FAB click: compute sum, add to history, refresh list, and display result in resultText
        if (fab != null) {
            fab.setOnClickListener(v -> {
                String aText = etA.getText().toString().trim();
                String bText = etB.getText().toString().trim();
                if (aText.isEmpty() || bText.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập hai số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    double a = Double.parseDouble(aText);
                    double b = Double.parseDouble(bText);
                    double sum = a + b;
                    String entry = aText + " + " + bText + " = " + sum;
                    history.add(0, entry); // newest first
                    adapter.notifyDataSetChanged();
                    if (listHistory != null) listHistory.smoothScrollToPosition(0);
                    if (resultText != null) resultText.setText(String.valueOf(sum));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateHeaderVisuals(int selectedIndex, TextView hep, TextView lich) {
        if (selectedIndex == 0) {
            hep.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            hep.setTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary));
            lich.setBackgroundColor(ContextCompat.getColor(this, R.color.header_unselected_bg));
            lich.setTextColor(ContextCompat.getColor(this, R.color.header_unselected_text));
        } else {
            lich.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            lich.setTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary));
            hep.setBackgroundColor(ContextCompat.getColor(this, R.color.header_unselected_bg));
            hep.setTextColor(ContextCompat.getColor(this, R.color.header_unselected_text));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_HISTORY, history);
    }
}
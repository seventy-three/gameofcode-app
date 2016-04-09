package lu.ing.gameofcode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lu.ing.gameofcode.R;

public class GoalActivity extends AppCompatActivity {

    @Bind(R.id.goals_rg)
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.validate_btn)
    public void onValidateClicked() {
        final int timeInMinute = getTimeInMinutesFromGoal(radioGroup.getCheckedRadioButtonId());
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferences.edit().putInt("goalTimeInMinutes", timeInMinute).apply();

        startActivity(new Intent(this, PlanningActivity.class));
    }

    private int getTimeInMinutesFromGoal(final int goal) {
        switch (goal) {
            case R.id.goal_one_rb:
                return 20;
            case R.id.goal_two_rb:
                return 30;
            case R.id.goal_three_rb:
                return 40;
        }
        return 20;
    }
}

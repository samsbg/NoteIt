package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.DayGoalAdapter;
import com.codepath.noteit.databinding.ActivityCalendarBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Reminder;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.YearMonth.now;

public class CalendarActivity extends AppCompatActivity {

    ActivityCalendarBinding binding;

    Map<Date, List<Goal>> reminderMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        queryReminders(view);
    }

    private void queryReminders(View view) {
        ParseQuery<Reminder> query = ParseQuery.getQuery(Reminder.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Reminder>() {
            @Override
            public void done(List<Reminder> remindersList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("CalendarActivity", "Issue with getting reminders", e);
                    return;
                }
                reminderMap = new HashMap<>();
                for (Reminder rem : remindersList) {
                    if(reminderMap.containsKey(rem.getDate())) {
                        if (!reminderMap.get(rem.getDate()).contains((Goal) rem.getGoal())) {
                            reminderMap.get(rem.getDate()).add((Goal) rem.getGoal());
                        }
                    } else {
                        reminderMap.put(rem.getDate(), new ArrayList<>());
                        reminderMap.get(rem.getDate()).add((Goal) rem.getGoal());
                    }
                }
                calendarBinder(view);
            }
        });
    }

    private void calendarBinder(View view) {
        YearMonth currentMonth = now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;

        class DayViewContainer extends ViewContainer {
            final TextView calendarDay;
            final RecyclerView goalsDay;
            final DayGoalAdapter goalsDayAdapter;
            List<Goal> goals;

            public DayViewContainer(@NonNull View view) {
                super(view);
                calendarDay = view.findViewById(R.id.tvCalendarDay);
                goalsDay = view.findViewById(R.id.rvGoalsDay);
                goals = new ArrayList<>();
                goalsDayAdapter = new DayGoalAdapter(view.getContext(), goals);
            }
        }

        class MonthViewContainer extends ViewContainer {
            final TextView calendarMonth;

            public MonthViewContainer(@NonNull View view) {
                super(view);
                calendarMonth = view.findViewById(R.id.tvCalendarMonth);
            }
        }

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.calendarDay.setText(String.valueOf(day.getDay()));
                container.goalsDay.setLayoutManager(new LinearLayoutManager(view.getContext()));
                container.goalsDay.setAdapter(container.goalsDayAdapter);
                container.goals.clear();
                if(reminderMap.containsKey(Date.from(day.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    container.goals.addAll(reminderMap.get(Date.from(day.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
                    container.goalsDayAdapter.notifyDataSetChanged();
                }
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @Override
            public MonthViewContainer create(View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                container.calendarMonth.setText(new DateFormatSymbols().getMonths()[calendarMonth.getMonth()-1]);
            }
        });

        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        binding.calendarView.scrollToMonth(currentMonth);
    }
}
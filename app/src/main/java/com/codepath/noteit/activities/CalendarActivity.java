package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.DayGoalAdapter;
import com.codepath.noteit.adapters.MainGoalAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static java.time.YearMonth.now;

public class CalendarActivity extends AppCompatActivity {

    ActivityCalendarBinding binding;

    Map<Date, List<Goal>> reminderMap;
    List<Goal> goalsBottom;
    MainGoalAdapter mainGoalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        queryReminders(view);

        MainGoalAdapter.OnClickListener onClickListenerGoal = new MainGoalAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Goal goal, View v) {
                if(goal.getNote() != null) {
                    Intent i = new Intent(CalendarActivity.this, NoteEditorActivity.class);
                    i.putExtra("GOAL", goal);
                    i.putExtra("NOTE_GOAL", goal.getNote());
                    startActivity(i);
                }
            }
        };

        goalsBottom = new ArrayList<>();
        mainGoalAdapter = new MainGoalAdapter(this, goalsBottom, onClickListenerGoal);
        binding.rvMainGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMainGoals.setAdapter(mainGoalAdapter);
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
            List<Goal> goalsDayList;

            public DayViewContainer(@NonNull View view) {
                super(view);
                calendarDay = view.findViewById(R.id.tvCalendarDay);
                goalsDay = view.findViewById(R.id.rvGoalsDay);
                goalsDayList = new ArrayList<>();
                goalsDayAdapter = new DayGoalAdapter(view.getContext(), goalsDayList);
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
                container.goalsDayList.clear();
                if(reminderMap.containsKey(Date.from(day.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    container.goalsDayList.addAll(reminderMap.get(Date.from(day.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
                    container.goalsDayAdapter.notifyDataSetChanged();
                }
                container.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strDate = day.getDate().toString();
                        LocalDate aLD = LocalDate.parse(strDate);
                        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                        binding.tvDay.setText(dTF.format(aLD));

                        goalsBottom.clear();
                        goalsBottom.addAll(container.goalsDayList);
                        mainGoalAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @Override
            public MonthViewContainer create(View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                container.calendarMonth.setText((new DateFormatSymbols().getMonths()[calendarMonth.getMonth()-1] + " " + calendarMonth.getYear()));
            }
        });

        calendarView.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                return Unit.INSTANCE;
            }
        });

        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        binding.calendarView.scrollToMonth(currentMonth);
    }
}
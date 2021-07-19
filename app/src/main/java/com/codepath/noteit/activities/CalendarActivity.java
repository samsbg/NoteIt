package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.codepath.noteit.R;
import com.codepath.noteit.databinding.ActivityCalendarBinding;
import com.codepath.noteit.databinding.CalendarDayLayoutBinding;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static java.time.YearMonth.now;

public class CalendarActivity extends AppCompatActivity {

    ActivityCalendarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        YearMonth currentMonth = now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;

        class DayViewContainer extends ViewContainer {
            final TextView calendarDay;

            public DayViewContainer(@NonNull View view) {
                super(view);
                calendarDay = view.findViewById(R.id.tvCalendarDay);
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
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @Override
            public MonthViewContainer create(View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                container.calendarMonth.setText(String.valueOf(calendarMonth.getMonth()));
            }
        });

        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        binding.calendarView.scrollToMonth(currentMonth);




    }


}
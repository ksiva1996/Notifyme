package com.leagueofshadows.notifyme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements Communicator{


    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public void logstart() {
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void logstop() {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int pos =  getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            if(pos==1)
            {
                rootView= inflater.inflate(R.layout.fragment_login, container, false);
                final TextView email = (TextView)rootView.findViewById(R.id.loginemail);
                final TextView pass = (TextView)rootView.findViewById(R.id.loginpass);
                Button login = (Button)rootView.findViewById(R.id.loginbutton);
                Button forgot = (Button)rootView.findViewById(R.id.forgot);
                forgot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = email.getText().toString();
                        Worker w= new Worker(getContext());
                        w.execute(username);
                    }
                });
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String em = email.getText().toString();
                        String p = pass.getText().toString();
                        if(!em.equals("")&&!p.equals("")&&em.contains("@"))
                        {
                            workerlogin wl = new workerlogin(getContext());
                            wl.execute(em,p);
                        }
                        else
                        {
                            Toast.makeText(getContext(),"enter the email password correctly",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            else
            {
                rootView= inflater.inflate(R.layout.fragment_signup, container, false);
                final TextView email = (TextView)rootView.findViewById(R.id.signupemail);
                final TextView pass = (TextView)rootView.findViewById(R.id.signuppass);
                final TextView num = (TextView)rootView.findViewById(R.id.signupnumber);
                final TextView confirmpass = (TextView)rootView.findViewById(R.id.confirmpass);
                Button signup = (Button)rootView.findViewById(R.id.signupbutton);
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String em = email.getText().toString();
                        String p = pass.getText().toString();
                        String n = num.getText().toString();
                        String cp = confirmpass.getText().toString();
                        if(cp.equals(p))
                        {
                            if(n.length()==10&&em.contains("@"))
                            {
                                workersignup ws = new workersignup(getContext());
                                ws.execute(em,n,p);
                            }
                            else
                                Toast.makeText(getContext(),"please enter a valid number",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(),"password and confirm password doesnot match",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN";
                case 1:
                    return "REGISTER";
            }
            return null;
        }
    }
}

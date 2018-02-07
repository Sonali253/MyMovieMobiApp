package com.sonali.mymoviemobiapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonali.mymoviemobiapp.MainActivity;
import com.sonali.mymoviemobiapp.R;
import com.sonali.mymoviemobiapp.web.Url;
import com.sonali.mymoviemobiapp.web.Ws;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MovieDetailFragment extends Fragment {
    private ViewPager vp_slider;
    private LinearLayout ll_dots;
    SliderPagerAdapter sliderPagerAdapter;
    ArrayList<String> slider_image_list;
    private TextView[] dots;
    int page_position = 0;
    public static final String KEY_FRAGMENT_NAME = "MovieDetailFragment";
    public static final String KEY_ID = "id";

    public static MovieDetailFragment newInstance(Integer id) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT_NAME,"MovieListFragment");
        Log.i(MainActivity.TAG,"id - "+id);
        args.putInt(KEY_ID,id);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        getParent().setActionBarTitle("Movies Details");
        vp_slider = (ViewPager) rootView.findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout) rootView.findViewById(R.id.ll_dots);
        slider_image_list = new ArrayList<>();

        getImages(rootView);
        addBottomDots(0);

        final Handler handler = new Handler();
        final Runnable update = () -> {
            if (page_position == slider_image_list.size()) {
                page_position = 0;
            } else {
                page_position = page_position + 1;
            }
            vp_slider.setCurrentItem(page_position, true);
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 10000);


        return rootView;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    private void getImages(final View rootView) {
        //rootView.findViewById(R.id.progressList).setVisibility(View.VISIBLE);

        String urlToGetRandomBappaAd = Url.URL_BASE + getArguments().getInt(KEY_ID) + Url.URL_SHOW_DETAIL_MOVIE_INFO;
        Log.i(MainActivity.TAG, "urlToGetRandomBappaAd : " + urlToGetRandomBappaAd);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToGetRandomBappaAd,
                response -> {
                    Log.i(MainActivity.TAG, "Response getRandomBappaAd: " + response);
                    //switchFromAddToList(getView());
                    if (response != null) {
                        //rootView.findViewById(R.id.progressList).setVisibility(View.GONE);
                        showImages(response, rootView);
                    }
                },
                error -> Log.d("Error.Response", error.toString())
        );
        Ws.getQueue(getActivity()).add(jsonObjectRequest);
    }

    private void showImages(JSONObject response, View rootView) {
        List<String> upcomingMoviesImages = getUpcomingMoviesJson(response, rootView);
        Log.i(MainActivity.TAG, "upcomingMoviesImages after return - " + upcomingMoviesImages.toString());
        if (!upcomingMoviesImages.isEmpty()) {
            if (getParent() != null) {
                sliderPagerAdapter = new SliderPagerAdapter(getParent(), upcomingMoviesImages);
                vp_slider.setAdapter(sliderPagerAdapter);
                vp_slider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        addBottomDots(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        } else {
            if (getParent() != null)
                getParent().snack(rootView, "Result Not Found"); //need to be externalise
        }
    }


    private void init(View rootView) {

        getParent().setActionBarTitle("Movies Details");
        vp_slider = (ViewPager) rootView.findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout) rootView.findViewById(R.id.ll_dots);
        slider_image_list = new ArrayList<>();

        slider_image_list.add("http://images.all-free-download.com/images/graphiclarge/mountain_bongo_animal_mammal_220289.jpg");
        slider_image_list.add("http://images.all-free-download.com/images/graphiclarge/bird_mountain_bird_animal_226401.jpg");
        slider_image_list.add("http://images.all-free-download.com/images/graphiclarge/mountain_bongo_animal_mammal_220289.jpg");
        slider_image_list.add("http://images.all-free-download.com/images/graphiclarge/bird_mountain_bird_animal_226401.jpg");


        /*sliderPagerAdapter = new SliderPagerAdapter(getParent(), slider_image_list);
        vp_slider.setAdapter(sliderPagerAdapter);*/

        vp_slider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<String> getUpcomingMoviesJson(JSONObject response, View rootView) {
        List<String> upcomingMovieImages = new ArrayList<>();
        try {
            JSONArray jsonArrayResult = response.getJSONArray("posters");
            Log.i(MainActivity.TAG, "Movie JSON: " + jsonArrayResult);
            if (jsonArrayResult != null) {
                for (Integer i = 0; i < 5; i++) {
                    JSONObject jsonObject1 = jsonArrayResult.getJSONObject(i);

                    upcomingMovieImages.add("http://image.tmdb.org/t/p/w185" + jsonObject1.getString("file_path"));

                    Log.i(MainActivity.TAG, "Movie Images - " + upcomingMovieImages);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return upcomingMovieImages;
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[slider_image_list.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#ffffff"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#000000"));
    }


    public class SliderPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        Activity activity;
        List<String> image_arraylist;

        public SliderPagerAdapter(Activity activity, List<String> image_arraylist) {
            this.activity = activity;
            this.image_arraylist = image_arraylist;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.layout_slider, container, false);
            ImageView im_slider = (ImageView) view.findViewById(R.id.im_slider);
            Picasso.with(activity.getApplicationContext())
                    .load(image_arraylist.get(position))
                    .into(im_slider);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return image_arraylist.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}

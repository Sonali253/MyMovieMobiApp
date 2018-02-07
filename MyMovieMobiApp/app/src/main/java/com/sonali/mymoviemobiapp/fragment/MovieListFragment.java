package com.sonali.mymoviemobiapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonali.mymoviemobiapp.MainActivity;
import com.sonali.mymoviemobiapp.R;
import com.sonali.mymoviemobiapp.web.Url;
import com.sonali.mymoviemobiapp.web.Ws;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MovieListFragment extends Fragment {

    public static final String KEY_FRAGMENT_NAME = "MovieListFragment";

    public static MovieListFragment newInstance() {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT_NAME, "MovieListFragment");
        fragment.setArguments(args);
        return fragment;
    }

    public static String reformatDate(String date) {
        String[] dateArray = date.split("-");
        StringBuilder str = new StringBuilder();
        str.append(dateArray[2]);
        str.append("/");
        str.append(dateArray[1]);
        str.append("/");
        str.append(dateArray[0]);
        return str.toString();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        getParent().setActionBarTitle("Upcoming Movies");
        getInfo(rootView);
        return rootView;
    }

    private void getInfo(final View rootView) {
        //rootView.findViewById(R.id.progressList).setVisibility(View.VISIBLE);
        String urlToGetRandomBappaAd = Url.URL_GET_MOVIE_LIST;
        Log.i(MainActivity.TAG, "urlToGetRandomBappaAd : " + urlToGetRandomBappaAd);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToGetRandomBappaAd,
                response -> {
                    Log.i(MainActivity.TAG, "Response getRandomBappaAd: " + response);
                    //switchFromAddToList(getView());
                    if (response != null) {
                        //rootView.findViewById(R.id.progressList).setVisibility(View.GONE);
                        populateUpcomingMovies(response, rootView);
                    }
                },
                error -> Log.d("Error.Response", error.toString())
        );
        Ws.getQueue(getActivity()).add(jsonObjectRequest);
    }

    private void populateUpcomingMovies(JSONObject response, View rootView) {
        List<UpcomingMovies> upcomingMoviesList = getUpcomingMoviesJson(response, rootView);
        Log.i(MainActivity.TAG, "populateRandomBappaAd- " + upcomingMoviesList.toString());
        if (!upcomingMoviesList.isEmpty()) {
            // ((RecyclerView) rootView.findViewById(R.id.recyclerViewMovieList)).setHasFixedSize(true);
            if (getParent() != null) {
                AdapterUpcomingMovies adapter = new AdapterUpcomingMovies(getParent(), upcomingMoviesList);
                ((RecyclerView) rootView.findViewById(R.id.recyclerViewMovieList)).setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                ((RecyclerView) rootView.findViewById(R.id.recyclerViewMovieList)).setLayoutManager(linearLayoutManager);
            }
        } else {
            if (getParent() != null)
                getParent().snack(rootView, "Result Not Found"); //need to be externalise
            ((RecyclerView) rootView.findViewById(R.id.recyclerViewMovieList)).setAdapter(null);
        }
    }

    private List<UpcomingMovies> getUpcomingMoviesJson(JSONObject response, View rootView) {
        List<UpcomingMovies> upcomingMovies = new ArrayList<>();
        try {
            JSONArray jsonArrayResult = response.getJSONArray("results");
            Log.i(MainActivity.TAG, "Movie JSON: " + jsonArrayResult);
            if (jsonArrayResult != null) {
                for (Integer i = 0; i < jsonArrayResult.length(); i++) {
                    JSONObject jsonObject1 = jsonArrayResult.getJSONObject(i);
                    UpcomingMovies item = new UpcomingMovies();
                    item.setId(jsonObject1.getInt("id"));
                    item.setMovieName(jsonObject1.getString("title"));
                    item.setReleseDate(reformatDate(jsonObject1.getString("release_date")));
                    //item.setInfo(jsonObject1.getString("info"));
                    item.setAdult(jsonObject1.getBoolean("adult"));
                    item.setPosterImages(jsonObject1.getString("poster_path"));

                   /* JSONArray jsonArrayImages = jsonObject1.getJSONArray("images");
                    Log.i(MainActivity.TAG, "Images : " + jsonArrayImages);
                    List<String> imagesList = new ArrayList<>();
                    if (jsonArrayImages != null) {
                        for (Integer j = 0; j < jsonArrayImages.length(); j++) {
                            Log.i(MainActivity.TAG, "ImageList " + jsonArrayImages.get(j).toString());
                            imagesList.add(jsonArrayImages.get(j).toString());
                            item.setImages(imagesList);
                        }
                    }
                    JSONObject jsonObjectUser = jsonObject1.getJSONObject("user");
                    item.setUserId(jsonObjectUser.getString("id"));*/
                    upcomingMovies.add(item);
                    Log.i(MainActivity.TAG, "Movies - " + item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return upcomingMovies;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    private void getImagesFromPicasso(ImageView imageView, String Image) {
        Picasso.with(getContext())
                .load(Image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .into(imageView);
        Picasso.with(getContext())
                .load(Image)
                // .placeholder(R.drawable.ic_camera)   // optional
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //progressBarOnImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private class AdapterUpcomingMovies extends RecyclerView.Adapter<RecyclerViewHolderGetUpcomingMovies> {
        private Context context;
        private LayoutInflater inflater;
        private List<UpcomingMovies> upcomingMovies = new ArrayList<>();

        public AdapterUpcomingMovies(Context context, List<UpcomingMovies> upcomingMovies) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.upcomingMovies = upcomingMovies;
        }

        @Override
        public RecyclerViewHolderGetUpcomingMovies onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_movie_list, parent, false);
            RecyclerViewHolderGetUpcomingMovies holder = new RecyclerViewHolderGetUpcomingMovies(layoutView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolderGetUpcomingMovies holder, int position) {
            Log.i(MainActivity.TAG, "List Of list : " + upcomingMovies.toString());
            holder.getTextViewMoviewName().setText(upcomingMovies.get(position).getMovieName());

            //GET URL from db = http://image.tmdb.org/t/p/w185/fn4n6uOYcB6Uh89nbNPoU2w80RV.jpg
            Log.i(MainActivity.TAG, " Images List - " + "http://image.tmdb.org/t/p/w185" + upcomingMovies.get(position).getPosterImages());
            getImagesFromPicasso(holder.getImageViewPosterImage(), "http://image.tmdb.org/t/p/w185" + upcomingMovies.get(position).getPosterImages());
            holder.getTextViewMoviewName().setText(upcomingMovies.get(position).getMovieName());
            holder.getTextViewReleseDate().setText(upcomingMovies.get(position).getReleseDate());
            if (upcomingMovies.get(position).getAdult()) {
                holder.getTextViewAdult().setText("(A)");
            } else holder.getTextViewAdult().setText("(U/A)");
            holder.getCardViewMandalList().setTag(upcomingMovies.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return upcomingMovies.size();
        }
    }

    private class RecyclerViewHolderGetUpcomingMovies extends RecyclerView.ViewHolder {

        public RecyclerViewHolderGetUpcomingMovies(View itemView) {
            super(itemView);
            View view = itemView;
            getCardViewMandalList().setOnClickListener(v -> {
                Log.i(MainActivity.TAG, "cardViewSub.getTag()" + getCardViewMandalList().getTag());
                //getParent().hideKeyboard();
                getParent().runFragmentTransaction(R.id.frameMainContainer,
                        MovieDetailFragment.newInstance((Integer) getCardViewMandalList().getTag()));
            });
        }

        public CardView getCardViewMandalList() {
            return (CardView) itemView.findViewById(R.id.cardViewMovieList);
        }

        public TextView getTextViewMoviewName() {
            return (TextView) itemView.findViewById(R.id.textViewMovieName);
        }

        public TextView getTextViewReleseDate() {
            return (TextView) itemView.findViewById(R.id.textViewReleseDate);
        }

        public TextView getTextViewAdult() {
            return (TextView) itemView.findViewById(R.id.textViewAdults);
        }

        public ImageView getImageViewPosterImage() {
            return (ImageView) itemView.findViewById(R.id.imagePoster);
        }

    }

    private class UpcomingMovies {
        private int id;
        private String movieName;
        private String releseDate;
        private Boolean adult;
        private String posterImages;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getReleseDate() {
            return releseDate;
        }

        public void setReleseDate(String releseDate) {
            this.releseDate = releseDate;
        }

        public Boolean getAdult() {
            return adult;
        }

        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        public String getPosterImages() {
            return posterImages;
        }

        public void setPosterImages(String posterImages) {
            this.posterImages = posterImages;
        }

        @Override
        public String toString() {
            return "UpcomingMovies{" +
                    "movieName='" + movieName + '\'' +
                    ", releseDate='" + releseDate + '\'' +
                    ", adult='" + adult + '\'' +
                    ", posterImages=" + posterImages +
                    '}';
        }
    }

}

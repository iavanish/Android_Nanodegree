package com.example.iavanish.popularmovies.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iavanish.popularmovies.R;
import com.example.iavanish.popularmovies.db.AccessDatabase;
import com.example.iavanish.popularmovies.entities.MoviesList;
import com.example.iavanish.popularmovies.util.JSONParser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailsFragment extends Fragment {

    private Context context;
    private View view;
    private ImageView movieThumbnail;
    private CheckBox favourite;
    private static List<String> trailers;
    private static List<String> reviews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        movieThumbnail = (ImageView) view.findViewById(R.id.movieThumbnail);
        favourite = (CheckBox) view.findViewById(R.id.favourite);

        final MoviesList movies = MoviesList.getInstance();

        Bundle bundle = getArguments();
        final int movieIndex = bundle.getInt("MovieIndex");

        movieThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
        String posterURL = getResources().getString(R.string.bigPosterURL) + movies.movies.get(movieIndex).getPoster_path();
        Picasso.with(context).load(posterURL).into(movieThumbnail);

        if(new AccessDatabase(context).isMoviePresentInDB(movies.movies.get(movieIndex))) {
            favourite.setChecked(true);
        }

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    new AccessDatabase(context).insertMovie(movies.movies.get(movieIndex));
                }
                else {
                    new AccessDatabase(context).deleteMovie(movies.movies.get(movieIndex));
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.baseLinearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = getTextView("Original Title: " + movies.movies.get(movieIndex).getOriginal_title());
        linearLayout.addView(textView);

        textView = getTextView("Overview: " + movies.movies.get(movieIndex).getOverview());
        linearLayout.addView(textView);

        textView = getTextView("Vote Average: " + movies.movies.get(movieIndex).getVote_average());
        linearLayout.addView(textView);

        textView = getTextView("Release Date: " + movies.movies.get(movieIndex).getRelease_date());
        linearLayout.addView(textView);

        String url = context.getResources().getString(R.string.base_url) + String.valueOf(movies.movies.get(movieIndex).getId()) +
                context.getResources().getString(R.string.trailer_url) + context.getResources().getString(R.string.apiKey);

        RequestQueue queue1 = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                trailers = new JSONParser().getTrailers(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("Something went wrong while retrieving data from theMovieDB!");
            }
        });
        queue1.add(stringRequest1);

        if(trailers != null) {
            for (String temp : trailers) {
                Button button = getButton(temp);
                linearLayout.addView(button);
            }
        }

        url = context.getResources().getString(R.string.base_url) + String.valueOf(movies.movies.get(movieIndex).getId()) +
                context.getResources().getString(R.string.review_url) + context.getResources().getString(R.string.apiKey);

        RequestQueue queue2 = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                trailers = new JSONParser().getReviews(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("Something went wrong while retrieving data from theMovieDB!");
            }
        });
        queue2.add(stringRequest2);

        if(reviews != null) {
            for (String temp : reviews) {
                textView = getTextView(temp);
                linearLayout.addView(textView);
            }
        }

        return view;
    }

    private TextView getTextView(String text) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.setMargins(5,60,5,0);
        textView.setLayoutParams(params);
        textView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        textView.setText(text);
        return textView;
    }

    private Button getButton(String text) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.setMargins(5,60,5,0);
        button.setLayoutParams(params);
        button.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        button.setGravity(View.TEXT_ALIGNMENT_CENTER);
        button.setText(text);
        return button;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}

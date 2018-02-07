package com.sonali.mymoviemobiapp.web;

/**
 * Created by sonali on 8/8/17.
 */

public interface Url {
    String PROTOCOL = "https";
    //https://api.themoviedb.org/3/movie/upcoming?api_key=b7cd3340a794e5a2f35e3abb820b497f&year=2017
    String SERVER = "api.themoviedb.org/3/movie";
    String URL_BASE = PROTOCOL + "://" + SERVER + "/";

    String URL_GET_MOVIE_LIST = URL_BASE +"upcoming?api_key=b7cd3340a794e5a2f35e3abb820b497f&year=2017";
    String URL_SHOW_DETAIL_MOVIE_INFO = "/images?api_key=b7cd3340a794e5a2f35e3abb820b497f";

    //https://api.themoviedb.org/3/movie/315635/images?api_key=b7cd3340a794e5a2f35e3abb820b497f
}

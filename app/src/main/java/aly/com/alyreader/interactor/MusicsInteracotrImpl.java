/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aly.com.alyreader.interactor;


import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aly.com.alyreader.bean.MusicBean;
import aly.com.alyreader.bean.MusicPageInfo;
import aly.com.alyreader.bean.MusicsListEntity;
import aly.com.alyreader.bean.ResponseMusicsListentity;
import aly.com.alyreader.listeners.BaseMultiLoadedListener;
import aly.com.alyreader.utils.UriHelper;
import okhttp3.Call;

/**
 * Author:  Tau.Chen
 * Email:   1076559197@qq.com | tauchen1990@gmail.com
 * Date:    2015/4/16.
 * Description:
 */
public class MusicsInteracotrImpl implements MusicsInteractor {

    private BaseMultiLoadedListener<ResponseMusicsListentity> loadedListener = null;

    public MusicsInteracotrImpl(BaseMultiLoadedListener<ResponseMusicsListentity> loadedListener) {
        this.loadedListener = loadedListener;
    }

    @Override
    public void getMusicListData(String requestTag, String keywords, final int event_tag) {
        Log.d(requestTag, UriHelper.getInstance().getDoubanPlayListUrl(keywords));
        getMusicList(event_tag);
//        playDefaultMusic(event_tag);
//        GsonRequest<ResponseMusicsListentity> gsonRequest = new GsonRequest<ResponseMusicsListentity>(
//                UriHelper.getInstance().getDoubanPlayListUrl(keywords),
//                null,
//                new TypeToken<ResponseMusicsListentity>() {
//                }.getType(),
//                new Response.Listener<ResponseMusicsListentity>() {
//                    @Override
//                    public void onResponse(ResponseMusicsListentity response) {
//                        loadedListener.onSuccess(event_tag, response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loadedListener.onException(error.getMessage());
//                    }
//                }
//        );
//
//        gsonRequest.setShouldCache(true);
//        gsonRequest.setTag(requestTag);
//
//        VolleyHelper.getInstance().getRequestQueue().add(gsonRequest);
    }
    private void playDefaultMusic(int eventtag)
    {
        String url = "http://apis.baidu.com/geekery/music/playinfo";
        Map<String, String> headers = new HashMap<>();
        headers.put("apikey", "d7e569060ebb9021e8d8cc654df20505");
//        Log.d("RecyclerLinearFragment", "getNews_Page:" + page.toString());
        OkHttpUtils
                .get()
                .url(url)
                .addParams("hash","C23D025EE9ECE593ABD96D7B97DB97B4")
                .headers(headers)
                .build()
                .execute(new MusicPageInfoCallBack(eventtag));
//        Uri uri = Uri.parse("android.resource://aly.com.alyreader/raw/alert.wav");
//        MusicsListEntity musicsListEntity=new MusicsListEntity();
//        musicsListEntity.setUrl(uri.toString());

    }
    private void getMusicList(int eventtag)
    {
        String url = "http://apis.baidu.com/geekery/music/query";
        Map<String, String> headers = new HashMap<>();
        headers.put("apikey", "d7e569060ebb9021e8d8cc654df20505");
//        Log.d("RecyclerLinearFragment", "getNews_Page:" + page.toString());
        OkHttpUtils
                .get()
                .url(url)
                .addParams("page","1")
                .addParams("size","8")
                .addParams("s"," 十年")
                .headers(headers)
                .build()
                .execute(new MusicPageInfoCallBack(eventtag));
    }
    private void initMusicListURL(MusicsListEntity musicsListEntity,int eventtag)
    {

    }

    private Gson gson = new Gson();
    public class MusicPageInfoCallBack extends StringCallback {
        private int eventtag;

        public MusicPageInfoCallBack(int eventtag) {
            this.eventtag = eventtag;
        }


        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            MusicPageInfo musicPageInfo = gson.fromJson(response, MusicPageInfo.class);
            if (musicPageInfo != null) {
                List<MusicsListEntity> list = musicPageInfo.getData().getData();
                ResponseMusicsListentity responseMusicsListentity = new ResponseMusicsListentity();
                responseMusicsListentity.setSong(list);
                loadedListener.onSuccess(eventtag, responseMusicsListentity);

            }

        }
    }
    public class MusicsURLCallBack extends StringCallback{
        int eventtag;

        public MusicsURLCallBack(int eventtag)
        {
            this.eventtag = eventtag;

        }
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            MusicBean musicBean = gson.fromJson(response, MusicBean.class);


        }
    }
}

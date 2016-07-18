package aly.com.alyreader.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import aly.com.alyreader.R;
import aly.com.alyreader.bean.MusicsListEntity;
import aly.com.alyreader.bean.ResponseMusicsListentity;
import aly.com.alyreader.blur.FastBlur;
import aly.com.alyreader.common.Constants;
import aly.com.alyreader.eventbus.URLEvent;
import aly.com.alyreader.eventbus.UpdateMusicInfoEvent;
import aly.com.alyreader.player.MusicPlayService;
import aly.com.alyreader.player.MusicPlayState;
import aly.com.alyreader.presenter.MusicsPresenter;
import aly.com.alyreader.presenter.MusicsPresenterImpl;
import aly.com.alyreader.utils.CommonUtils;
import aly.com.alyreader.utils.UriHelper;
import aly.com.alyreader.view.MusicsView;
import aly.com.alyreader.widgets.PlayerDiscView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements MusicsView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MusicFragment";

    @Bind(R.id.musics_player_background)
    ImageView musicsPlayerBackground;
    @Bind(R.id.player_disc)
    ImageView playerDisc;
    @Bind(R.id.player_disc_image)
    ImageView playerDiscImage;
    @Bind(R.id.player_needle)
    ImageView playerNeedle;
    @Bind(R.id.player_disc_container)
    PercentRelativeLayout playerDiscContainer;
    @Bind(R.id.musics_player_name)
    TextView musicsPlayerName;
    @Bind(R.id.musics_player_songer_name)
    TextView musicsPlayerSongerName;
    @Bind(R.id.musics_player_current_time)
    TextView musicsPlayerCurrentTime;
    @Bind(R.id.musics_player_seekbar)
    SeekBar musicsPlayerSeekbar;
    @Bind(R.id.musics_player_total_time)
    TextView musicsPlayerTotalTime;
    @Bind(R.id.musics_player_progress_container)
    LinearLayout musicsPlayerProgressContainer;
    @Bind(R.id.musics_player_play_prev_btn)
    ImageButton musicsPlayerPlayPrevBtn;
    @Bind(R.id.musics_player_play_ctrl_btn)
    ImageButton musicsPlayerPlayCtrlBtn;
    @Bind(R.id.musics_player_play_next_btn)
    ImageButton musicsPlayerPlayNextBtn;
    @Bind(R.id.musics_player_loading_view)
    View musicsPlayerLoadingView;
    @Bind(R.id.musics_player_container)
    PercentRelativeLayout musicsPlayerContainer;
    @Bind(R.id.musics_player_disc_view)
    PlayerDiscView mPlayerDiscView;
    protected static String TAG_LOG = null;
    @Bind(R.id.music_player_love_btn)
    ImageButton musicPlayerLoveBtn;
    @Bind(R.id.music_player_list_btn)
    ImageButton musicPlayerListBtn;
    @Bind(R.id.music_player_mode_btn)
    ImageButton musicPlayerModeBtn;
    @Bind(R.id.music_player_share_btn)
    ImageButton musicPlayerShareBtn;
    @Bind(R.id.musiccontrolbtn)
    LinearLayout musiccontrolbtn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private MusicsPresenter mMusicsPresenter = null;
    private String mMusicsCollectId = UriHelper.URL_MUSICS_LIST_CHANNEL_ID;

    private List<MusicsListEntity> mPlayListData;

    private boolean isPlaying = false;
    private static final int BLUR_RADIUS = 100;

    private PlayBundleBroadCast mBundleBroadCast;
    private PlayPositionBroadCast mPositionBroadCast;
    private PlaySecondProgressBroadCast mSecondProgressBroadCast;
    private SweetSheet mSweetSheet;
    ArrayList<MenuEntity> menuEntities = new ArrayList<>();
    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        ButterKnife.bind(this, view);

//        Bitmap bitmap = ImageBlurManager.doBlurJniArray(BitmapFactory.decodeResource(getResources(),
//                R.drawable.player_bg),
//                BLUR_RADIUS,
//                false);
//        musicsPlayerBackground.setImageBitmap(bitmap);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.player_bg);
        musicsPlayerBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                                                              @Override
                                                                              public boolean onPreDraw() {
                                                                                  blur(bitmap, musicsPlayerBackground);
                                                                                  return true;
                                                                              }
                                                                          }
        );
        initViewsAndEvents();
        initSheet();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
        mBundleBroadCast = new PlayBundleBroadCast();
        IntentFilter bundleFilter = new IntentFilter();
        bundleFilter.addAction(Constants.ACTION_MUSIC_BUNDLE_BROADCAST);

        mContext.registerReceiver(mBundleBroadCast, bundleFilter);

        mPositionBroadCast = new PlayPositionBroadCast();
        IntentFilter posFilter = new IntentFilter();
        posFilter.addAction(Constants.ACTION_MUSIC_CURRENT_PROGRESS_BROADCAST);

        mContext.registerReceiver(mPositionBroadCast, posFilter);

        mSecondProgressBroadCast = new PlaySecondProgressBroadCast();
        IntentFilter secondProgressFilter = new IntentFilter();
        secondProgressFilter.addAction(Constants.ACTION_MUSIC_SECOND_PROGRESS_BROADCAST);

        mContext.registerReceiver(mSecondProgressBroadCast, secondProgressFilter);

        mContext.startService(new Intent(mContext, MusicPlayService.class));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mMusicsPresenter != null) {
            mMusicsPresenter.onStopPlay();
        }
        mContext.unregisterReceiver(mBundleBroadCast);
        mContext.unregisterReceiver(mPositionBroadCast);
        mContext.unregisterReceiver(mSecondProgressBroadCast);
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onURLEvent(URLEvent urlEvent)
    {
        MusicsListEntity musicsListEntity=urlEvent.getMusicsListEntity();
        mMusicsPresenter.getUrl(musicsListEntity);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMusicInfoEvent(UpdateMusicInfoEvent updateMusicInfoEvent)
    {
        MusicsListEntity musicsListEntity=updateMusicInfoEvent.getMusicsListEntity();
        musicsPlayerName.setText(musicsListEntity.getSingername());
        musicsPlayerSongerName.setText(musicsListEntity.getFilename());
    }
    protected void initViewsAndEvents() {
//        Bitmap bitmap = ImageBlurManager.doBlurJniArray(BitmapFactory.decodeResource(getResources(),
//                R.drawable.player_bg),
//                BLUR_RADIUS,
//                false);
//        mBackgroundImage.setImageBitmap(bitmap);
        mMusicsPresenter = new MusicsPresenterImpl(mContext, this
        );
//        mPlayerDiscView.resetNeedleAngle();
        mPlayerDiscView.loadAlbumCover(R.drawable.aly_default);
        musicsPlayerPlayCtrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mMusicsPresenter.onPausePlay();
                } else {
                    mMusicsPresenter.onRePlay();
                }
//                playDefaultMusic();
            }
        });

        musicsPlayerPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicsPresenter.onNextClick();
            }
        });

        musicsPlayerPlayPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicsPresenter.onPrevClick();
            }
        });
        mMusicsPresenter.loadListData(TAG_LOG, mMusicsCollectId, Constants.EVENT_REFRESH_DATA);
    }

    private void initSheet() {
        // SweetSheet 控件,根据 rl 确认位置
        mSweetSheet = new SweetSheet(musicsPlayerContainer);

        //设置数据源 (数据源支持设置 menuEntities 数组,也支持从菜单中获取)
        mSweetSheet.setMenuList(menuEntities);
        //根据设置不同的 Delegate 来显示不同的风格.
        mSweetSheet.setDelegate(new RecyclerViewDelegate(true));
        //根据设置不同Effect 来显示背景效果BlurEffect:模糊效果.DimEffect 变暗效果
        mSweetSheet.setBackgroundEffect(new BlurEffect(8));
        //设置点击事件
        mSweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                //即时改变当前项的颜色
                menuEntities.get(position).titleColor = 0xff5823ff;
                ((RecyclerViewDelegate) mSweetSheet.getDelegate()).notifyDataSetChanged();

                //根据返回值, true 会关闭 SweetSheet ,false 则不会.
                Toast.makeText(mContext, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.getInstance().doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
//        System.out.println(System.currentTimeMillis() - startMs + "ms");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        EventBus.getDefault().register(this);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }


//        @Override
//        public void inProgress(float progress)
//        {
//            Log.e("ChatActivity", "inProgress:" + progress);
////            mProgressBar.setProgress((int) (100 * progress));
//        }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    private void initMusicList(ResponseMusicsListentity data)
    {
        for (MusicsListEntity musicsListEntity : data.getSong())
        {
            MenuEntity menuEntity1 = new MenuEntity();
            menuEntity1.iconId = R.drawable.ic_add_white_48dp;
            menuEntity1.titleColor = 0xff000000;
            menuEntity1.title = musicsListEntity.getFilename();
            menuEntities.add(menuEntity1);
        }

    }

    @Override
    public void refreshMusicsList(ResponseMusicsListentity data) {
        if (null != data) {
            mPlayListData = data.getSong();
            if (null != mPlayListData && !mPlayListData.isEmpty()) {
                initMusicList(data);
                MusicPlayService.refreshMusicList(mPlayListData);

//                mMusicsPresenter.onStartPlay();
            }
        }
    }

    @Override
    public void addMoreMusicsList(ResponseMusicsListentity data) {
        if (null != data) {
            mPlayListData = data.getSong();
            if (null != mPlayListData && !mPlayListData.isEmpty()) {
                MusicPlayService.refreshMusicList(mPlayListData);
                mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_NEXT));
            }
        }
    }

    @Override
    public void rePlayMusic() {
        isPlaying = true;
        mPlayerDiscView.rePlay();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_pause_selector);
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_REPLAY));
    }

    @Override
    public void startPlayMusic() {
        isPlaying = true;
        mPlayerDiscView.startPlay();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_pause_selector);
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_PLAY));
    }

    @Override
    public void stopPlayMusic() {
        isPlaying = false;
        mPlayerDiscView.pause();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_play_selector);
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_STOP));
    }

    @Override
    public void pausePlayMusic() {
        isPlaying = false;
        mPlayerDiscView.pause();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_play_selector);
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_PAUSE));
    }

    @Override
    public void playNextMusic() {
        isPlaying = true;
        mPlayerDiscView.next();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_play_selector);
        mMusicsPresenter.loadListData(TAG_LOG, mMusicsCollectId, Constants.EVENT_LOAD_MORE_DATA);
    }

    @Override
    public void playPrevMusic() {
        isPlaying = true;
        mPlayerDiscView.next();
        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_play_selector);
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_MUSIC_PREV));
    }

    @Override
    public void seekToPosition(int position) {
        mContext.sendBroadcast(new Intent(MusicPlayState.ACTION_SEEK_TO).putExtra(Constants.KEY_PLAYER_SEEK_TO_PROGRESS, position));
    }

    @Override
    public void refreshPageInfo(MusicsListEntity entity, int totalDuration) {
//        toggleShowLoading(false, null);
//        mPlayerDiscView.startPlay();
//        musicsPlayerPlayCtrlBtn.setImageResource(R.drawable.btn_pause_selector);
//
//        if (null != entity) {
//            mTitle.setText(entity.getTitle());
//            StringBuilder sb = new StringBuilder();
//            sb.append("--\t");
//            sb.append(entity.getArtist());
//            sb.append("\t--");
//            mSonger.setText(sb.toString().trim());
//        }
//
        if (totalDuration > 0) {
            musicsPlayerSeekbar.setMax(totalDuration);
        }
//
//        String imageUrl = entity.getPicture();
//        if (!CommonUtils.isEmpty(imageUrl)) {
//            mPlayerDiscView.loadAlbumCover(imageUrl);
//            ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    Bitmap bitmap = ImageBlurManager.doBlurJniArray(loadedImage, BLUR_RADIUS, false);
//                    mBackgroundImage.setImageBitmap(bitmap);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//
//                }
//            });
//        } else {
//            Bitmap bitmap = ImageBlurManager.doBlurJniArray(BitmapFactory.decodeResource(getResources(),
//                    R.drawable.player_bg),
//                    BLUR_RADIUS,
//                    false);
//            mBackgroundImage.setImageBitmap(bitmap);
//        }
//
        String totalTime = CommonUtils.convertTime(totalDuration);
        if (null != totalTime && !TextUtils.isEmpty(totalTime)) {
            musicsPlayerTotalTime.setText(totalTime);
        }
        musicsPlayerName.setText(entity.getSingername());
        musicsPlayerSongerName.setText(entity.getFilename());
    }

    @Override
    public void refreshPlayProgress(int progress) {
        Log.i(TAG, "refreshPlayProgress: " + progress);

        musicsPlayerSeekbar.setProgress(progress);
        String currentTime = CommonUtils.convertTime(progress);
        if (null != currentTime && !TextUtils.isEmpty(currentTime)) {

            musicsPlayerCurrentTime.setText(currentTime);
        }
    }

    @Override
    public void refreshPlaySecondProgress(int progress) {
        musicsPlayerSeekbar.setSecondaryProgress(progress);
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showException(String msg) {

    }

    @Override
    public void showNetError() {

    }

    @OnClick({R.id.music_player_love_btn, R.id.music_player_list_btn, R.id.music_player_mode_btn, R.id.music_player_share_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_player_love_btn:
                break;
            case R.id.music_player_list_btn:

                if (mSweetSheet.isShow())
                    mSweetSheet.dismiss();
                else mSweetSheet.show();

                break;
            case R.id.music_player_mode_btn:
                break;
            case R.id.music_player_share_btn:
                break;
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class PlayBundleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null != action && !TextUtils.isEmpty(action)) {
                if (action.equals(Constants.ACTION_MUSIC_BUNDLE_BROADCAST)) {
                    Bundle extras = intent.getExtras();
                    if (null != extras) {
                        MusicsListEntity entity = extras.getParcelable(Constants.KEY_MUSIC_PARCELABLE_DATA);
                        int totalDuration = extras.getInt(Constants.KEY_MUSIC_TOTAL_DURATION);
                        mMusicsPresenter.refreshPageInfo(entity, totalDuration);
                    }
                }
            }
        }

    }

    private class PlayPositionBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null != action && !TextUtils.isEmpty(action)) {
                if (action.equals(Constants.ACTION_MUSIC_CURRENT_PROGRESS_BROADCAST)) {
                    Bundle extras = intent.getExtras();
                    if (null != extras) {
                        int progress = extras.getInt(Constants.KEY_MUSIC_CURRENT_DUTATION);

                        mMusicsPresenter.refreshProgress(progress);
                    }
                }
            }

        }
    }

    private class PlaySecondProgressBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null != action && !TextUtils.isEmpty(action)) {
                if (action.equals(Constants.ACTION_MUSIC_SECOND_PROGRESS_BROADCAST)) {
                    Bundle extras = intent.getExtras();
                    if (null != extras) {
                        int progress = extras.getInt(Constants.KEY_MUSIC_SECOND_PROGRESS);

                        mMusicsPresenter.refreshSecondProgress(progress);
                    }
                }
            }

        }
    }
}

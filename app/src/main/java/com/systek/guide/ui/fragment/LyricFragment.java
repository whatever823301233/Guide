package com.systek.guide.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.base.Constants;
import com.systek.guide.util.LogUtil;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.base.BaseFragment;
import com.systek.guide.adapter.LyricAdapter;
import com.systek.guide.util.lyric.LyricDownloadManager;
import com.systek.guide.util.lyric.LyricLoadHelper;
import com.systek.guide.util.lyric.LyricSentence;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LyricFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LyricFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LyricFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView lvLyric;
    private TextView tvContent;
    private LyricLoadHelper mLyricLoadHelper;
    private LyricAdapter mLyricAdapter;
    private Exhibit exhibit;
    private String lyricUrl;
    private String museumId;
    private String exhibitContent;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LyricFragment() {
        // Required empty public constructor
    }

    public static LyricFragment newInstance() {
        LyricFragment fragment = new LyricFragment();
        Bundle args = new Bundle();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mLyricLoadHelper = new LyricLoadHelper();
        mLyricLoadHelper.setLyricListener(mLyricListener);
        mLyricAdapter = new LyricAdapter(getHoldingActivity());

    }

    private LyricLoadHelper.LyricListener mLyricListener = new LyricLoadHelper.LyricListener() {

        @Override
        public void onLyricLoaded(List<LyricSentence> lyricSentences, int index) {
            if (lyricSentences != null) {
                //LogUtil.i(TAG, "onLyricLoaded--->歌词句子数目=" + lyricSentences.size() + ",当前句子索引=" + index);
                if(mLyricAdapter==null){
                    mLyricAdapter=new LyricAdapter(getActivity());
                    lvLyric.setAdapter(mLyricAdapter);
                }
                mLyricAdapter.setLyric(lyricSentences);
                mLyricAdapter.setCurrentSentenceIndex(index);
                mLyricAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onLyricSentenceChanged(int indexOfCurSentence) {
            if(mLyricAdapter==null){return;}
            mLyricAdapter.setCurrentSentenceIndex(indexOfCurSentence);
            mLyricAdapter.notifyDataSetChanged();
            lvLyric.smoothScrollToPositionFromTop(indexOfCurSentence, lvLyric.getHeight() / 2, 500);

        }
    };




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        lvLyric=(ListView)view.findViewById(R.id.lvLyric);
        tvContent=(TextView)view.findViewById(R.id.tvContent);
        lvLyric.setAdapter(mLyricAdapter);
        lvLyric.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lyric;
    }


    public void setLyricUrl(String lyricUrl) {
        this.lyricUrl = lyricUrl;
    }

    public void refreshLyricContent() {
        loadLyricByHand();
        if(tvContent!=null&&!TextUtils.isEmpty(exhibitContent)){
            tvContent.setText(exhibitContent);
        }
    }



    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }



    public void loadLyricByHand() {

        if(TextUtils.isEmpty(lyricUrl)){return;}
        try{
            if(mLyricLoadHelper==null){
                mLyricLoadHelper=new LyricLoadHelper();
                mLyricLoadHelper.setLyricListener(mLyricListener);
            }
            if(mLyricAdapter==null){
                mLyricAdapter = new LyricAdapter(getActivity());
                if(lvLyric!=null){
                    lvLyric.setAdapter(mLyricAdapter);
                }
            }
            String name = lyricUrl.replaceAll("/", "_");
            // 取得歌曲同目录下的歌词文件绝对路径
            String lyricFilePath = Constants.LOCAL_PATH+museumId+"/"+ name;
            File lyricFile = new File(lyricFilePath);
            if (lyricFile.exists()) {
                // 本地有歌词，直接读取
                mLyricLoadHelper.loadLyric(lyricFilePath);
            } else {
                //mIsLyricDownloading = true;
                // 尝试网络获取歌词
                //LogUtil.i("ZHANG", "loadLyric()--->本地无歌词，尝试从网络获取");
                new LyricDownloadAsyncTask().execute(lyricUrl);
            }
        }catch (Exception e){
            LogUtil.e("",e);
        }
    }


    public void onSwitchLyric() {
        if(tvContent.getVisibility()==View.VISIBLE){
            tvContent.setVisibility(View.GONE);
            lvLyric.setVisibility(View.VISIBLE);
        }else{
            tvContent.setVisibility(View.VISIBLE);
            lvLyric.setVisibility(View.GONE);
        }
    }
    public void notifyTime(long currentPosition) {
        if(mLyricLoadHelper!=null){
            mLyricLoadHelper.notifyTime(currentPosition);
        }
    }

    public void setExhibitContent(String exhibitContent) {
        this.exhibitContent = exhibitContent;
    }

    private  class LyricDownloadAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            LyricDownloadManager mLyricDownloadManager = new LyricDownloadManager(getActivity());
            // 从网络获取歌词，然后保存到本地
            String savePath=Constants.LOCAL_PATH + museumId ;//+ "/"
            String lyricName=params[0];
            // 返回本地歌词路径
            // mIsLyricDownloading = false;
            return mLyricDownloadManager.searchLyricFromWeb(lyricName, savePath);
        }

        @Override
        protected void onPostExecute(String lyricSavePath) {
            // Log.i(TAG, "网络获取歌词完毕，歌词保存路径:" + result);
            // 读取保存到本地的歌曲
            if(mLyricLoadHelper!=null){
                mLyricLoadHelper.loadLyric(lyricSavePath);
            }
        }
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}

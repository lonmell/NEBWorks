package com.krafte.nebworks.ui.community;

import androidx.appcompat.app.AppCompatActivity;

public class OwnerFeedAddActivity extends AppCompatActivity {
//    private ActivityOwnerFeedAddBinding binding;
//    private final static String TAG = "WorkCommunityWriteAcitivy";
//    Context mContext;
//
//    // shared 저장값
//    PreferenceHelper shardpref;
//    String USER_INFO_NO = "";
//    String USER_INFO_ID = "";
//    String USER_INFO_NAME = "";
//    String USER_INFO_AUTH = "";
//    int SELECTED_POSITION = 0;
//    String store_insurance = "";
//    String USER_INFO_NICKNAME = "";
//    String place_id = "";
//    String feed_id = "";
//
//    //Other
//    DateCurrent dc = new DateCurrent();
//    DBConnection dbConnection = new DBConnection();
//    GetResultData resultData = new GetResultData();
//    PageMoveClass pm = new PageMoveClass();
//    RetrofitConnect rc = new RetrofitConnect();
//    Dlog dlog = new Dlog();
//
//    Drawable icon_off;
//    Drawable icon_on;
//    int like_state = 0;
//    String CommTitle = "";
//    String CommContnets = "";
//    int nickname_select = 1;
//
//    ArrayList<String> SetCategoryList;
//
//    //--EditData
//    String state_txt = "";
//    String write_id_txt = "";
//    String writer_name_txt = "";
//    String title_txt = "";
//    String contents_txt = "";
//    String write_date_txt = "";
//    String view_cnt = "";
//    String like_cnt = "";
//    String boardkind = "";
//    String category = "";
//    String user_input_name = "";
//    String write_nickname = "";
//    String feed_img = "";
//
//    boolean chng_icon = false;
//    Calendar cal;
//    String format = "yyyy-MM-dd";
//    SimpleDateFormat sdf = new SimpleDateFormat(format);
//    String toDay = "";
//    String Year = "";
//    String Month = "";
//    String Day = "";
//    String getYMPicker = "";
//    String bYear = "";
//    String bMonth = "";
//    String bDay = "";
//
//    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_community_add);
//        binding = ActivityOwnerFeedAddBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mContext = this;
//        dlog.DlogContext(mContext);
//
//        icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
//        icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);
//
//        shardpref = new PreferenceHelper(mContext);
//        USER_INFO_NO = shardpref.getString("USER_INFO_NO", "");
//        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
//        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
//        USER_INFO_NICKNAME = shardpref.getString("USER_INFO_NICKNAME", "");
//        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
//        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
//
//        place_id = shardpref.getString("place_id", "");
//        feed_id = shardpref.getString("place_id", "");
//
//        /*작성자가 수정 버튼을 눌렀을때 가져옴*/
//        state_txt = shardpref.getString("state", "");
//        write_id_txt = shardpref.getString("write_id", "");
//        write_nickname = shardpref.getString("write_nickname", "");
//        dlog.i("--------------------------WorkCommunityWriteAcitivy--------------------------");
//        user_input_name = USER_INFO_NAME;
//        if (write_id_txt.equals(USER_INFO_ID)) {
//            if (state_txt.equals("EditFeed")) {
//                //작성자의 글 수정
//                feed_id = shardpref.getString("feed_id", "");
//                writer_name_txt = shardpref.getString("writer_name", "");
//                title_txt = shardpref.getString("title", "");
//                contents_txt = shardpref.getString("contents", "");
//                write_date_txt = shardpref.getString("write_date", "");
//                view_cnt = shardpref.getString("view_cnt", "");
//                like_cnt = shardpref.getString("like_cnt", "");
//                boardkind = shardpref.getString("boardkind", "");
//                category = shardpref.getString("category", "");
//                feed_img = shardpref.getString("feed_img", "");
//
//                binding.writeTitle.setText(title_txt);
//                binding.writeContents.setText(contents_txt);
//                binding.addcommunityBtn.setText("수정");
//                binding.selectBoardkindTxt.setText(boardkind);
//                binding.selectCategoryTxt.setText("#" + category);
//
//                if (!writer_name_txt.equals(write_nickname)) {
//                    //닉네임x
//                    nickname_select = 1;
//                } else {
//                    //닉네임o
//                    nickname_select = 2;
//                }
//                if (nickname_select == 1) {
//                    user_input_name = USER_INFO_NAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
//                } else {
//                    user_input_name = USER_INFO_NICKNAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
//                }
//
//                if (write_nickname.equals(USER_INFO_NICKNAME)) {
//                    //닉네임으로 선택하고 작성했을 경우
//                    nickname_select = 2;
//                    user_input_name = USER_INFO_NICKNAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
//                } else if (write_nickname.equals(USER_INFO_NAME)) {
//                    nickname_select = 1;
//                    user_input_name = USER_INFO_NAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
//                }
//            }
//        } else {
//            user_input_name = USER_INFO_NAME;
//            dlog.i("user_input_name : " + user_input_name);
//            binding.addcommunityBtn.setText("등록");
//        }
//
//        dlog.i("1 state_txt : " + state_txt);
//        dlog.i("1 state_txt : " + state_txt);
//        dlog.i("place_id : " + place_id);
//        dlog.i("feed_id : " + feed_id);
//        dlog.i("writer_name_txt : " + writer_name_txt);
//        dlog.i("title_txt : " + title_txt);
//        dlog.i("contents_txt : " + contents_txt);
//        dlog.i("write_date_txt : " + write_date_txt);
//        dlog.i("view_cnt : " + view_cnt);
//        dlog.i("like_cnt : " + like_cnt);
//        dlog.i("boardkind : " + boardkind);
//        dlog.i("category : " + category);
//        dlog.i("write_id_txt : " + write_id_txt);
//        dlog.i("write_nickname : " + write_nickname);
//        dlog.i("USER_INFO_NAME : " + USER_INFO_NAME);
//        dlog.i("USER_INFO_NICKNAME : " + USER_INFO_NICKNAME);
//        dlog.i("2 state_txt : " + state_txt);
//        dlog.i("DataCheck() : " + DataCheck());
//        dlog.i("feed_img : " + feed_img);
//        //-------------------------------
//        setBtnEvent();
//        dlog.i("-----------------------------------------------------------------------------");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @SuppressLint("LongLogTag")
//    private void setBtnEvent() {
//        boardkind = "사장님페이지";
//        binding.selectBoardkindTxt.setText("사장님페이지");
//
//        binding.selectCategoryTxt.setOnClickListener(v -> {
//            shardpref.putInt("SelectKind", 2);
//            SelectStringBottomSheet ssb = new SelectStringBottomSheet();
//            ssb.show(getSupportFragmentManager(), "selectBoardkindTxt");
//            ssb.setOnItemClickListener(new SelectStringBottomSheet.OnItemClickListener() {
//                @Override
//                public void onItemClick(View v, String category) {
//                    binding.selectCategoryTxt.setText(category);
//                }
//            });
//        });
//
//        binding.location.setOnClickListener(v -> {
//            shardpref.putInt("SelectKind", 3);
//            SelectStringBottomSheet ssb = new SelectStringBottomSheet();
//            ssb.show(getSupportFragmentManager(), "selectBoardkindTxt");
//            ssb.setOnItemClickListener(new SelectStringBottomSheet.OnItemClickListener() {
//                @Override
//                public void onItemClick(View v, String category) {
//                    binding.selectCategoryTxt.setText(category);
//                }
//            });
//        });
//
//        Calendar c = Calendar.getInstance();
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Year = String.valueOf(year);
//                Month = String.valueOf(month+1);
//                Day = String.valueOf(dayOfMonth);
//                Day = Day.length()==1?"0"+Day:Day;
//                Month = Month.length()==1?"0"+Month:Month;
//                binding.overDate.setText(year +"-" + Month + "-" + Day);
//                getYMPicker = binding.overDate.getText().toString().substring(0,7);
//                shardpref.putString("FtoDay",toDay);
//            }
//        }, mYear, mMonth, mDay);
//
//        binding.overDate.setOnClickListener(view -> {
//            if (binding.overDate.isClickable()) {
//                datePickerDialog.show();
//            }
//        });
//
//        binding.backBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
//            intent.putExtra("data", "작성을 종료하시겠습니까?\n편집한 내용이 저장되지 않습니다.");
//            intent.putExtra("flag", "작성여부");
//            intent.putExtra("left_btn_txt", "계속작성");
//            intent.putExtra("right_btn_txt", "작성종료");
//            mContext.startActivity(intent);
//            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        });
//
//
//        binding.addcommunityBtn.setOnClickListener(v -> {
//            dlog.i("3 state_txt : " + state_txt);
//            if (DataCheck()) {
//                if (state_txt.equals("EditFeed")) {
//                    EditStroeNoti();
//                } else {
//                    AddFeedCommunity();
//                }
//            } else {
//                Toast.makeText(this, "입력되지 않은 값이 있습니다.", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        binding.writerName.setOnClickListener(v -> {
//            if (USER_INFO_NICKNAME.isEmpty()) {
//                shardpref.putString("returnPage", TAG);
//
//                Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
//                intent.putExtra("data", "저장된 닉네임이 없습니다\n닉네임설정으로 이동합니다.");
//                intent.putExtra("flag", "닉네임없음");
//                intent.putExtra("left_btn_txt", "확인");
//                intent.putExtra("right_btn_txt", "취소");
//                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            } else {
//                if (nickname_select == 1) {
//                    nickname_select = 2;
//                    user_input_name = USER_INFO_NICKNAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
//                } else {
//                    nickname_select = 1;
//                    user_input_name = USER_INFO_NAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
//                }
//            }
//
//        });
//
//    }
//
//    @SuppressLint("LongLogTag")
//    private boolean DataCheck() {
//
//        CommTitle = binding.writeTitle.getText().toString();
//        CommContnets = binding.writeContents.getText().toString();
//
//        dlog.i("-----------------DataCheck------------------");
//        dlog.i("CommTitle : " + CommTitle);
//        dlog.i("CommContnets : " + CommContnets);
//        dlog.i("boardkind : " + boardkind);
//        dlog.i("category : " + category);
//        dlog.i("user_input_name : " + user_input_name);
//        dlog.i("-----------------DataCheck------------------");
//
//        if (!CommTitle.isEmpty() && !CommContnets.isEmpty() && !user_input_name.isEmpty()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    //피드 게시글 업로드
//    public void AddFeedCommunity() {
////        String title = binding.writeTitle.getText().toString();
////        String content = binding.writeContents.getText().toString();
////        boardkind = binding.selectBoardkindTxt.getText().toString();
////        category = binding.selectCategoryTxt.getText().toString();
////        String over_date = binding.overDate.getText().toString();
////        dlog.i("-----AddStroeNoti Check-----");
////        dlog.i("title : " + title);
////        dlog.i("content : " + content);
////        dlog.i("BoardKind : " + boardkind);
////        dlog.i("category : " + category);
////        dlog.i("-----AddStroeNoti Check-----");
////
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl(OwnerFeedInsertInterface.URL)
////                .addConverterFactory(ScalarsConverterFactory.create())
////                .build();
////        OwnerFeedInsertInterface api = retrofit.create(OwnerFeedInsertInterface.class);
////        Call<String> call = api.getData(place_id, USER_INFO_ID, title, content, "", "", "", boardkind, category, over_date);
////        call.enqueue(new Callback<String>() {
////            @SuppressLint({"LongLogTag", "SetTextI18n"})
////            @Override
////            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
////                dlog.i("AddStroeNoti Callback : " + response.body());
////                if (response.isSuccessful() && response.body() != null) {
////                    runOnUiThread(() -> {
////                        if (response.isSuccessful() && response.body() != null) {
////                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
////                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
////                            try {
////                                if (!response.body().equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
////                                    Toast_Nomal("게시글 저장이 완료되었습니다.");
////                                    pm.CommunityActivity(mContext);
////                                }
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    });
////                }
////            }
////
////            @SuppressLint("LongLogTag")
////            @Override
////            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
////                dlog.e("에러1 = " + t.getMessage());
////            }
////        });
//    }
//
//    public void EditStroeNoti() {
////        String title = binding.writeTitle.getText().toString();
////        String content = binding.writeContents.getText().toString();
////        boardkind = binding.selectBoardkindTxt.getText().toString();
////        category = binding.selectCategoryTxt.getText().toString();
////
////        dlog.i("-----AddStroeNoti Check-----");
////        dlog.i("title : " + title);
////        dlog.i("content : " + content);
////        dlog.i("Profile Url : " + ProfileUrl);
////        dlog.i("BoardKind : " + boardkind);
////        dlog.i("category : " + category);
////        dlog.i("-----AddStroeNoti Check-----");
////
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl(FeedNotiEditInterface.URL)
////                .addConverterFactory(ScalarsConverterFactory.create())
////                .build();
////        FeedNotiEditInterface api = retrofit.create(FeedNotiEditInterface.class);
////        Call<String> call = api.getData(feed_id, title, content, "", ProfileUrl, "", "", "", category, boardkind);
////        call.enqueue(new Callback<String>() {
////            @SuppressLint({"LongLogTag", "SetTextI18n"})
////            @Override
////            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
////                dlog.i("AddStroeNoti Callback : " + response.body());
////                if (response.isSuccessful() && response.body() != null) {
////                    runOnUiThread(() -> {
////                        if (response.isSuccessful() && response.body() != null) {
////                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
////                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
////                            try {
////                                if (!response.body().equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
////                                    if (!ProfileUrl.isEmpty()) {
////                                        saveBitmapAndGetURI();
////                                    }
////                                    Toast.makeText(mContext, "매장 공지사항 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
////                                    pm.FeedList(mContext);
////                                }
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    });
////                }
////            }
////
////            @SuppressLint("LongLogTag")
////            @Override
////            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
////                dlog.e("에러1 = " + t.getMessage());
////            }
////        });
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        shardpref.remove("writer_name");
//        shardpref.remove("write_nickname");
//        shardpref.remove("title");
//        shardpref.remove("contents");
//        shardpref.remove("write_date");
//        shardpref.remove("view_cnt");
//        shardpref.remove("like_cnt");
//        shardpref.remove("categoryItem");
//        shardpref.remove("TopFeed");
//    }
//
//    private void BackMove() {
////        if(state_txt.equals("EditFeed")){
////            Intent intent = new Intent(this, WorkCommunityDetailActivity.class);
////            startActivity(intent);
////            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        }else{
////            Intent intent = new Intent(this, WorkCommunityActivity.class);
////            startActivity(intent);
////            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        }
//    }
//
//
//    @Override
//    public void onBackPressed() {
////       super.onBackPressed();
//        BackMove();
//    }
//
//    //이미지 업로드에 필요한 소스 START
//    @SuppressLint("LongLogTag")
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//
//    public void Toast_Nomal(String message) {
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
//        TextView toast_textview = layout.findViewById(R.id.toast_textview);
//        toast_textview.setText(String.valueOf(message));
//        Toast toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
//        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
//        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
//        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
//        toast.setView(layout);
//        toast.show();
//    }
}

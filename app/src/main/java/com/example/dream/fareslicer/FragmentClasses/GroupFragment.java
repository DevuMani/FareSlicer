package com.example.dream.fareslicer.FragmentClasses;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dream.fareslicer.Activities.NewGroup;
import com.example.dream.fareslicer.Activities.RegistrationActivity;
import com.example.dream.fareslicer.AdapterClasses.GroupListAdapter;
import com.example.dream.fareslicer.BeanClasses.GroupData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountOutput;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ch.halcyon.squareprogressbar.SquareProgressBar;
import ch.halcyon.squareprogressbar.utils.PercentStyle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private static final String TAG = "Group Fragment" ;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout no_data_layout,data_layout;
    private CardView  all_settled_up;

    private FloatingActionButton fab;
    //    private TextView total_currency, total_text,total_amount;remove
    private TextView home_total_amount,own_currency,owe_currency,owe_amount,own_amount;
    //    private LinearLayout  total_text_layout;//remove
    private LinearLayout heading_layout, layout_you_owe, layout_you_own;
    private RelativeLayout fragment_totalLayout,main_Layout;
    private SwipeRefreshLayout swipeRefreshLayout;
//    SquareProgressBar squareProgressBar;//square progressbar, remove because didn't work

    private String user_id; //To store shared preference value
    private String currency="";

    private ArrayList<GroupData> groupList;
    private LinearLayoutManager linearLayoutManager;
    private GroupListAdapter adapter;
    private GroupData groupData;


    public GroupFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("User",MODE_PRIVATE);
        this.user_id =sharedPreferences.getString("user_id","");
//        this.currency=getResources().getString(R.string.currency);//remove currency
        this.currency=sharedPreferences.getString("user_currency","");
    }

    @Override
    public void onResume() {
        super.onResume();

        if(user_id.equals(""))
        {
            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);

        return view;

    }

    private void initialize(View rootView) {

        swipeRefreshLayout=rootView.findViewById(R.id.home_swiperefresh);

//        squareProgressBar=rootView.findViewById(R.id.sprogressbar);//square progressbar
        progressBar=rootView.findViewById(R.id.home_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView=rootView.findViewById(R.id.home_recyclerView);
        main_Layout=rootView.findViewById(R.id.main_Layout);
        main_Layout.setVisibility(View.GONE);

        fab=rootView.findViewById(R.id.home_fab);
        fragment_totalLayout=rootView.findViewById(R.id.fragment_totalLayout);

        all_settled_up=rootView.findViewById(R.id.all_settled_up);
        layout_you_owe=rootView.findViewById(R.id.layout_you_owe);
        layout_you_own=rootView.findViewById(R.id.layout_you_own);

        home_total_amount=rootView.findViewById(R.id.home_total_amount);

        owe_currency=rootView.findViewById(R.id.home_heading_owe_currency);
        owe_amount=rootView.findViewById(R.id.owe_amount);
        own_currency=rootView.findViewById(R.id.home_heading_own_currency);
        own_amount=rootView.findViewById(R.id.own_amount);

        heading_layout=rootView.findViewById(R.id.home_transaction_heading_layout); //Main Layout for showing total amount details
        heading_layout.setVisibility(View.GONE);
//        lv_color_for_CardView=rootView.findViewById(R.id.home_color_cardview);

        no_data_layout=rootView.findViewById(R.id.home_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);
        data_layout=rootView.findViewById(R.id.home_transaction_layout);
        data_layout.setVisibility(View.GONE);
//        linearHead=rootView.findViewById(R.id.linear_head_text);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {

                    fab.hide();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        showAmountHome();

//        setRecyclerViewData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(),NewGroup.class);
                intent.putExtra("g_id","");
                startActivity(intent);
            }
        });

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }

                }
        );

    }

    private void showAmountHome() {

        ShowAmountInput showAmountInput =new ShowAmountInput();
        showAmountInput.setUserId(user_id);

        Call<ShowAmountOutput> call=RetrofitClient.getInstance().getApi().showAmountHome(showAmountInput);

        call.enqueue(new Callback<ShowAmountOutput>() {
            @Override
            public void onResponse(Call<ShowAmountOutput> call, Response<ShowAmountOutput> response) {

                main_Layout.setVisibility(View.VISIBLE); //To make the main layout visible in the response of any one of the api calls
                swipeRefreshLayout.setRefreshing(false);

                if (response.code() == 200) {

                    setGroupList();

                    heading_layout.setVisibility(View.VISIBLE); //to make this layout visible only if response is successfull

                    ShowAmountOutput showAmountOutput = response.body();

                    String amount=showAmountOutput.getAmount();
                    amount=String.format("%.2f", Double.parseDouble(amount));

                    Double owed = Math.abs(Double.parseDouble(showAmountOutput.getOwe()));
                    String owe=String.format("%.2f", owed);

                    String own=showAmountOutput.getOwn();
                    own=String.format("%.2f", Double.parseDouble(own));

                    if(amount.equals("0.00"))
                    {
//                        total_text_layout.setVisibility(View.GONE); remove
                        all_settled_up.setVisibility(View.VISIBLE);
                        home_total_amount.setText(currency+" "+amount);
                        home_total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));
                    }
                    else
                    {

                        all_settled_up.setVisibility(View.GONE);
//                        total_text_layout.setVisibility(View.VISIBLE); remove
//                        total_currency.setText(" "+ currency+" ");


                        if (Double.parseDouble(amount)<0)//Amount you owe
                        {
                            //To remove -ve sign
                            Double x = Math.abs(Double.parseDouble(amount));
                            amount=""+x;

                            home_total_amount.setText(currency+" "+amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.warning_red));
//                            lv_color_for_CardView.setBackground(getResources().getDrawable(R.drawable.cardview_outline_red)); to give border color for caredview

                            //square progressbar remove
//                            squareProgressBar.showProgress(true);
//                            PercentStyle percentStyle = new PercentStyle(Paint.Align.CENTER, 20, true);
//                            percentStyle.setCustomText(amount);
//                            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(R.drawable.add_person));
//                            squareProgressBar.setImageBitmap(bitmap);
//                            squareProgressBar.setPercentStyle(percentStyle);
//
//                            squareProgressBar.setColor("#00BD98");
//                            squareProgressBar.setHoloColor(R.color.warning_red);
//                            percentStyle.setTextColor(getResources().getColor(R.color.caribbean_green));

                        }
                        else if (Double.parseDouble(amount)>=0)//Amount you own
                        {
//

                            home_total_amount.setText(currency+" "+amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));
//                            lv_color_for_CardView.setBackground(getResources().getDrawable(R.drawable.cardview_outline_green));to give border color for caredview

                            //square progressbar remove
//                            squareProgressBar.showProgress(true);
//                            PercentStyle percentStyle = new PercentStyle(Paint.Align.CENTER, 20, true);
//                            percentStyle.setCustomText(amount);
//                            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(R.drawable.add_person));
//                            squareProgressBar.setImageBitmap(bitmap);
//                            squareProgressBar.setPercentStyle(percentStyle);
//
//                            squareProgressBar.setColor("#00BD98");
//                            squareProgressBar.setHoloColor(R.color.warning_red);
//                            percentStyle.setTextColor(getResources().getColor(R.color.caribbean_green));

                            //remove
//                            total_text.setTextColor(getResources().getColor(R.color.caribbean_green));
//                            total_currency.setTextColor(getResources().getColor(R.color.caribbean_green));

                        }
                    }


//                    Amount Owe(to give)

                    layout_you_owe.setVisibility(View.VISIBLE);
                    owe_currency.setText(" "+ currency+" ");//hardcoding

                    //To remove -ve sign
                    Double x = Math.abs(Double.parseDouble(owe));
                    owe=""+x;

                    owe_amount.setText(owe);
                    owe_currency.setTextColor(getResources().getColor(R.color.warning_red));
                    owe_amount.setTextColor(getResources().getColor(R.color.warning_red));

//                  Amount own(to get)

                    layout_you_own.setVisibility(View.VISIBLE);
                    own_currency.setText(" "+ currency+" ");

                    own_amount.setText(own);
                    own_currency.setTextColor(getResources().getColor(R.color.caribbean_green));
                    own_amount.setTextColor(getResources().getColor(R.color.caribbean_green));

                }
                else {

                    heading_layout.setVisibility(View.GONE);

                    //Warning snackbar
                    Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();


                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Selection",e.getMessage());

                    }
                }


            }
            @Override
            public void onFailure(Call<ShowAmountOutput> call, Throwable t) {
                Log.e("Selection",t.getMessage());

                //Warning snackbar
                Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void setGroupList() {

        GroupListFetchInput input=new GroupListFetchInput();
        input.setUserId(user_id);

        groupList= new ArrayList<>();

        Call<GroupListFetchResult> call=RetrofitClient.getInstance().getApi().groupListFetch(input);

        call.enqueue(new Callback<GroupListFetchResult>() {
            @Override
            public void onResponse(Call<GroupListFetchResult> call, Response<GroupListFetchResult> response) {


                if (response.code()==200) {


                    GroupListFetchResult groupListFetchResult = response.body();

                    List<GroupListFetchOutput> groupListFetchOutputs = groupListFetchResult.getOutput();

                    for (int i = 0; i < groupListFetchOutputs.size(); i++) {

                        GroupListFetchOutput output = groupListFetchOutputs.get(i);

                        groupData = new GroupData(output.getGroupId(), output.getGroupName(), output.getAmount(), output.getOwe(), output.getOwn());

                        groupList.add(groupData);
                    }


                    if (groupList.isEmpty()) {

                        no_data_layout.setVisibility(View.VISIBLE);
                        heading_layout.setVisibility(View.GONE);
                        data_layout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);


                    } else {
                        no_data_layout.setVisibility(View.GONE);
                        heading_layout.setVisibility(View.VISIBLE);
                        data_layout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
//                        adapter.notifyDataSetChanged();
                        setRecyclerViewData();

                    }

                }
                else
                {
                    //Warning snackbar
                    Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Selection",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<GroupListFetchResult> call, Throwable t) {

                //Warning snackbar
                Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();

//                Toast.makeText(getContext(), "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Selection", t.getMessage());
            }
        });
    }

   private void myUpdateOperation() {

        showAmountHome();
//        setGroupList();
//        setRecyclerViewData();

    }

    private void setRecyclerViewData() {

        linearLayoutManager=new LinearLayoutManager(getContext());
        adapter = new GroupListAdapter(getContext(), groupList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }


}

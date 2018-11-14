package com.example.dream.fareslicer;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayout no_data_layout,data_layout;

//    JsonObjectRequest jsonObjectRequest;
//    RequestQueue requestQueue;
//
//    String url=SyncStateContract.Constants.BASE_URL+"leave_request_api.php?";
////String url=Constants.BASE_URL+"leave_request1.php?";
//    String parameter="";
    int tab_position=0;
//
//    JsonObjectRequest count_jsonObjectRequest;
//    RequestQueue count_requestQueue;
//    String count_url=SyncStateContract.Constants.BASE_URL+"leave_count.php?";
    String count_parameter="";

    String cLeaves="";
    String aLeaves="";

    FloatingActionButton fab;
    TextView owe_amount,amount_own;
    LinearLayout linearHead;

    private String userID;



    public HomeFragment() {
        // Required empty public constructor

    }



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        int tab_position = getArguments().getInt("tab");

        this.tab_position=tab_position;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("User",MODE_PRIVATE);
        this.userID=sharedPreferences.getString("user_phone","");
        userID="0";

//        if(!userID.equals(""))
//        {
//            leaveCall();
//            countCall();
//        }
//        else
//        {
//            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(getActivity(),Login.class);
//            startActivity(intent);
//            getActivity().finish();
//        }

//
    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sp=getActivity().getSharedPreferences("UserLogin",MODE_PRIVATE);
//        userID=sp.getString("userID","");
        if(!userID.equals(""))
        {
//            leaveCall();
//            countCall();
        }
        else
        {
            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(rootView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(),NewGroup.class);
                startActivity(intent);
            }
        });

//         Inflate the layout for this fragment
        return rootView;

    }
    private void initialize(View rootView) {

        progressBar=rootView.findViewById(R.id.home_progressBar);
        recyclerView=rootView.findViewById(R.id.home_recyclerView);

        fab=rootView.findViewById(R.id.home_fab);
        owe_amount=rootView.findViewById(R.id.owe_amount);
        amount_own=rootView.findViewById(R.id.amount_own);

        no_data_layout=rootView.findViewById(R.id.home_no_transaction_layout);
        data_layout=rootView.findViewById(R.id.home_transaction_layout);

        linearHead=rootView.findViewById(R.id.linear_head_text);
        Boolean head=true;
        if(head==true)
        {
            no_data_layout.setVisibility(View.VISIBLE);
            data_layout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            fab.show();

        }
        else
        {
            no_data_layout.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            fab.hide();
        }


    }

//    private void countCall() {
//
//        count_parameter="userID="+userID;
//
//        System.out.println("Count URL "+count_url+count_parameter);
//
//        count_requestQueue= Volley.newRequestQueue(getActivity());
//
//        count_jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, count_url + count_parameter, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                GsonBuilder gsonBuilder=new GsonBuilder();
//                Gson gson=gsonBuilder.create();
//
//
//                LeaveCount leaveCount=gson.fromJson(response.toString(),LeaveCount.class);
//                CountStatus countStatus=leaveCount.getStatus();
//
//                if(countStatus.getCode().equals("200"))
//                {
//                    CountCode countCode=leaveCount.getCode();
//
//                    cLeaves=countCode.getCurrentLeaves();
//                    aLeaves=countCode.getLeaveAvailable();
//
//                    currentLeaves.setText(cLeaves);
//                    availableLeaves.setText(aLeaves);
////                    Toast.makeText(LeaveActivity.this, "Current Leaves"+currentLeaves, Toast.LENGTH_SHORT).show();
////                    Toast.makeText(LeaveActivity.this, "Leaves Available"+availableLeaves, Toast.LENGTH_SHORT).show();
//                }
//                else if(countStatus.getCode().equals("500"))
//                {
//                    Log.e("Count Failed",countStatus.getMessage());
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "Count Parse Error", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                error.printStackTrace();
//
//            }
//        });
//
//        count_jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        count_requestQueue.add(count_jsonObjectRequest);
//    }
//
//
//    private void leaveCall()
//    {
//
//        parameter="userID="+userID+"&leaveRequestStatus="+status;
//
//        System.out.println("Leave URL "+url+parameter);
////        Log.e("URL ",url);
//        //instantiate request queue
//        requestQueue= Volley.newRequestQueue(getActivity());
//
//        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url + parameter, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                GsonBuilder gsonBuilder=new GsonBuilder();
//                Gson gson=gsonBuilder.create();
//
//                //Prints object data
//                System.out.println("-------------------------------------------Leave JSON---------------------------------------------------------");
//
//                System.out.println("Leave response string "+response.toString());
//                LeaveList list=gson.fromJson(response.toString(),LeaveList.class);
//
//                Status status=list.getStatus();
//
//                if(status.getCode().equals("200"))
//                {
//                    List<Code> codeList=list.getCode();
//                    LeaveListAdapter adapter=new LeaveListAdapter(getActivity(),codeList);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    recyclerView.setAdapter(adapter);
//                    imageView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                }
//                else if(status.getCode().equals("204"))
//                {
//                    imageView.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                }
//                else if(status.getCode().equals("400"))
//                {
//                    Log.e("Error:::",status.getCode()+" "+status.getMessage());
//                }
//                else
//                {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(), "Leave List Couldn't Parse Error", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                error.printStackTrace();
//
//            }
//        });
//
//
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        //      Add the request to request queue
//        requestQueue.add(jsonObjectRequest);
//    }
}

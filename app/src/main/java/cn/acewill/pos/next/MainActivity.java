package cn.acewill.pos.next;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.httpserver.PosServer;
import cn.acewill.pos.next.model.table.Table;
import cn.acewill.pos.next.model.table.Table_Table;

public class MainActivity extends AppCompatActivity {
    @BindView( R.id.startbutton)
    Button startButton;

    @BindView( R.id.editText)
    TextView editText;



    private PosServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化 DBFlow
        FlowManager.init(new FlowConfig.Builder(getApplicationContext()).build());
        //初始化 BufferKnife
        ButterKnife.bind(this);
    }


    @OnClick(R.id.startHttpServer)
    public void startHttpServer() {
//        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
//                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//        editText.setText("Please access! http://" + formatedIpAddress + ":" + 9999);
//        editText.setText("如果在模拟器中运行，请用adb forward命令映射到电脑的端口，然后直接访问电脑IP即可");
//
//        AsyncTask task = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//                try {
//                    server = new PosServer(9999);
//                    server.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        };
//
//        task.execute();
    }

    @OnClick(R.id.login)
    public void login() {
//        CXJSystemService systemService = new CXJSystemService("localhost");
//        systemService.login("1", "acewill", new ResultCallback<User>() {
//            @Override
//            public void onResult(User result) {
//                editText.setText("登录成功");
//            }
//
//            @Override
//            public void onError(PosServiceException e) {
//                editText.setText("登录失败");
//            }
//        });
    }

    @OnClick(R.id.startbutton)
    public void saveProduct() {
        String s = "click at %s %d";

        Log.d("ff",String.format(s,"start button", 4));

        Table p = new Table();
        p.setName( "Wiki");
        p.setCapacity(1);
//        p.setBookingStatus(TableStatus.BookingStatus.IN_USE);
//        p.setStatus(TableStatus.UseStatus.DIRTY);



        p = new Table();
        p.setName( "Wiki2");
        p.setCapacity(2);
//        p.setBookingStatus(TableStatus.BookingStatus.NOT_BOOKING);
//        p.setStatus(TableStatus.UseStatus.EMPTY);
        p.save();

        Table product = new Select().from(Table.class).where(Table_Table.name.eq("Wiki")).querySingle();
        product = new Select().from(Table.class).where(Table_Table.name.eq("Wikixxx")).querySingle();
        product = new Select().from(Table.class).where(Table_Table.name.eq("Wiki2")).querySingle();


    }
}
